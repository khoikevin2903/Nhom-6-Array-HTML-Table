package View;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class View_Output extends JFrame{
	private JPanel pnlMain;
	private JTextArea tfOutput;
	public View_Output() {
		this.setTitle("ArrayToHTMLTable");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        Gui();
        
        this.setVisible(true);
	}
	public void Gui() {
		pnlMain = new JPanel(new GridLayout(1,1));
		pnlMain.setBorder(BorderFactory.createTitledBorder("Output"));
		tfOutput = new JTextArea(1,1);
		tfOutput.setBorder(BorderFactory.createLineBorder(Color.decode("#6495ED")));
		tfOutput.setLineWrap(true);
		tfOutput.setEditable(false);
		pnlMain.add(new JScrollPane(tfOutput));
		this.add(pnlMain);
	}
}
