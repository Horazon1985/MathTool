package algorithmexecuter.model.command;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecuter.CompilerUtils;
import algorithmexecuter.booleanexpression.BooleanConstant;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.exceptions.constants.AlgorithmCompileExceptionIds;
import algorithmexecuter.exceptions.constants.AlgorithmExecutionExceptionIds;
import algorithmexecuter.model.identifier.Identifier;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.booleanexpression.BooleanExpression;
import algorithmexecuter.enums.AssignValueType;
import algorithmexecuter.enums.Operators;
import algorithmexecuter.enums.ReservedChars;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.Signature;
import algorithmexecuter.model.utilclasses.MalString;
import exceptions.EvaluationException;
import java.util.HashSet;
import java.util.Set;

public class AssignValueCommand extends AlgorithmCommand {

    private final Identifier identifierSrc;
    private final AbstractExpression targetExpression;
    private final MalString malString;
    private final AssignValueType type;
    private final Signature targetAlgorithmSignature;
    private final Identifier[] targetAlgorithmArguments;
    private Algorithm targetAlgorithm;

    public AssignValueCommand(Identifier identifierSrc, AbstractExpression targetExpression, AssignValueType type) throws AlgorithmCompileException {
        if (!areTypesCompatible(identifierSrc, IdentifierType.identifierTypeOf(targetExpression))) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_INCOMPATIBLE_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.targetExpression = targetExpression;
        this.malString = null;
        this.type = type;
        this.targetAlgorithmSignature = null;
        this.targetAlgorithmArguments = null;
    }

    public AssignValueCommand(Identifier identifierSrc, Signature targetAlgorithmSignature, Identifier[] targetAlgorithmArguments, AssignValueType type) throws AlgorithmCompileException {
        if (!areTypesCompatible(identifierSrc, targetAlgorithmSignature.getReturnType())) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_INCOMPATIBLE_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.targetExpression = null;
        this.malString = null;
        this.type = type;
        this.targetAlgorithmSignature = targetAlgorithmSignature;
        this.targetAlgorithmArguments = targetAlgorithmArguments;
    }

    public AssignValueCommand(Identifier identifierSrc, MalString malString, AssignValueType type) {
        this.identifierSrc = identifierSrc;
        this.targetExpression = null;
        this.malString = malString;
        this.type = type;
        this.targetAlgorithmSignature = null;
        this.targetAlgorithmArguments = null;
    }

    private boolean areTypesCompatible(Identifier identifierSrc, IdentifierType targetType) {
        return identifierSrc.getType().isSameOrGeneralTypeOf(targetType);
    }

    public AbstractExpression getTargetExpression() {
        return this.targetExpression;
    }

    public MalString getMalString() {
        return this.malString;
    }

    public Identifier getIdentifierSrc() {
        return this.identifierSrc;
    }

    public Signature getTargetAlgorithmSignature() {
        return targetAlgorithmSignature;
    }

    public Identifier[] getTargetAlgorithmArguments() {
        return targetAlgorithmArguments;
    }

    public Algorithm getTargetAlgorithm() {
        return targetAlgorithm;
    }

    public AssignValueType getType() {
        return type;
    }

    public void setTargetAlgorithm(Algorithm targetAlgorithm) {
        this.targetAlgorithm = targetAlgorithm;
    }

    @Override
    public String toString() {
        String command = "AssignValueCommand[type = " + this.type + ", identifierSrc = " + this.identifierSrc;
        // Typ: String.
        if (this.malString != null) {
            String values = "(";
            for (int i = 0; i < this.malString.getStringValues().length; i++) {
                if (this.malString.getStringValues()[i] instanceof String) {
                    values += "\"" + this.malString.getStringValues()[i] + "\"";
                } else {
                    values += this.malString.getStringValues()[i].toString();
                }
                if (i < this.malString.getStringValues().length - 1) {
                    values += ", ";
                }
            }
            values += ")";
            return command + ", stringValues = " + values + "]";
        }
        // Typ: (Abstrakter) Ausdruck.
        if (this.targetExpression != null) {
            return command + ", targetExpression = " + this.targetExpression + "]";
        }
        return command + ", targetAlgorithm = " + this.targetAlgorithmSignature.toString() + "]";
    }

    private Set<String> getVarsFromAlgorithmParameters(Algorithm alg) {
        Set<String> varsInAlgorithmSignature = new HashSet<>();
        AbstractExpression abstrExpr;
        for (Identifier identifier : alg.getInputParameters()) {
            abstrExpr = identifier.getValue();
            if (abstrExpr != null) {
                varsInAlgorithmSignature.addAll(abstrExpr.getContainedIndeterminates());
            }
        }
        return varsInAlgorithmSignature;
    }

