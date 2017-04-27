package algorithmexecutor.command.condition;

import abstractexpressions.interfaces.IdentifierValidator;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.enums.ReservedChars;
import algorithmexecutor.exceptions.BooleanExpressionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import java.util.HashSet;
import java.util.Set;

public abstract class BooleanExpression {

    public final BooleanConstant TRUE = new BooleanConstant(true);
    public final BooleanConstant FALSE = new BooleanConstant(false);

    public abstract boolean evaluate();

    public static BooleanExpression build(String input, IdentifierValidator validator, Algorithm alg) throws BooleanExpressionException {

        input = input.replaceAll(" ", "").toLowerCase();

        /*
         Prioritäten: ==: 0, |: 1, &: 2, !: 3, Vergleiche: 4, Boolsche Konstante: 5.
         */
        int priority = 5;
        int breakpoint = -1;
        int bracketCounter = 0;
        int inputLength = input.length();
        String currentChar;

        if (input.equals("")) {
            throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        for (int i = 1; i <= inputLength; i++) {
            currentChar = input.substring(inputLength - i, inputLength - i + 1);

            // Öffnende und schließende Klammern zählen.
            if (currentChar.equals("(") && bracketCounter == 0) {
                throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
            }

            if (currentChar.equals(")")) {
                bracketCounter++;
            }
            if (currentChar.equals("(")) {
                bracketCounter--;
            }

            if (bracketCounter != 0) {
                continue;
            }
            //Aufteilungspunkt finden; zunächst wird nach =, >, |, &, ! gesucht 
            //breakpoint gibt den Index in formula an, wo die Formel aufgespalten werden soll.
            if (currentChar.equals("=") && priority > 0) {
                priority = 0;
                breakpoint = inputLength - i;
            } else if (currentChar.equals("|") && priority > 1) {
                priority = 1;
                breakpoint = inputLength - i;
            } else if (currentChar.equals("&") && priority > 2) {
                priority = 2;
                breakpoint = inputLength - i;
            } else if (currentChar.equals("1") && priority > 3) {
                priority = 3;
                breakpoint = inputLength - i;
            }
        }

        if (bracketCounter > 0) {
            throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        // Aufteilung, falls eine Elementaroperation (=, |, &) vorliegt
        if (priority <= 2) {
            String inputLeft = input.substring(0, breakpoint);
            String inputRight = input.substring(breakpoint + 1, inputLength);

            if ((inputLeft.equals("")) && (priority != 1)) {
                throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            if (inputRight.equals("")) {
                throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
            }

            switch (priority) {
                case 0:
                    return new BooleanBinaryOperation(build(inputLeft, validator, alg), build(inputRight, validator, alg), BooleanBinaryOperationType.EQUIVALENCE);
                case 1:
                    return new BooleanBinaryOperation(build(inputLeft, validator, alg), build(inputRight, validator, alg), BooleanBinaryOperationType.OR);
                case 2:
                    return new BooleanBinaryOperation(build(inputLeft, validator, alg), build(inputRight, validator, alg), BooleanBinaryOperationType.AND);
                default:    //Passiert zwar nicht, aber trotzdem!
                    return null;
            }
        }
        if (priority == 3 && breakpoint == 0) {
            /*
             Falls eine Negation vorliegt, dann muss breakpoint == 0 sein.
             Falls formula von der Form !xyz... ist, dann soll xyz... gelesen
             werden und dann die entsprechende Negation zurückgegeben
             werden.
             */
            String inputLeft = input.substring(1, inputLength);
            return new BooleanNegation(build(inputLeft, validator, alg));
        }

        //Falls kein binärer Operator und die Formel die Form (...) hat -> Klammern beseitigen
        if (priority == 4 && input.substring(0, 1).equals(ReservedChars.OPEN_BRACKET.getValue()) 
                && input.substring(inputLength - 1, inputLength).equals(ReservedChars.CLOSE_BRACKET.getValue())) {
            return build(input.substring(1, inputLength - 1), validator, alg);
        }

        //Falls der Ausdruck eine logische Konstante ist (false, true)
        if (priority == 4) {
            if (input.equals(Keywords.FALSE.getValue())) {
                return new BooleanConstant(false);
            }
            if (input.equals(Keywords.TRUE.getValue())) {
                return new BooleanConstant(true);
            }
        }

        //Falls der Ausdruck eine Variable ist
        if (priority == 4) {
            if (validator.isValidIdentifier(input)) {
                Identifier identifierWithLogicalExpression = Identifier.createIdentifier(alg, input, IdentifierTypes.BOOLEAN_EXPRESSION);
                return new BooleanBuildingBlock(identifierWithLogicalExpression);
            }
        }

        throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    public Set<String> getContainedIndeterminates() {
        Set<String> vars = new HashSet<>();
        addContainedIdentifier(vars);
        return vars;
    }

    public abstract void addContainedIdentifier(Set<String> vars);

}
