package mathtool;

import command.TypeCommand;
import exceptions.ExpressionException;
import expressionbuilder.Expression;
import java.awt.Color;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import logicalexpressionbuilder.LogicalExpression;
import mathcommandcompiler.MathCommandCompiler;
import matrixexpressionbuilder.MatrixExpression;

public class MathToolController {

    final static ImageIcon computingOwlEyesOpen = new ImageIcon(MathToolController.class.getResource("icons/LogoOwlEyesOpen.png"));
    final static ImageIcon computingOwlEyesHalfOpen = new ImageIcon(MathToolController.class.getResource("icons/LogoOwlEyesHalfOpen.png"));
    final static ImageIcon computingOwlEyesClosed = new ImageIcon(MathToolController.class.getResource("icons/LogoOwlEyesClosed.png"));

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

    /**
     * Richtet alle Grafikpanels an der Stelle (x, y) aus und setzt ihre Breite
     * / Höhe gemäß width / height.
     */
    public static void locateGraphicPanels(JPanel[] panels, int x, int y, int width, int height) {
        for (JPanel panel : panels) {
            panel.setBounds(x, y, width, height);
        }
    }

    /**
     * Setzt alle Grafikpanels auf sichtbar / unsichtbar (gemäß visible).
     */
    public static void setGraphicPanelsVisible(JPanel[] panels, boolean visible) {
        for (JPanel panel : panels) {
            panel.setVisible(visible);
        }
    }

    /**
     * Richtet alle Komponenten in components an der Stelle (x, y) aus und
     * verschieben die nachfolgenden Komponenten (äquidistant) um jeweils delta.
     */
    public static void locateButtonsAndDropDowns(JComponent[] components, int x, int y, int width, int height, int delta) {
        for (int i = 0; i < components.length; i++) {
            components[i].setBounds(x + i * delta, y, width, height);
        }
    }

    public static void initializeTimer(Timer computingTimer, final ComputingDialogGUI computingDialog) {

        // Es folgen die TimerTasks, welche die Eule veranlassen, mit den Augen zu zwinkern.
        TimerTask start = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.setVisible(true);
                }
            }
        };
        TimerTask openEyes = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesOpen);
                }
            }
        };
        TimerTask halfOpenEyes = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesHalfOpen);
                }
            }
        };
        TimerTask closedEyes = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesClosed);
                }
            }
        };
        TimerTask halfOpenEyesAgain = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesHalfOpen);
                }
            }
        };
        TimerTask openEyesAgain = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesOpen);
                }
            }
        };
        TimerTask halfOpenEyes2 = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesHalfOpen);
                }
            }
        };
        TimerTask closedEyes2 = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesClosed);
                }
            }
        };
        TimerTask halfOpenEyesAgain2 = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesHalfOpen);
                }
            }
        };
        TimerTask openEyesAgain2 = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesOpen);
                }
            }
        };

        computingTimer.schedule(start, 1000);
        computingTimer.schedule(openEyes, 0, 2000);
        computingTimer.schedule(halfOpenEyes, 100, 2000);
        computingTimer.schedule(closedEyes, 200, 2000);
        computingTimer.schedule(halfOpenEyesAgain, 300, 2000);
        computingTimer.schedule(openEyesAgain, 400, 2000);
        computingTimer.schedule(halfOpenEyes2, 500, 2000);
        computingTimer.schedule(closedEyes2, 600, 2000);
        computingTimer.schedule(halfOpenEyesAgain2, 700, 2000);
        computingTimer.schedule(openEyesAgain2, 800, 2000);

    }

}
