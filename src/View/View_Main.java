package View;

import javax.swing.*;
import java.awt.*;

public class View_Main extends JFrame {
    private JTextArea inputTA;
    private JButton runBtn, saveBtn, clearBtn;
    private JMenuItem exitMenu, aboutMenu, gettingStarted, exportResult, deleteRow, editRow;
    private JCheckBox headerCB, indexCB;
    private JTabbedPane tabPane;
    private JTable table;
    private JPopupMenu popupMenu;

    public View_Main() {
        this.setTitle("ArrayToHTMLTable");
//        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(500, 400);
//        this.setLocationRelativeTo(null);

        initComponents();
        createMenuBar();
    }

    private void initComponents() {
        tabPane = new JTabbedPane();
        tabPane.addTab("Workspace", createMainTabPanel());
        tabPane.addTab("History", createHistoryPanel());
        this.getContentPane().add(tabPane);
    }

    private JPanel createMainTabPanel() {
        JPanel tabPanel1 = new JPanel();

        tabPanel1.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridLayout(1, 1));

        JPanel panel1 = new JPanel(new BorderLayout(5, 5));
        panel1.setBorder(BorderFactory.createTitledBorder("Input"));
        panel1.add(new JLabel("2-D Array"), BorderLayout.WEST);
        inputTA = new JTextArea(4, 1);
        inputTA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel1.add(inputTA, BorderLayout.CENTER);

        JPanel panel1_1 = new JPanel(new GridBagLayout());
        headerCB = new JCheckBox("True");
        indexCB = new JCheckBox("True");
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(5, 2, 5, 5);
        gc.gridx = 0;
        gc.gridy = 0;
        panel1_1.add(new JLabel("Header"), gc);
        gc.gridx++;
        panel1_1.add(headerCB, gc);
        gc.gridx = 0;
        gc.gridy++;
        panel1_1.add(new JLabel("Index"), gc);
        gc.gridx++;
        gc.weightx = 8;
        panel1_1.add(indexCB, gc);
        panel1.add(panel1_1, BorderLayout.SOUTH);
        mainPanel.add(panel1);

        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commandPanel.setBorder(BorderFactory.createTitledBorder("Command"));
       
        ImageIcon iconRun = new ImageIcon("img/run.png");
        
        runBtn = new JButton("Run");
        runBtn.setIcon(iconRun);
        
        ImageIcon iconSave = new ImageIcon("img/save.png");
        saveBtn = new JButton("Save");
        saveBtn.setIcon(iconSave);
        
        ImageIcon iconClear = new ImageIcon("img/delete.png");
        clearBtn = new JButton("Clear");
        clearBtn.setIcon(iconClear);
        
        commandPanel.add(runBtn);
        commandPanel.add(saveBtn);
        commandPanel.add(clearBtn);

        tabPanel1.add(mainPanel, BorderLayout.CENTER);
        tabPanel1.add(commandPanel, BorderLayout.SOUTH);
        return tabPanel1;
    }

    private JPanel createHistoryPanel() {
        table = new JTable();
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        initPopupMenu();
        return historyPanel;
    }

    private void initPopupMenu() {
        popupMenu = new JPopupMenu();
        deleteRow = new JMenuItem("Delete");
        editRow = new JMenuItem("Edit");
        popupMenu.add(editRow);
        popupMenu.add(deleteRow);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        exitMenu = new JMenuItem("Exit");
        exportResult = new JMenuItem("Export...");
        fileMenu.add(exportResult);
        fileMenu.add(exitMenu);

        JMenu helpMenu = new JMenu("Help");
        aboutMenu = new JMenuItem("About");
        gettingStarted = new JMenuItem("Getting Started");
        helpMenu.add(gettingStarted);
        helpMenu.add(aboutMenu);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);
    }

    public JTextArea getInput() {
        return inputTA;
    }

    public JCheckBox getHeader() {
        return headerCB;
    }

    public JCheckBox getIndex() {
        return indexCB;
    }

    public JButton getRunBtn() {
        return runBtn;
    }

    public JMenuItem getExitMenu() {
        return exitMenu;
    }

    public JMenuItem getAboutMenu() {
        return aboutMenu;
    }

    public JMenuItem getGettingStartedMenu() {
        return gettingStarted;
    }

    public JMenuItem getExportResult() {
        return exportResult;
    }

    public JButton getSaveBtn() {
        return saveBtn;
    }

    public JTable getHistoryTable() {
        return table;
    }

    public JTabbedPane getTabPane() {
        return tabPane;
    }

    public JButton getClearBtn() {
        return clearBtn;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public JMenuItem getDeleteRow() {
        return deleteRow;
    }

    public JMenuItem getEditRow() {
        return editRow;
    }
}
