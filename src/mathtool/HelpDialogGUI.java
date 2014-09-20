package mathtool;

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

public class HelpDialogGUI extends JDialog implements MouseListener {

    private JLabel generalities, mathFormulas, operators, commands;
    private final JEditorPane helpArea;
    private final JScrollPane scrollPaneHelp;

    public HelpDialogGUI() {

        setTitle("Hilfe");
        setLayout(null);
        setResizable(false);
        setModal(true);

        JLabel menue = new JLabel("Menü");
        menue.setFont(menue.getFont().deriveFont(Font.BOLD));
        generalities = new JLabel("Allgemeines");
        mathFormulas = new JLabel("Mathematische Formeln");
        operators = new JLabel("Operatoren");
        commands = new JLabel("Befehle");

        menue.setVisible(true);
        generalities.setVisible(true);
        mathFormulas.setVisible(true);
        operators.setVisible(true);
        commands.setVisible(true);

        menue.setBounds(30, 120, 350, 25);
        generalities.setBounds(30, 150, 350, 25);
        generalities.addMouseListener(this);

        mathFormulas.setBounds(30, 180, 350, 25);
        mathFormulas.addMouseListener(this);

        operators.setBounds(30, 210, 350, 25);
        operators.addMouseListener(this);

        commands.setBounds(30, 240, 350, 25);
        commands.addMouseListener(this);

        add(menue);
        add(generalities);
        add(mathFormulas);
        add(operators);
        add(commands);

        this.setBounds(400, 100, 505, 310);
        this.getContentPane().setBackground(Color.white);

        JPanel panel = new JPanel();
        add(panel);
        panel.add(new JLabel(new ImageIcon(getClass().getResource("icons/Helplogo.png"))));
        panel.setBounds(0, -5, 500, 100);
        panel.setVisible(true);

        helpArea = new JEditorPane();
        helpArea.setContentType("text/html");
        add(helpArea);
        helpArea.setBounds(20, 270, 460, 330);
        helpArea.setEditable(false);
        helpArea.setVisible(false);
        scrollPaneHelp = new JScrollPane(helpArea);
        scrollPaneHelp.setBounds(20, 270, 460, 330);
        scrollPaneHelp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneHelp);
        scrollPaneHelp.setVisible(false);

        validate();
        repaint();
    }

    private void showHelpFileGeneralities() {
        helpArea.setVisible(true);
        scrollPaneHelp.setVisible(true);
        java.net.URL helpURL = HelpDialogGUI.class.getResource("help/MathToolHelpGeneralities.html");
        if (helpURL != null) {
            try {
                helpArea.setPage(helpURL);
            } catch (IOException e) {
                System.err.println("Fehler: " + helpURL);
            }
        } else {
            System.err.println("Datei nicht gefunden.");
        }
        repaint();
        validate();
    }

    private void showHelpFileFormulas() {
        helpArea.setVisible(true);
        scrollPaneHelp.setVisible(true);
        java.net.URL helpURL = HelpDialogGUI.class.getResource("help/MathToolHelpFormulas.html");
        if (helpURL != null) {
            try {
                helpArea.setPage(helpURL);
            } catch (IOException e) {
                System.err.println("Fehler: " + helpURL);
            }
        } else {
            System.err.println("Datei nicht gefunden.");
        }
        repaint();
        validate();
    }

    private void showHelpFileOperators() {
        helpArea.setVisible(true);
        scrollPaneHelp.setVisible(true);
        java.net.URL helpURL = HelpDialogGUI.class.getResource("help/MathToolHelpOperators.html");
        if (helpURL != null) {
            try {
                helpArea.setPage(helpURL);
            } catch (IOException e) {
                System.err.println("Fehler: " + helpURL);
            }
        } else {
            System.err.println("Datei nicht gefunden.");
        }
        repaint();
        validate();
    }

    private void showHelpFileCommands() {
        helpArea.setVisible(true);
        scrollPaneHelp.setVisible(true);
        java.net.URL helpURL = HelpDialogGUI.class.getResource("help/MathToolHelpCommands.html");
        if (helpURL != null) {
            try {
                helpArea.setPage(helpURL);
            } catch (IOException e) {
                System.err.println("Fehler: " + helpURL);
            }
        } else {
            System.err.println("Datei nicht gefunden.");
        }
        repaint();
        validate();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == generalities) {
            this.setBounds(400, 50, 505, 650);
            showHelpFileGeneralities();
            validate();
            repaint();
        }
        if (e.getSource() == mathFormulas) {
            this.setBounds(400, 50, 505, 650);
            showHelpFileFormulas();
            validate();
            repaint();
        }
        if (e.getSource() == operators) {
            this.setBounds(400, 50, 505, 650);
            showHelpFileOperators();
            validate();
            repaint();
        }
        if (e.getSource() == commands) {
            this.setBounds(400, 50, 505, 650);
            showHelpFileCommands();
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
            generalities.setText("<html><u>Allgemeines</u></<html>");
            validate();
            repaint();
        }
        if (e.getSource() == mathFormulas) {
            mathFormulas.setText("<html><u>Mathematische Formeln</u></<html>");
            validate();
            repaint();
        }
        if (e.getSource() == operators) {
            operators.setText("<html><u>Operatoren</u></<html>");
            validate();
            repaint();
        }
        if (e.getSource() == commands) {
            commands.setText("<html><u>Befehle</u></<html>");
            validate();
            repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == generalities) {
            generalities.setText("<html>Allgemeines</<html>");
            validate();
            repaint();
        }
        if (e.getSource() == mathFormulas) {
            mathFormulas.setText("<html>Mathematische Formeln</<html>");
            validate();
            repaint();
        }
        if (e.getSource() == operators) {
            operators.setText("<html>Operatoren</<html>");
            validate();
            repaint();
        }
        if (e.getSource() == commands) {
            commands.setText("<html>Befehle</<html>");
            validate();
            repaint();
        }
    }

}
