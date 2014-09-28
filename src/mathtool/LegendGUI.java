package mathtool;

import expressionbuilder.Expression;
import java.awt.Color;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.HashMap;

public class LegendGUI extends JDialog {

    public LegendGUI(String[] instructions, HashMap<Integer, Color> colors, HashMap<Integer, Expression> expr) {

        setTitle("Legende");
        setLayout(null);
        setResizable(false);
        setModal(true);

        this.setBounds(400, 200, 400, 145 + 20*instructions.length + 20*colors.size());
        this.getContentPane().setBackground(Color.white);

        JLabel instruction = new JLabel("<html><b>Bedienung:</b></<html>");
        instruction.setBounds(10, 70, 350, 25);
        add(instruction);

        JLabel[] instr = new JLabel[instructions.length];
        for (int i = 0; i < instr.length; i++) {
            instr[i] = new JLabel();
            instr[i].setText(instructions[i]);
            instr[i].setBounds(10, 90 + 20*i, 350, 25);
            add(instr[i]);
        }

        JLabel graphs = new JLabel("<html><b>Graphen:</b></<html>");
        graphs.setBounds(10, 90 + 20*instructions.length, 350, 25);
        add(graphs);

        JLabel[] colorLabels = new JLabel[colors.size()];
        for (int i = 0; i < colorLabels.length; i++) {
            colorLabels[i] = new JLabel();
            colorLabels[i].setForeground(colors.get(i));
            colorLabels[i].setText("Graph " + (i + 1) + ": " + expr.get(i).writeFormula(true));
            colorLabels[i].setBounds(10, 110 + 20*instructions.length + 20*i, 350, 25);
            add(colorLabels[i]);
        }

        /**
         * Logo laden
         */
        JPanel panel = new JPanel(); 
        add(panel); 
        panel.add(new JLabel(new ImageIcon(getClass().getResource("icons/LegendLogo.png"))));
        panel.setBounds(0, -5, 400, 60); panel.setVisible(true);
        validate();
        repaint();
    }
}
