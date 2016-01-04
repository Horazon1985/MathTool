package mathtool;

import command.Command;
import command.TypeCommand;
import enumerations.TypeGraphic;
import enumerations.TypeLanguage;
import exceptions.ExpressionException;
import expressionbuilder.Expression;
import expressionbuilder.Operator;
import expressionbuilder.TypeOperator;
import graphic.GraphicArea;
import graphic.GraphicPanel3D;
import graphic.GraphicPanelCurves3D;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import logicalexpressionbuilder.LogicalExpression;
import mathcommandcompiler.MathCommandCompiler;
import static mathtool.MathToolGUI.mathToolGraphicAreaHeight;
import static mathtool.MathToolGUI.mathToolGraphicAreaWidth;
import static mathtool.MathToolGUI.mathToolGraphicAreaX;
import static mathtool.MathToolGUI.mathToolGraphicAreaY;
import matrixexpressionbuilder.MatrixExpression;
import translator.Translator;

public class MathToolController {

    final static ImageIcon computingOwlEyesOpen = new ImageIcon(MathToolController.class.getResource("icons/LogoOwlEyesOpen.png"));
    final static ImageIcon computingOwlEyesHalfOpen = new ImageIcon(MathToolController.class.getResource("icons/LogoOwlEyesHalfOpen.png"));
    final static ImageIcon computingOwlEyesClosed = new ImageIcon(MathToolController.class.getResource("icons/LogoOwlEyesClosed.png"));

    /**
     * Setzt die Einträge in den Operator-Dropdown.
     */
    public static void fillOperatorChoice(JComboBox operatorChoice) {
        ArrayList<String> newEntries = new ArrayList<>();
        newEntries.add(Translator.translateExceptionMessage("GUI_MathToolForm_OPERATOR"));
        for (TypeOperator value : TypeOperator.values()) {
            newEntries.add(Operator.getNameFromType(value));
        }
        operatorChoice.removeAllItems();
        for (String op : newEntries) {
            operatorChoice.addItem(op);
        }
    }

    /**
     * Setzt die Einträge in den Befehl-Dropdown.
     */
    public static void fillCommandChoice(JComboBox commandChoice) {
        ArrayList<String> newEntries = new ArrayList<>();
        newEntries.add(Translator.translateExceptionMessage("GUI_MathToolForm_COMMAND"));
        for (TypeCommand value : TypeCommand.values()) {
            newEntries.add(value.toString());
        }
        commandChoice.removeAllItems();
        for (String c : newEntries) {
            commandChoice.addItem(c);
        }
    }

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
     * Ermittelt den Typ des GraphicPanels, welcher zum Befehl c gehört.
     */
    public static TypeGraphic getTypeGraphicFromCommand(Command c) {
        if (c.getName().equals("plot2d")) {
            return TypeGraphic.GRAPH2D;
        }
        if (c.getName().equals("plotimplicit")) {
            return TypeGraphic.GRAPHIMPLICIT;
        }
        if (c.getName().equals("plot3d") || c.getName().equals("tangent") && ((HashMap) c.getParams()[1]).size() == 2) {
            return TypeGraphic.GRAPH3D;
        }
        if (c.getName().equals("plotcurve") && c.getParams().length == 4) {
            return TypeGraphic.CURVE2D;
        }
        if (c.getName().equals("plotcurve") && c.getParams().length == 5) {
            return TypeGraphic.CURVE3D;
        }
        if (c.getName().equals("plotpolar")) {
            return TypeGraphic.POLARGRAPH2D;
        }
        if (c.getName().equals("regressionline") && c.getParams().length >= 2) {
            return TypeGraphic.GRAPH2D;
        }
        if (c.getName().equals("solve") && c.getParams().length >= 4 || c.getName().equals("tangent") && ((HashMap) c.getParams()[1]).size() == 1) {
            return TypeGraphic.GRAPH2D;
        }
        if (c.getName().equals("solvedeq")) {
            return TypeGraphic.GRAPH2D;
        }
        return TypeGraphic.NONE;
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
     * Setzt die Maße der beiden JScrollPanes auf (x, y, width, height) und die
     * Maße der restlichen Komponenten werden dementsprechend angepasst.
     */
    public static void resizeConsole(JScrollPane scrollPaneText, JScrollPane scrollPaneGraphic, int x, int y, int width, int height,
            JTextArea mathToolTextArea, GraphicArea mathToolGraphicArea, MathToolTextField mathToolTextField,
            JButton inputButton, JButton cancelButton) {

        // Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
        scrollPaneText.setBounds(x, y, width, height);
        scrollPaneGraphic.setBounds(x, y, width, height);
        mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
        mathToolGraphicArea.setBounds(0, 0, scrollPaneGraphic.getWidth(), scrollPaneGraphic.getHeight());
        mathToolGraphicAreaX = 0;
        mathToolGraphicAreaY = 0;
        mathToolGraphicAreaWidth = scrollPaneGraphic.getWidth();
        mathToolGraphicAreaHeight = scrollPaneGraphic.getHeight();
        mathToolTextField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
        inputButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, inputButton.getWidth(), inputButton.getHeight());
        cancelButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, cancelButton.getWidth(), cancelButton.getHeight());

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

