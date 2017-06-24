package algorithmexecuter.model.command;

import abstractexpressions.expression.classes.Expression;
import algorithmexecuter.AlgorithmCompiler;
import algorithmexecuter.enums.FixedAlgorithmNames;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.exceptions.ExecutionExceptionTexts;
import algorithmexecuter.model.identifier.Identifier;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.Signature;
import exceptions.EvaluationException;

public class VoidCommand extends AlgorithmCommand {

    private final String name;
    private final Identifier[] identifiers;

    public VoidCommand(String name, Identifier... identifiers) {
        this.name = name;
        this.identifiers = identifiers;
    }

    public String getName() {
        return name;
    }

    public Identifier[] getIdentifiers() {
        return identifiers;
    }

    @Override
    public String toString() {
        return "VoidCommand[name = " + this.name + ", identifiers = " + this.identifiers + "]";
    }

    public Signature getSignature() {
        IdentifierType[] identifierTypes = new IdentifierType[this.identifiers.length];
        for (int i = 0; i < this.identifiers.length; i++) {
            identifierTypes[i] = this.identifiers[i].getType();
        }
        return new Signature(null, this.name, identifierTypes);
    }

    @Override
    public Identifier execute(AlgorithmMemory scopeMemory) throws AlgorithmExecutionException, EvaluationException {
        // Zunächst über alle definierten Algorithmen iterieren.
        for (Algorithm alg : AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage()) {
            if (alg.getSignature().equals(getSignature()) && alg.getReturnType() == null) {
                // TO DO: Algorithmenparameter durch Bezeichnerwerte ersetzen. 
                alg.execute();
                return null;
            }
        }
        // Nun alle standardmäßig implementierten Void-Methoden ausprobieren.
        // "inc" = ++
        if (this.name.equals(FixedAlgorithmNames.INC.getValue()) && this.identifiers.length == 1 && this.identifiers[0].getType() == IdentifierType.EXPRESSION) {
            inc(this.identifiers[0]);
            scopeMemory.addToMemoryInRuntime(Identifier.createIdentifier(scopeMemory, this.name, IdentifierType.EXPRESSION));
            return null;
        }
        // "dec" = --
        if (this.name.equals(FixedAlgorithmNames.DEC.getValue()) && this.identifiers.length == 1 && this.identifiers[0].getType() == IdentifierType.EXPRESSION) {
            dec(this.identifiers[0]);
            scopeMemory.addToMemoryInRuntime(Identifier.createIdentifier(scopeMemory, this.name, IdentifierType.EXPRESSION));
            return null;
        }
        // "print" = Konsolenausgabe
        if (this.name.equals(FixedAlgorithmNames.DEC.getValue()) && this.identifiers.length == 1 && this.identifiers[0].getType() == IdentifierType.EXPRESSION) {
            return null;
        }
        throw new AlgorithmExecutionException(ExecutionExceptionTexts.AE_NO_SUCH_COMMAND);
    }

    //////////////////////// Liste vordefinierter Void-Befehle ////////////////////////
    public static void inc(Identifier identifier) throws AlgorithmExecutionException {
        if (identifier.getValue() != null) {
            identifier.setValue(((Expression) identifier.getValue()).add(Expression.ONE));
        }
        throw new AlgorithmExecutionException(ExecutionExceptionTexts.AE_NULL_POINTER, identifier.getName());
    }

    public static void dec(Identifier identifier) throws AlgorithmExecutionException {
        if (identifier.getValue() != null) {
            identifier.setValue(((Expression) identifier.getValue()).sub(Expression.ONE));
        }
        throw new AlgorithmExecutionException(ExecutionExceptionTexts.AE_NULL_POINTER, identifier.getName());
    }

}
