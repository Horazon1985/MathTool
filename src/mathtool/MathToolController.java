package mathtool;

import command.TypeCommand;
import exceptions.ExpressionException;
import expressionbuilder.Expression;
import java.awt.Color;
import java.util.HashSet;
import logicalexpressionbuilder.LogicalExpression;
import matrixexpressionbuilder.MatrixExpression;

public class MathToolController {

    /**
     * Gibt den i-ten geloggten Befehl zurück.
     */
    public static void showLoggedCommand(MathToolTextField mathToolTextField, int i) {
        if (!MathToolGUI.getListOfCommands().isEmpty() && MathToolGUI.getListOfCommands().get(i) != null) {
            mathToolTextField.setText(MathToolGUI.getListOfCommands().get(i));
        }
    }
    
    /**
     * Prüft, ob die Eingabe gültig ist. Bei gültiger (kompilierbarer) Eingabe
     * wird der Text schwary gefärbt, bei ungültiger Eingabe rot.
     */
    public static void checkInputValidity(MathToolTextField mathToolTextField) {

        // ToolTipText im Vorfeld entfernen (falls die Validierung doch korrekt ist).
        mathToolTextField.setToolTipText("");
        String s = mathToolTextField.getText().replaceAll(" ", "").toLowerCase();

        if (mathToolTextField.getText().equals("")) {
            mathToolTextField.setForeground(Color.black);
            return;
        }

        boolean validCommand = false;
        try {
            String[] commandName = Expression.getOperatorAndArguments(s);
            for (TypeCommand commandType : TypeCommand.values()) {
                validCommand = validCommand || commandName[0].equals(commandType.toString());
                if (validCommand) {
                    break;
                }
            }
            if (!validCommand) {
                // Dafür da, damit man in den Catch-Block springt.
                throw new ExpressionException("");
            }
            String[] params = Expression.getArguments(commandName[1]);
            MathCommandCompiler.getCommand(commandName[0], params);
        } catch (ExpressionException eCommand) {

            // Wenn der Befehlname gültig ist, aber der Befehl ungültig ist: entsprechende Fehlermeldung anzeigen.
            if (validCommand) {
                mathToolTextField.setForeground(Color.red);
                mathToolTextField.setToolTipText(eCommand.getMessage());
                return;
            }
            
            try {
                Expression.build(s, new HashSet<String>());
            } catch (ExpressionException eExpr) {
                try {
                    LogicalExpression.build(s, new HashSet<String>());
                } catch (ExpressionException eLogExpr) {
                    try {
                        MatrixExpression.build(s, new HashSet<String>());
                    } catch (ExpressionException eMatExpr) {
                        mathToolTextField.setForeground(Color.red);
                        mathToolTextField.setToolTipText(eMatExpr.getMessage());
                        return;
                    }
                }
            }
        }

        mathToolTextField.setForeground(Color.black);

    }
    
    /**
     * Berechnet für Operatoren und Befehle die Mindestanzahl der benötigten
     * Kommata bei einer gültigen Eingabe.
     */
    public static int getNumberOfComma(String operatorOrCommandName) {

        if (operatorOrCommandName.equals("diff")) {
            return 1;
        }
        if (operatorOrCommandName.equals("gcd")) {
            return 1;
        }
        if (operatorOrCommandName.equals("int")) {
            return 1;
        }
        if (operatorOrCommandName.equals("lcm")) {
            return 1;
        }
        if (operatorOrCommandName.equals("mod")) {
            return 1;
        }
        if (operatorOrCommandName.equals("prod")) {
            return 3;
        }
        if (operatorOrCommandName.equals("sum")) {
            return 3;
        }
        if (operatorOrCommandName.equals("taylor")) {
            return 3;
        }

        if (operatorOrCommandName.equals("plot2d")) {
            return 2;
        }
        if (operatorOrCommandName.equals("plot3d")) {
            return 4;
        }
        if (operatorOrCommandName.equals("plotcurve")) {
            return 2;
        }
        if (operatorOrCommandName.equals("plotpolar")) {
            return 2;
        }
        if (operatorOrCommandName.equals("regressionline")) {
            return 1;
        }
        if (operatorOrCommandName.equals("solvedeq")) {
            return 5;
        }
        if (operatorOrCommandName.equals("solvesystem")) {
            return 1;
        }
        if (operatorOrCommandName.equals("tangent")) {
            return 1;
        }
        if (operatorOrCommandName.equals("taylordeq")) {
            return 5;
        }

        // Default-Case! Alle Operatoren/Befehle, welche beliebig viele Argumente vertragen.
        return 0;

    }

}