    /**
     * Rotation bei 3D-Graphen stoppen.
     */
    public static void stopRotationOfGraph(GraphicPanel3D graphicPanel3D, GraphicPanelCurves3D graphicPanelCurves3D,
            Thread rotateThread, JLabel rotateLabel) {

        if (MathToolGUI.getTypeGraphic().equals(TypeGraphic.GRAPH3D)) {
            graphicPanel3D.setIsRotating(false);
        } else if (MathToolGUI.getTypeGraphic().equals(TypeGraphic.CURVE3D)) {
            graphicPanelCurves3D.setIsRotating(false);
        }
        rotateThread.interrupt();
        rotateLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_ROTATE_GRAPH") + "</b></html>");

    }

    /**
     * Setzt je nach Sprachmodus den entsprechenden Menüeintrag auf fett.
     */
    public static void setFontForLanguages(JMenuItem menuItemLanguageEnglish, JMenuItem menuItemLanguageGerman,
            JMenuItem menuItemLanguageRussian, JMenuItem menuItemLanguageUkrainian) {
        
        // Im Sprachmenü die gewählte Sprache fett hervorheben.
        if (Expression.getLanguage().equals(TypeLanguage.EN)) {
            menuItemLanguageEnglish.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.BOLD, 12));
            menuItemLanguageGerman.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageRussian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageUkrainian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
        } else if (Expression.getLanguage().equals(TypeLanguage.DE)) {
            menuItemLanguageEnglish.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageGerman.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.BOLD, 12));
            menuItemLanguageRussian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageUkrainian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
        } else if (Expression.getLanguage().equals(TypeLanguage.RU)) {
            menuItemLanguageEnglish.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageGerman.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageRussian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.BOLD, 12));
            menuItemLanguageUkrainian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
        } else if (Expression.getLanguage().equals(TypeLanguage.UA)) {
            menuItemLanguageEnglish.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageGerman.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageRussian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageUkrainian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.BOLD, 12));
        }
        
    }
    
    /**
     * Setzt je nach Darstellungsmodus den entsprechenden Menüeintrag auf fett.
     */
    public static void setFontForMode(JMenuItem menuItemRepresentationFormula, JMenuItem menuItemRepresentationText) {
        // Im Darstellungsmenü den gewählten Modus fett hervorheben.
        if (MathToolGUI.getMode().equals(TypeMode.GRAPHIC)) {
            menuItemRepresentationFormula.setFont(new Font(menuItemRepresentationFormula.getFont().getFamily(), Font.BOLD, 12));
            menuItemRepresentationText.setFont(new Font(menuItemRepresentationText.getFont().getFamily(), Font.PLAIN, 12));
        } else if (MathToolGUI.getMode().equals(TypeMode.TEXT)) {
            menuItemRepresentationFormula.setFont(new Font(menuItemRepresentationFormula.getFont().getFamily(), Font.PLAIN, 12));
            menuItemRepresentationText.setFont(new Font(menuItemRepresentationText.getFont().getFamily(), Font.BOLD, 12));
        }
    }
    
    /**
     * Die Captions aller in componentCaptions befindlichen Komponenten werden
     * entsprechend der Sprache und der ID (im zugehörigen value) gesetzt.
     */
    public static void updateAllCaptions(HashMap<JComponent, String> componentCaptions) {
        for (JComponent component : componentCaptions.keySet()) {
            if (component instanceof JLabel) {
                ((JLabel) component).setText("<html><b>" + Translator.translateExceptionMessage(componentCaptions.get(component)) + "</b></html>");
            } else if (component instanceof JButton) {
                ((JButton) component).setText(Translator.translateExceptionMessage(componentCaptions.get(component)));
            } else if (component instanceof JMenuItem) {
                ((JMenuItem) component).setText(Translator.translateExceptionMessage(componentCaptions.get(component)));
            }
        }
    }

}
