package mathtool;

import Translator.Translator;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import expressionbuilder.Expression;

public class HelpDialogGUI extends JDialog implements MouseListener {

    private final JLabel generalities, mathFormulas, logicalExpressions, operators, commands, contact;
    private final JEditorPane helpArea;
    private final JScrollPane scrollPaneHelp;

    public HelpDialogGUI(int x_mathtoolform, int y_mathtoolform, int with_mathtoolform, int heigth_mathtoolform) {

        setTitle(Translator.translateExceptionMessage("GUI_HelpDialogGUI_HELP"));
        setLayout(null);
        setResizable(false);
        setModal(true);

        JLabel menue = new JLabel(Translator.translateExceptionMessage("GUI_HelpDialogGUI_MENUE"));
        menue.setFont(menue.getFont().deriveFont(Font.BOLD));
        generalities = new JLabel(Translator.translateExceptionMessage("GUI_HelpDialogGUI_GENERALITIES"));
        mathFormulas = new JLabel(Translator.translateExceptionMessage("GUI_HelpDialogGUI_MATH_FORMULAS"));
        logicalExpressions = new JLabel(Translator.translateExceptionMessage("GUI_HelpDialogGUI_LOGICAL_EXPRESSION"));
        operators = new JLabel(Translator.translateExceptionMessage("GUI_HelpDialogGUI_OPERATORS"));
        commands = new JLabel(Translator.translateExceptionMessage("GUI_HelpDialogGUI_COMMANDS"));
        contact = new JLabel(Translator.translateExceptionMessage("GUI_HelpDialogGUI_BUG_REPORT"));

        menue.setVisible(true);
        generalities.setVisible(true);
        mathFormulas.setVisible(true);
        logicalExpressions.setVisible(true);
        operators.setVisible(true);
        commands.setVisible(true);
        contact.setVisible(true);

        menue.setBounds(30, 120, 350, 25);
        generalities.setBounds(30, 150, 350, 25);
        generalities.addMouseListener(this);

        mathFormulas.setBounds(30, 180, 350, 25);
        mathFormulas.addMouseListener(this);

        logicalExpressions.setBounds(30, 210, 350, 25);
        logicalExpressions.addMouseListener(this);

        operators.setBounds(30, 240, 350, 25);
        operators.addMouseListener(this);

        commands.setBounds(30, 270, 350, 25);
        commands.addMouseListener(this);

        contact.setBounds(30, 300, 350, 25);
        contact.addMouseListener(this);

        add(menue);
        add(generalities);
        add(mathFormulas);
        add(logicalExpressions);
        add(operators);
        add(commands);
        add(contact);

        this.setBounds((with_mathtoolform - 505) / 2 + x_mathtoolform, (heigth_mathtoolform - 680) / 2 + y_mathtoolform, 505, 370);
        this.getContentPane().setBackground(Color.white);

        /**
         * Logo laden
         */
        JPanel panel = new JPanel();
        add(panel);
        panel.add(new JLabel(new ImageIcon(getClass().getResource("icons/Helplogo.png"))));
        panel.setBounds(0, -5, 500, 100);
        panel.setVisible(true);

        helpArea = new JEditorPane();
        helpArea.setContentType("text/html");
        add(helpArea);
        helpArea.setBounds(20, 330, 460, 300);
        helpArea.setEditable(false);
        helpArea.setVisible(false);
        scrollPaneHelp = new JScrollPane(helpArea);
        scrollPaneHelp.setBounds(20, 330, 460, 300);
        scrollPaneHelp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneHelp);
        scrollPaneHelp.setVisible(false);

