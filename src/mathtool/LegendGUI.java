package mathtool;

import expressionbuilder.Expression;
import java.awt.Color;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.util.HashMap;

public class LegendGUI extends JDialog {
    
    public LegendGUI(HashMap<Integer, Color> colors, HashMap<Integer, Expression> expr) {

        setTitle("About MathTool");
        setLayout(null);
        setResizable(false);

        this.setBounds(400, 200, 400, 20 + 40*colors.size());
        this.getContentPane().setBackground(Color.white);

        JLabel[] colorLabels = new JLabel[colors.size()];
        for (int i = 0; i < colorLabels.length; i++){
            colorLabels[i].setForeground(colors.get(i));
            colorLabels[i].setText("Graph " + (i + 1) + ": " + expr.get(i).writeFormula(true));
            colorLabels[i].setBounds(10, 20 + 40*i, 350, 25);
            add(colorLabels[i]);
        }
        
        /**
         * Logo laden
         */
        JPanel panel = new JPanel();
        add(panel);
        panel.add(new JLabel(new ImageIcon(getClass().getResource("icons/MathToolLogo.png"))));
        panel.setBounds(0, -5, 500, 100);
        panel.setVisible(true);

        validate();
        repaint();
    }
}
