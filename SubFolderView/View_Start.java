package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View_Start extends JFrame implements ActionListener {
	private JButton btnStart;
	private JButton btnExit;
	
	public View_Start() {
		this.setTitle("ArraytoHTMLTable");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		Gui();
		this.setVisible(true);
	}
	public void Gui() {
		JPanel pnlNorth = new JPanel();
		pnlNorth.setBorder(BorderFactory.createLineBorder(Color.decode("#6495ED")));
		JLabel labelTopic = new JLabel("Array to HTML table");
		labelTopic.setFont(new Font("Arial", Font.BOLD, 30));
		pnlNorth.setForeground(Color.white);
		labelTopic.setOpaque(true);
		pnlNorth.setOpaque(true);
		//pnlNorth.setBackground(Color.decode("#34ad95"));
		//labelTopic.setBackground(Color.decode("#34ad95"));
		pnlNorth.add(labelTopic);
		this.add(pnlNorth, BorderLayout.NORTH);
		
		JPanel pnlCenter = new JPanel();
		pnlCenter.setBorder(BorderFactory.createLineBorder(Color.decode("#6495ED")));
		
		Box b = Box.createVerticalBox();
		Box b1 = Box.createVerticalBox();
		Box b2 = Box.createVerticalBox();
		Box b3 = Box.createVerticalBox();
		
		
		Box b1_1 = Box.createVerticalBox();
		Box b1_2 = Box.createHorizontalBox();
		b1_1.add(new JLabel("Group 6"));
		b1_2.add(new JLabel("Trần Anh Khôi"));
		b1_2.add(Box.createHorizontalStrut(10));
		b1_2.add(new JLabel("Đỗ Văn Trình"));
		
		
		b1.add(b1_1);
		b1.add(Box.createVerticalStrut(5));
		b1.add(b1_2);
		
		Box b2_1 = Box.createVerticalBox();
		Box b2_2 = Box.createVerticalBox();
		b2_1.add(new JLabel("Input:"));
		b2_2.add(new JLabel("- In this problem the input is a 2-D array (M rows, N columns)."));
		b2_2.add(Box.createHorizontalStrut(10));
		b2_2.add(new JLabel("- This application take input from TextField of input panel."));
		b.add(Box.createHorizontalStrut(2));
		b2_2.add(new JLabel("- Input must following these rules:"));
		b.add(Box.createHorizontalStrut(2));
		b2_2.add(new JLabel("        + One row in TextField stand for one row in our array."));
		b2_2.add(Box.createHorizontalStrut(2));
		b2_2.add(new JLabel("        + Cell of each row must be separated by a comma (',')."));
		b.add(Box.createHorizontalStrut(2));
		b2_2.add(new JLabel("        + If you want something like null/None value, just not"));
		b.add(Box.createHorizontalStrut(0));
		b2_2.add(new JLabel("                                   to write anything between two comma."));
		
		
		b2.add(b2_1);
		b2.add(Box.createVerticalStrut(5));
		b2.add(b2_2);
		
		
		Box b3_1 = Box.createVerticalBox();
		Box b3_2 = Box.createVerticalBox();
		b3_1.add(new JLabel("Output:"));
		b3_2.add(Box.createHorizontalStrut(10));
		b3_2.add(new JLabel("- Output is a string containing HTML tags representing the data from input array."));
		b3_2.add(Box.createHorizontalStrut(10));
		b3_2.add(new JLabel("- Press Run button, result will appear in TextField of output panel."));
		b3_2.add(new JLabel(""));
		b3_2.add(new JLabel(""));
		b3_2.add(new JLabel(""));
		b3_2.add(new JLabel(""));
		
		
		
		b3.add(b3_1);
		b3.add(Box.createVerticalStrut(5));
		b3.add(b3_2);
		
		btnStart = new JButton();
		
		b.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		
		b.add(b2);
		b.add(Box.createHorizontalStrut(30));
		b.add(b3);
		b.add(Box.createHorizontalStrut(30));
		
		
		pnlCenter.add(b, BorderLayout.SOUTH);
		//pnlCenter.add(b1, BorderLayout.CENTER);
		this.add(pnlCenter, BorderLayout.CENTER);
		
		JPanel	pnlSouth=new JPanel();
		pnlSouth.setBorder(BorderFactory.createLineBorder(Color.red));
		pnlSouth.setLayout(new BorderLayout());
		pnlSouth.add(b1, BorderLayout.WEST);
		Box b5 = Box.createHorizontalBox();
		b5.add(Box.createHorizontalStrut(200));
		b5.add(btnStart=new JButton("Get Stated"));
		b5.add(Box.createHorizontalStrut(30));
		//b5.add(btnExit=new JButton("Exit"));
		pnlSouth.add(b5);
		this.add(pnlSouth,BorderLayout.SOUTH);
		btnExit.addActionListener(this);

	}
}
