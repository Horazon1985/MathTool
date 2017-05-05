package algorithmexecutor.command.condition;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.interfaces.IdentifierValidator;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.enums.ComparingOperators;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.enums.Operators;
import algorithmexecutor.enums.ReservedChars;
import algorithmexecutor.exceptions.BooleanExpressionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.model.Algorithm;
import exceptions.ExpressionException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BooleanExpression {

    public abstract boolean evaluate(Map<String, AbstractExpression> valuesMap);

    public static BooleanExpression build(String input, IdentifierValidator validator) throws BooleanExpressionException {

        input = convertOperators(input.replaceAll(" ", "").toLowerCase());

        /*
         Prioritäten: |: 0, &: 1, !: 2, ~: 3, Vergleiche, Boolsche Konstante oder Variable: 4.
         */
        int priority = 4;
        int breakpoint = -1;
        int bracketCounter = 0;
        int inputLength = input.length();
        String currentChar;

        if (input.equals("")) {
            throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        for (int i = 1; i <= inputLength - 1; i++) {
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
            // Aufteilungspunkt finden; zunächst wird nach |, &, !, ~ gesucht 
            // breakpoint gibt den Index in formula an, wo die Formel aufgespalten werden soll.
             if (currentChar.equals(Operators.OR.getValue()) && priority > 1) {
                priority = 0;
                breakpoint = inputLength - i;
            } else if (currentChar.equals(Operators.AND.getValue()) && priority > 2) {
                priority = 1;
                breakpoint = inputLength - i;
            } else if (currentChar.equals(Operators.NOT.getValue()) && priority > 3) {
                priority = 2;
                breakpoint = inputLength - i;
            } else if (currentChar.equals(ComparingOperators.EQUALS.getConvertedValue()) && priority > 0) {
                priority = 3;
                breakpoint = inputLength - i;
            }
        }

        if (bracketCounter > 0) {
            throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        // Aufteilung, falls eine Elementaroperation (|, &, !) vorliegt
        if (priority <= 2) {
            String inputLeft = input.substring(0, breakpoint);
            String inputRight = input.substring(breakpoint + 1, inputLength);

            if (inputLeft.equals("") && priority != 1) {
                throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            if (inputRight.equals("")) {
                throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
            }

            switch (priority) {
                case 0:
                    return new BooleanBinaryOperation(build(inputLeft, validator), build(inputRight, validator), BooleanBinaryOperationType.EQUIVALENCE);
                case 1:
                    return new BooleanBinaryOperation(build(inputLeft, validator), build(inputRight, validator), BooleanBinaryOperationType.OR);
                case 2:
                    return new BooleanBinaryOperation(build(inputLeft, validator), build(inputRight, validator), BooleanBinaryOperationType.AND);
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
            return new BooleanNegation(build(inputLeft, validator));
        }

        // Der folgende Fall wird zuerst behandelt: "==" als Vergleich zwischen zwei (Matrizen-)Ausdrücken.
        if (priority >= 3) {
            /* 
            Falls der Ausdruck ein Vergleich von Ausdrücken (Instanzen von Expression) ist.
            WICHTIG: Verglichen wird stets mit der Methode equivalent().
             */
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
                String[] comparison = input.split(comparisonType.getConvertedValue());
                /* 
                Operatoren wie ">=", ">", ... machen nur bei gewöhnlichen 
                Ausdrücken Sinn. "==" dagegen macht auch bei Matrizenausdrücken Sinn.
                 */
                if (comparisonType.getConvertedValue().equals(ComparingOperators.EQUALS.getConvertedValue())) {
                    try {
                        MatrixExpression matExprLeft = MatrixExpression.build(comparison[0], validator, validator);
                        MatrixExpression matExprRight = MatrixExpression.build(comparison[1], validator, validator);
                        return new BooleanBuildingBlock(matExprLeft, matExprRight);
                    } catch (ExpressionException e) {
                    }
                }

                try {
                    Expression exprLeft = Expression.build(comparison[0], validator);
                    Expression exprRight = Expression.build(comparison[1], validator);
                    return new BooleanBuildingBlock(exprLeft, exprRight, comparisonType);
                } catch (ExpressionException e) {
                }
            }
        }
        
        // Falls kein binärer Operator und die Formel die Form (...) hat -> Klammern beseitigen
        if (priority == 4 && input.substring(0, 1).equals(ReservedChars.OPEN_BRACKET.getValue())
                && input.substring(inputLength - 1, inputLength).equals(ReservedChars.CLOSE_BRACKET.getValue())) {
            return build(input.substring(1, inputLength - 1), validator);
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

        throw new BooleanExpressionException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    private static String convertOperators(String input) {
        String convertedInput = input;
        for (ComparingOperators op : ComparingOperators.values()) {
            convertedInput = convertedInput.replaceAll(op.getValue(), op.getConvertedValue());
        }
        return convertedInput;
    }

    private static boolean containsOperatorExactlyOneTime(String input, ComparingOperators op) {
        return input.contains(op.getConvertedValue()) && input.length() - input.replaceAll(op.getConvertedValue(), "").length() == 1;
    }

    public Set<String> getContainedIndeterminates() {
        Set<String> vars = new HashSet<>();
        addContainedIdentifier(vars);
        return vars;
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
