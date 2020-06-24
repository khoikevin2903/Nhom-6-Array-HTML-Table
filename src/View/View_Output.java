package View;

import javax.swing.*;
import java.awt.*;

public class View_Output extends JFrame {
    private JTextArea tfOutput;

    public View_Output() {
        this.setTitle("ArrayToHTMLTable");
        this.setSize(500, 500);

        Gui();
    }

    public void Gui() {
        JPanel pnlMain = new JPanel(new GridLayout(1, 1));
        pnlMain.setBorder(BorderFactory.createTitledBorder("Output"));
        tfOutput = new JTextArea(1, 1);
        tfOutput.setBorder(BorderFactory.createLineBorder(Color.decode("#6495ED")));
        tfOutput.setLineWrap(true);
        tfOutput.setEditable(false);
        pnlMain.add(new JScrollPane(tfOutput));
        this.add(pnlMain);
    }

    public JTextArea getOutputTA() {
        return tfOutput;
    }
}
