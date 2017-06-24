package algorithmexecuter.booleanexpression;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.interfaces.IdentifierValidator;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecuter.enums.ComparingOperators;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.enums.Keywords;
import algorithmexecuter.enums.Operators;
import algorithmexecuter.enums.ReservedChars;
import algorithmexecuter.exceptions.BooleanExpressionException;
import algorithmexecuter.exceptions.CompileExceptionTexts;
import exceptions.ExpressionException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BooleanExpression implements AbstractExpression {

    public abstract boolean evaluate(Map<String, AbstractExpression> valuesMap);

    @Override
    public Set<String> getContainedVars() {
        Set<String> vars = new HashSet<>();
        addContainedVars(vars);
        return vars;
    }
    
    @Override
    public Set<String> getContainedIndeterminates() {
        Set<String> vars = new HashSet<>();
        addContainedIndeterminates(vars);
        return vars;
    }
    
    public static BooleanExpression build(String input, IdentifierValidator validator, 
            Map<String, IdentifierType> typesMap) throws BooleanExpressionException {

        /*
         Prioritäten: |: 0, &: 1, !: 2, ~: 3, Vergleiche, Boolsche Konstante oder Variable: 4.
         */
        int priority = 4;
        int breakpoint = -1;
        int bracketCounter = 0;
        int inputLength = input.length();
        String currentEnding;

        if (input.equals("")) {
            throw new BooleanExpressionException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
        }

        for (int i = 1; i <= inputLength - 1; i++) {
            currentEnding = input.substring(0, inputLength - i + 1);

            // Öffnende und schließende Klammern zählen.
            if (currentEnding.endsWith("(") && bracketCounter == 0) {
                throw new BooleanExpressionException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
            }

            if (currentEnding.endsWith(")")) {
                bracketCounter++;
            }
            if (currentEnding.endsWith("(")) {
                bracketCounter--;
            }

            if (bracketCounter != 0) {
                continue;
            }
            // Aufteilungspunkt finden; zunächst wird nach |, &, !, Vergleichsoperator gesucht 
            // breakpoint gibt den Index in formula an, wo die Formel aufgespalten werden soll.
            if (currentEnding.endsWith(Operators.OR.getValue()) && priority > 1) {
                priority = 0;
                breakpoint = inputLength - i;
            } else if (currentEnding.endsWith(Operators.AND.getValue()) && priority > 2) {
                priority = 1;
                breakpoint = inputLength - i;
            } else if (currentEnding.endsWith(Operators.NOT.getValue()) && priority > 3) {
                priority = 2;
                breakpoint = inputLength - i;
            } else if ((currentEnding.endsWith(ComparingOperators.EQUALS.getValue()) 
                    || currentEnding.endsWith(ComparingOperators.NOT_EQUALS.getValue())
                    || currentEnding.endsWith(ComparingOperators.GREATER.getValue()) 
                    || currentEnding.endsWith(ComparingOperators.GREATER_OR_EQUALS.getValue())
                    || currentEnding.endsWith(ComparingOperators.SMALLER.getValue()) 
                    || currentEnding.endsWith(ComparingOperators.SMALLER_OR_EQUALS.getValue())) 
                    && priority > 0) {
                priority = 3;
                breakpoint = inputLength - i;
            }
        }

        if (bracketCounter > 0) {
            throw new BooleanExpressionException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
        }

        // Aufteilung, falls eine Elementaroperation (|, &, !) vorliegt
        if (priority <= 2) {
            String inputLeft = input.substring(0, breakpoint);
            String inputRight = input.substring(breakpoint + 1, inputLength);

            if (inputLeft.equals("") && priority != 1) {
                throw new BooleanExpressionException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
            }
            if (inputRight.equals("")) {
                throw new BooleanExpressionException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
            }

            switch (priority) {
                case 0:
                    return new BooleanBinaryOperation(build(inputLeft, validator, typesMap), 
                            build(inputRight, validator, typesMap), BooleanBinaryOperationType.OR);
                case 1:
                    return new BooleanBinaryOperation(build(inputLeft, validator, typesMap), 
                            build(inputRight, validator, typesMap), BooleanBinaryOperationType.AND);
                default:    //Passiert zwar nicht, aber trotzdem!
                    return null;
            }
        }

        if (priority == 2 && breakpoint == 0) {
            /*
             Falls eine Negation vorliegt, dann muss breakpoint == 0 sein.
             Falls formula von der Form !xyz... ist, dann soll xyz... gelesen
             werden und dann die entsprechende Negation zurückgegeben
             werden.
             */
            String inputLeft = input.substring(1, inputLength);
            return new BooleanNegation(build(inputLeft, validator, typesMap));
        }

        if (priority >= 3) {
            // WICHTIG: Verglichen wird bei (Matrizen-) Ausdrücken stets mit der Methode equivalent().
            ComparingOperators comparisonType = null;
            if (containsOperatorExactlyOneTime(input, ComparingOperators.EQUALS)) {
                comparisonType = ComparingOperators.EQUALS;
            } else if (containsOperatorExactlyOneTime(input, ComparingOperators.GREATER)) {
                comparisonType = ComparingOperators.GREATER;
            } else if (containsOperatorExactlyOneTime(input, ComparingOperators.GREATER_OR_EQUALS)) {
                comparisonType = ComparingOperators.GREATER_OR_EQUALS;
            } else if (containsOperatorExactlyOneTime(input, ComparingOperators.SMALLER)) {
                comparisonType = ComparingOperators.SMALLER;
            } else if (containsOperatorExactlyOneTime(input, ComparingOperators.SMALLER_OR_EQUALS)) {
                comparisonType = ComparingOperators.SMALLER_OR_EQUALS;
            }
            
            if (comparisonType != null) {
                // Es kommt genau ein Vergleichsoperator in input vor.
                String[] comparison = input.split(comparisonType.getValue());
                /* 
                Operatoren wie ">=", ">", ... machen nur bei gewöhnlichen 
                Ausdrücken Sinn. "==" dagegen macht auch bei Matrizenausdrücken Sinn.
                 */
                AbstractExpression left = parseAbstractExpression(comparison[0], validator, typesMap);
                AbstractExpression right = parseAbstractExpression(comparison[1], validator, typesMap);
                if (left != null && right != null) {
                    return new BooleanBuildingBlock(left, right, comparisonType);
                }
            }
        }

        // Falls kein binärer Operator und die Formel die Form (...) hat -> Klammern beseitigen
        if (priority == 4 && input.substring(0, 1).equals(ReservedChars.OPEN_BRACKET.getValue())
                && input.substring(inputLength - 1, inputLength).equals(ReservedChars.CLOSE_BRACKET.getValue())) {
            return build(input.substring(1, inputLength - 1), validator, typesMap);
        }

        // Falls der Ausdruck eine logische Konstante ist (false, true)
        if (priority == 4) {
            if (input.equals(Keywords.FALSE.getValue())) {
                return new BooleanConstant(false);
            }
            if (input.equals(Keywords.TRUE.getValue())) {
                return new BooleanConstant(true);
            }
        }

        // Falls der Ausdruck eine Variable ist
        if (priority == 4) {
            if (validator.isValidIdentifier(input)) {
                return BooleanVariable.create(input);
            }
        }

        throw new BooleanExpressionException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
    }

    private static boolean containsOperatorExactlyOneTime(String input, ComparingOperators op) {
        return input.contains(op.getValue()) && input.length() - input.replaceAll(op.getValue(), "").length() == op.getValue().length();
    }
    
    private static AbstractExpression parseAbstractExpression(String input, IdentifierValidator validator, Map<String, IdentifierType> typesMap) {
        // In valuesMap werden nur Variablen aufgenommen.
        AbstractExpression parsedInput;
        try {
            parsedInput = Expression.build(input, validator);
            if (doesValuesMapContainAllVarsOfCorrectType(parsedInput, typesMap)) {
                return parsedInput;
            }
        } catch (ExpressionException e) {
        }
        try {
            parsedInput = BooleanExpression.build(input, validator, typesMap);
            if (doesValuesMapContainAllVarsOfCorrectType(parsedInput, typesMap)) {
                return parsedInput;
            }
        } catch (BooleanExpressionException e) {
        }
        try {
            /*
            Hier darf build() rekursiv angewendet werden, da input hier eine kleinere Länge
            besitzt, als der input im vorherigen Aufruf.
            */
            parsedInput = MatrixExpression.build(input, validator, validator);
            if (doesValuesMapContainAllVarsOfCorrectType(parsedInput, typesMap)) {
                return parsedInput;
            }
        } catch (ExpressionException e) {
        }
        try {
            parsedInput = BooleanExpression.build(input, validator, typesMap);
            if (doesValuesMapContainAllVarsOfCorrectType(parsedInput, typesMap)) {
                return parsedInput;
            }
        } catch (BooleanExpressionException e) {
        }
        return null;
    }
    
    private static boolean doesValuesMapContainAllVarsOfCorrectType(AbstractExpression abstrExpr, Map<String, IdentifierType> typesMap) {
        Set<String> vars = abstrExpr.getContainedVars();
        IdentifierType type = IdentifierType.identifierTypeOf(abstrExpr);
        for (String var : vars) {
            if (!typesMap.containsKey(var)) {
                return false;
            }
            if (!type.isSameOrGeneralTypeOf(typesMap.get(var))) {
                return false;
            }
        }
        return true;
    }

    public abstract void addContainedIdentifier(Set<String> vars);

    public boolean isEquiv() {
        return this instanceof BooleanBinaryOperation && ((BooleanBinaryOperation) this).getType().equals(BooleanBinaryOperationType.EQUIVALENCE);
    }

    public boolean isOr() {
        return this instanceof BooleanBinaryOperation && ((BooleanBinaryOperation) this).getType().equals(BooleanBinaryOperationType.OR);
    }

    public boolean isAnd() {
        return this instanceof BooleanBinaryOperation && ((BooleanBinaryOperation) this).getType().equals(BooleanBinaryOperationType.AND);
    }

    public boolean isBuildingBlock() {
        return this instanceof BooleanBuildingBlock;
    }

    public BooleanExpression not() {
        return new BooleanNegation(this);
    }

    public BooleanExpression equiv(BooleanExpression boolExpr) {
        return new BooleanBinaryOperation(this, boolExpr, BooleanBinaryOperationType.EQUIVALENCE);
    }

    public BooleanExpression or(BooleanExpression boolExpr) {
        return new BooleanBinaryOperation(this, boolExpr, BooleanBinaryOperationType.OR);
    }

    public BooleanExpression and(BooleanExpression boolExpr) {
        return new BooleanBinaryOperation(this, boolExpr, BooleanBinaryOperationType.AND);
    }

}
