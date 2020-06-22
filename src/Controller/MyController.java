package Controller;

import Model.Database;
import Model.HTMLObject;
import Model.HTMLObjectTableModel;
import View.View_Main;
import View.View_Output;
import View.View_Start;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyController {
    private final View_Main mainView;
    private final View_Output outputView;
    private final View_Start startView;
    private final Database database = Database.getInstance();
    private final HTMLObjectTableModel model;
    private HTMLObject editObject = null;
    private HTMLObject myObject = null;
    private boolean editMode = false;

    public MyController() {
        mainView = new View_Main();
        outputView = new View_Output();
        startView = new View_Start();
        model = new HTMLObjectTableModel();

        initView();
        initModel();
        initController();
    }

    private void initView() {
        startView.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        mainView.getHistoryTable().setModel(model);
        mainView.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Point x = mainView.getLocation();
        outputView.setLocation(new Point((int) (x.getX() + mainView.getWidth()), (int) x.getY()));
        outputView.setSize(new Dimension(mainView.getSize()));
        outputView.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        mainView.setVisible(true);
//        outputView.setVisible(true);
        startView.setVisible(true);
    }

    private void initModel() {
        model.setMyObjects(database.getMyObjects());
    }

    private void initController() {
        initWindowAction();
        initMenuAction();
        initButtonAction();
        initTableAction();
    }

    private void initMenuAction() {
        mainView.getAboutMenu().addActionListener(l -> JOptionPane.showMessageDialog(mainView,
                "Not implement yet", "About", JOptionPane.INFORMATION_MESSAGE));
        mainView.getExitMenu().addActionListener(l -> mainView.dispatchEvent(new WindowEvent(mainView, WindowEvent.WINDOW_CLOSING)));
        mainView.getGettingStartedMenu().addActionListener(l -> {
            if (!startView.isVisible())
                startView.setVisible(true);
        });
        mainView.getExportResult().addActionListener(l -> {
            JOptionPane.showMessageDialog(null,"Not implement yet");
            /*if (myObject == null) {
                JOptionPane.showMessageDialog(mainView, "Nothing to export",
                        "Export information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (fileChooser.showSaveDialog(mainView) == JFileChooser.APPROVE_OPTION) {
                new Thread(() -> {
                    try {
                        exportDataToFile(fileChooser.getSelectedFile());
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }).start();
            }*/
        });
    }

    private void initWindowAction() {
        mainView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                new Thread(() -> {
                    try {
                        database.connect();
                        database.loadDB();
                        refreshHistoryTable();
                    } catch (ClassNotFoundException | SQLException exception) {
                        JOptionPane.showMessageDialog(mainView,
                                "Cannot connect DB " + exception.getMessage(),
                                "Connect DB error!", JOptionPane.ERROR_MESSAGE);
                        mainView.dispose();
                        outputView.dispose();
                    }
                }).start();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(mainView, "Do you want to exit!",
                        "Exit Confirm", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        database.disconnect();
                    } catch (SQLException ignored) {
                    } finally {
                        outputView.dispose();
                        startView.dispose();
                        mainView.dispose();
                    }
                }
            }
        });
    }

    private void initButtonAction() {
        startView.getBtnStart().addActionListener(l -> startView.dispose());

        mainView.getRunBtn().addActionListener(l -> {
            String rawInput = mainView.getInput().getText().trim();
            boolean header = mainView.getHeader().isSelected();
            boolean index = mainView.getIndex().isSelected();
            if ("".equals(rawInput))
                return;
            String[][] input = preProcessInput(rawInput);
            String date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            myObject = new HTMLObject(input, new boolean[]{header, index}, date);
            if (!outputView.isVisible()) outputView.setVisible(true);
            outputView.getOutputTA().setText(myObject.getTable());
        });

        mainView.getSaveBtn().addActionListener(l -> {
            if (myObject == null) return;
            if (editMode) {
                myObject.setId(editObject.getId());
                database.replaceByID(myObject);
                editMode = false;
            } else {
                myObject.setId(HTMLObject.ID_IDENTIFY++);
                database.addObject(myObject);
            }
            try {
                database.saveToDB(myObject);
                refreshHistoryTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(mainView, "Cannot save data",
                        "Save data error!", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainView.getClearBtn().addActionListener(l -> {
            mainView.getInput().setText("");
            outputView.getOutputTA().setText("");
            mainView.getHeader().setSelected(false);
            mainView.getIndex().setSelected(false);
            myObject = null;
            editObject = null;
            editMode = false;
        });
    }

    private void initTableAction() {
        mainView.getDeleteRow().addActionListener(l -> {
            JTable table = mainView.getHistoryTable();
            int id = (int) model.getValueAt(table.getSelectedRow(), 0);
            try {
                database.deleteObjectByID(id);
                refreshHistoryTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(mainView, e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        mainView.getEditRow().addActionListener(l -> {
            JTable table = mainView.getHistoryTable();
            int id = (int) model.getValueAt(table.getSelectedRow(), 0);
            editObject = database.findByID(id);
            editMode = true;
            mainView.getInput().setText(generateRawInput(editObject.getArr()));
            mainView.getHeader().setSelected(editObject.getHeader());
            mainView.getIndex().setSelected(editObject.getIndex());
            outputView.getOutputTA().setText(editObject.getTable());
            mainView.getTabPane().setSelectedIndex(0);
        });
        mainView.getHistoryTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JTable table = mainView.getHistoryTable();
                    int row = table.rowAtPoint(e.getPoint());
                    table.getSelectionModel().setSelectionInterval(row, row);
                    mainView.getPopupMenu().show(table, e.getX(), e.getY());
                }
            }
        });
    }

    //Utils Function
    private void refreshHistoryTable() {
        model.fireTableDataChanged();
    }

/*
    private void exportDataToFile(File file) throws IOException {
        String htmlTmp = readHTMLTemplate();
        String output = myObject.getTableAsHTML();
        String header = String.valueOf(myObject.getHeader());
        String index = String.valueOf(myObject.getIndex());
        String arr = Arrays.deepToString(myObject.getArr());
        htmlTmp = htmlTmp.replaceAll("@output", output)
                .replaceAll("@header", header)
                .replaceAll("@index", index)
                .replaceAll("@arr", arr);

        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(htmlTmp);
        }
    }
*/

    private String[][] preProcessInput(String rawInput) {
        String[] rows = rawInput.split("\n");
        String[][] arr = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            String[] cell = row.split(",");
            arr[i] = new String[cell.length];
            for (int j = 0; j < cell.length; j++) {
                String tmp = (cell[j].trim().length() > 0) ? cell[j].trim() : null;
                arr[i][j] = tmp;
            }
        }
        return arr;
    }

    private String generateRawInput(String[][] arr) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : arr) {
            String tmp = String.join(", ", row);
            sb.append(tmp).append("\n");
        }
        return sb.toString();
    }

/*
    private String readHTMLTemplate() throws IOException {
        File file = new File("template/IndexTemp.html");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
*/
}