        validate();
        repaint();
    }

    private void showHelpFile(String helpType) {
        helpArea.setVisible(true);
        scrollPaneHelp.setVisible(true);

        /**
         * Der Parameter helpType gibt den (relevanten) Teil des Dateinamens an,
         * aus welcher die Hilfe ausgelesen wird.
         */
        java.net.URL helpURL = HelpDialogGUI.class.getResource("help/MathToolHelp" + helpType + Expression.getLanguage().toString() + ".html");

        if (helpURL != null) {
            try {
                helpArea.setPage(helpURL);
            } catch (IOException e) {
            }
        }
        repaint();
        validate();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == generalities) {
            this.setBounds(this.getX(), this.getY(), 505, 680);
            generalities.setForeground(Color.blue);
            mathFormulas.setForeground(Color.black);
            logicalExpressions.setForeground(Color.black);
            operators.setForeground(Color.black);
            commands.setForeground(Color.black);
            contact.setForeground(Color.black);
            showHelpFile("Generalities");
            validate();
            repaint();
        } else if (e.getSource() == mathFormulas) {
            this.setBounds(this.getX(), this.getY(), 505, 680);
            generalities.setForeground(Color.black);
            mathFormulas.setForeground(Color.blue);
            logicalExpressions.setForeground(Color.black);
            operators.setForeground(Color.black);
            commands.setForeground(Color.black);
            contact.setForeground(Color.black);
            showHelpFile("Formulas");
            validate();
            repaint();
        } else if (e.getSource() == logicalExpressions) {
            this.setBounds(this.getX(), this.getY(), 505, 680);
            generalities.setForeground(Color.black);
            mathFormulas.setForeground(Color.black);
            logicalExpressions.setForeground(Color.blue);
            operators.setForeground(Color.black);
            commands.setForeground(Color.black);
            contact.setForeground(Color.black);
            showHelpFile("LogicalExpressions");
            validate();
            repaint();
        } else if (e.getSource() == operators) {
            this.setBounds(this.getX(), this.getY(), 505, 680);
            generalities.setForeground(Color.black);
            mathFormulas.setForeground(Color.black);
            logicalExpressions.setForeground(Color.black);
            operators.setForeground(Color.blue);
            commands.setForeground(Color.black);
            contact.setForeground(Color.black);
            showHelpFile("Operators");
            validate();
            repaint();
        } else if (e.getSource() == commands) {
            this.setBounds(this.getX(), this.getY(), 505, 680);
            generalities.setForeground(Color.black);
            mathFormulas.setForeground(Color.black);
            logicalExpressions.setForeground(Color.black);
            operators.setForeground(Color.black);
            commands.setForeground(Color.blue);
            contact.setForeground(Color.black);
            showHelpFile("Commands");
            validate();
            repaint();
        } else if (e.getSource() == contact) {
            this.setBounds(this.getX(), this.getY(), 505, 680);
            generalities.setForeground(Color.black);
            mathFormulas.setForeground(Color.black);
            logicalExpressions.setForeground(Color.black);
            operators.setForeground(Color.black);
            commands.setForeground(Color.black);
            contact.setForeground(Color.blue);
            showHelpFile("Contact");
            validate();
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() == generalities) {
            generalities.setText("<html><u>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_GENERALITIES") + "</u></html>");
            validate();
            repaint();
        } else if (e.getSource() == mathFormulas) {
            mathFormulas.setText("<html><u>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_MATH_FORMULAS") + "</u></html>");
            validate();
            repaint();
        } else if (e.getSource() == logicalExpressions) {
            logicalExpressions.setText("<html><u>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_LOGICAL_EXPRESSION") + "</u></html>");
            validate();
            repaint();
        } else if (e.getSource() == operators) {
            operators.setText("<html><u>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_OPERATORS") + "</u></html>");
            validate();
            repaint();
        } else if (e.getSource() == commands) {
            commands.setText("<html><u>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_COMMANDS") + "</u></html>");
            validate();
            repaint();
        } else if (e.getSource() == contact) {
            contact.setText("<html><u>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_BUG_REPORT") + "</u></html>");
            validate();
            repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == generalities) {
            generalities.setText("<html>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_GENERALITIES") + "</html>");
            validate();
            repaint();
        } else if (e.getSource() == mathFormulas) {
            mathFormulas.setText("<html>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_MATH_FORMULAS") + "</html>");
            validate();
            repaint();
        } else if (e.getSource() == logicalExpressions) {
            logicalExpressions.setText("<html>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_LOGICAL_EXPRESSION") + "</html>");
            validate();
            repaint();
        } else if (e.getSource() == operators) {
            operators.setText("<html>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_OPERATORS") + "</html>");
            validate();
            repaint();
        } else if (e.getSource() == commands) {
            commands.setText("<html>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_COMMANDS") + "</html>");
            validate();
            repaint();
        } else if (e.getSource() == contact) {
            contact.setText("<html>" + Translator.translateExceptionMessage("GUI_HelpDialogGUI_BUG_REPORT") + "</html>");
            validate();
            repaint();
        }
    }

}