    @Override
    public Identifier execute(AlgorithmMemory scopeMemory) throws AlgorithmExecutionException, EvaluationException {
        if (this.malString != null) {
            String resultValue = "";
            for (Object obj : this.malString.getStringValues()) {
                if (obj instanceof String) {
                    resultValue += obj;
                } else if (obj instanceof AbstractExpression) {
                    resultValue += simplifyTargetExpression((AbstractExpression) obj, scopeMemory);
                }
            }
            this.identifierSrc.setStringValue(new MalString(resultValue));
        } else if (this.targetExpression != null) {
            Set<String> varsInTargetExpr = this.targetExpression.getContainedIndeterminates();
            checkForUnknownIdentifier(scopeMemory, varsInTargetExpr);
            AbstractExpression targetExprSimplified = simplifyTargetExpression(this.targetExpression, scopeMemory);
            this.identifierSrc.setValue(targetExprSimplified);
        } else {
            this.targetAlgorithm.initInputParameter(this.targetAlgorithmArguments);
            Set<String> varsInTargetExpr = getVarsFromAlgorithmParameters(this.targetAlgorithm);
            checkForUnknownIdentifier(scopeMemory, varsInTargetExpr);
            this.identifierSrc.setAllValuesFromAnotherIdentifier(this.targetAlgorithm.execute());
        }
        scopeMemory.addToMemoryInRuntime(this.identifierSrc);
        return null;
    }

    private void checkForUnknownIdentifier(AlgorithmMemory scopeMemory, Set<String> varsInTargetExpr) throws AlgorithmExecutionException {
        for (String var : varsInTargetExpr) {
            if (!scopeMemory.containsIdentifier(var)) {
                throw new AlgorithmExecutionException(AlgorithmExecutionExceptionIds.AE_UNKNOWN_IDENTIFIER);
            }
        }
    }

    private AbstractExpression simplifyTargetExpression(AbstractExpression abstrExpr, AlgorithmMemory scopeMemory) throws EvaluationException {
        AbstractExpression targetExprSimplified;

        if (abstrExpr instanceof Expression) {
            Expression exprSimplified = (Expression) abstrExpr;
            for (Identifier identifier : scopeMemory.getMemory().values()) {
                if (identifier.getValue() instanceof Expression) {
                    exprSimplified = exprSimplified.replaceVariable(identifier.getName(), (Expression) identifier.getValue());
                }
            }
            targetExprSimplified = exprSimplified;
        } else if (abstrExpr instanceof LogicalExpression) {
            LogicalExpression logExprSimplified = (LogicalExpression) abstrExpr;
            for (Identifier identifier : scopeMemory.getMemory().values()) {
                if (identifier.getValue() instanceof LogicalExpression) {
                    logExprSimplified = logExprSimplified.replaceVariable(identifier.getName(), (LogicalExpression) identifier.getValue());
                }
            }
            targetExprSimplified = logExprSimplified;
        } else if (abstrExpr instanceof MatrixExpression) {
            MatrixExpression matExprSimplified = (MatrixExpression) abstrExpr;
            for (Identifier identifier : scopeMemory.getMemory().values()) {
                if (identifier.getValue() instanceof Expression) {
                    matExprSimplified = matExprSimplified.replaceVariable(identifier.getName(), (Expression) identifier.getValue());
                } else if (identifier.getValue() instanceof MatrixExpression) {
                    matExprSimplified = matExprSimplified.replaceMatrixVariable(identifier.getName(), (MatrixExpression) identifier.getValue());
                }
            }
            targetExprSimplified = matExprSimplified;
        } else {
            targetExprSimplified = new BooleanConstant(((BooleanExpression) abstrExpr).evaluate(CompilerUtils.extractValuesOfIdentifiers(scopeMemory)));
        }

        if (targetExprSimplified instanceof Expression) {
            return ((Expression) targetExprSimplified).simplify();
        } else if (targetExprSimplified instanceof LogicalExpression) {
            return ((LogicalExpression) targetExprSimplified).simplify();
        } else if (targetExprSimplified instanceof MatrixExpression) {
            return ((MatrixExpression) targetExprSimplified).simplify();
        }
        return targetExprSimplified;
    }

    @Override
    public String toCommandString() {
        String commandString = "";
        if (this.type == AssignValueType.NEW) {
            commandString += this.identifierSrc.getType().toString() + " ";
        }
        commandString += this.identifierSrc.getName() + Operators.DEFINE.getValue();
        if (this.malString != null) {
            for (int i = 0; i < this.malString.getStringValues().length; i++) {
                if (this.malString.getStringValues()[i] instanceof String) {
                    commandString += "\"" + this.malString.getStringValues()[i] + "\"";
                } else {
                    commandString += this.malString.getStringValues()[i].toString();
                }
                if (i < this.malString.getStringValues().length - 1) {
                    commandString += Operators.CONCAT.getValue();
                }
            }
        } else if (this.targetExpression != null) {
            commandString += this.targetExpression.toString();
        } else {
            commandString += this.targetAlgorithm.getName() + ReservedChars.OPEN_BRACKET.getStringValue();
            for (int i = 0; i < this.targetAlgorithmArguments.length; i++) {
                commandString += this.targetAlgorithmArguments[i].getName();
                if (i < this.targetAlgorithmArguments.length - 1) {
                    commandString += ReservedChars.ARGUMENT_SEPARATOR.getStringValue();
                }
            }
            commandString += ReservedChars.CLOSE_BRACKET.getStringValue();
        }
        return commandString + ReservedChars.LINE_SEPARATOR.getStringValue();
    }

}
