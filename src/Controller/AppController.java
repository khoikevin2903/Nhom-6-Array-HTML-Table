package Controller;

import Model.Database;
import Model.HTMLObject;
import Model.TableModel;
import View.OutputFrame;
import View.StartFrame;
import View.WorkFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppController {
    private final WorkFrame mainFrame;
    private final OutputFrame outputFrame;
    private final StartFrame startFrame;
    private final Database database = Database.getInstance();
    private final TableModel tableModel;
    private final List<Integer> deletedRowInLogTable = new ArrayList<>();
    private HTMLObject editObject = null;
    private HTMLObject currentObject = null;
    private boolean editMode = false;

    public AppController() {
        mainFrame = new WorkFrame();
        outputFrame = new OutputFrame();
        startFrame = new StartFrame();
        tableModel = new TableModel();

        initView();
        initModel();
        initController();
    }

    private void initView() {
        startFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        mainFrame.getLogTable().setModel(tableModel);
        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Point x = mainFrame.getLocation();
        outputFrame.setLocation(new Point((int) (x.getX() + mainFrame.getWidth()), (int) x.getY()));
        outputFrame.setSize(new Dimension(mainFrame.getSize()));
        outputFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        mainFrame.setVisible(true);
//        outputView.setVisible(true);
        startFrame.setVisible(true);
    }

    private void initModel() {
        tableModel.setLogTableData(database.getDataInDB());
    }

    private void initController() {
        setWorkFrameAction();
        setMenuAction();
        setButtonAction();
        setTableAction();
    }

    private void setMenuAction() {
        mainFrame.getAboutMenu().addActionListener(l -> JOptionPane.showMessageDialog(mainFrame,
                "18TCLC-Nhat - BKDN\n" +
                        "Đỗ Văn Trình\n" +
                        "Trần Anh Khôi", "About", JOptionPane.INFORMATION_MESSAGE));

        mainFrame.getExitMenu().addActionListener(l -> mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING)));

        mainFrame.getGettingStartedMenu().addActionListener(l -> {
            if (!startFrame.isVisible())
                startFrame.setVisible(true);
        });

    }

    private void setWorkFrameAction() {
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                new Thread(() -> {
                    try {
                        database.connectDB();
                        database.loadDB();
                        refreshLogTable();
                    } catch (ClassNotFoundException | SQLException exception) {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Cannot connect to DB " + exception.getMessage(),
                                "Connect DB error!", JOptionPane.ERROR_MESSAGE);
                        mainFrame.dispose();
                        outputFrame.dispose();
                    }
                }).start();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                String message;

                if (deletedRowInLogTable.size() != 0)
                    message = "Save changes and exit";
                else
                    message = "Do you want to exit?";

                int option = JOptionPane.showConfirmDialog(mainFrame, message,
                        "Exit Confirm", JOptionPane.OK_CANCEL_OPTION);

                //Not save changes and exit
                if (option == JOptionPane.CANCEL_OPTION && deletedRowInLogTable.size() != 0) {
                    outputFrame.dispose();
                    startFrame.dispose();
                    mainFrame.dispose();
                }

                //Save changes and exit
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        for (Integer id : deletedRowInLogTable)
                            database.deleteRowInLogTableByID(id);
                        database.disconnectDB();
                    } catch (SQLException ignored) {
                    } finally {
                        outputFrame.dispose();
                        startFrame.dispose();
                        mainFrame.dispose();
                    }
                }
            }
        });
    }

    private void setButtonAction() {
        startFrame.getBtnStart().addActionListener(l -> startFrame.dispose());

        mainFrame.getRunBtn().addActionListener(l -> {
            String rawInput = mainFrame.getInput().getText().trim();
            boolean header = mainFrame.getHeader().isSelected();
            boolean index = mainFrame.getIndex().isSelected();
            if ("".equals(rawInput))
                return;

            String[][] input = preProcessInput(rawInput);
            String date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

            currentObject = new HTMLObject(input, new boolean[]{header, index}, date);

            if (!outputFrame.isVisible())
                outputFrame.setVisible(true);

            outputFrame.getOutputTA().setText(currentObject.getTable());
        });

        mainFrame.getSaveBtn().addActionListener(l -> {
            if (currentObject == null) return;

            if (editMode) {
                currentObject.setId(editObject.getId());
                database.replaceObjectByID(currentObject);
                editMode = false;
            } else {
                currentObject.setId(HTMLObject.idIdentify++);
                database.addNewObject(currentObject);
            }

            try {
                database.saveToDB(currentObject);
                refreshLogTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(mainFrame, "Cannot save data",
                        "Save data error!", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainFrame.getClearBtn().addActionListener(l -> {
            mainFrame.getInput().setText("");
            outputFrame.getOutputTA().setText("");
            mainFrame.getHeader().setSelected(false);
            mainFrame.getIndex().setSelected(false);
            currentObject = null;
            editObject = null;
            editMode = false;
        });
    }

    private void setTableAction() {
        mainFrame.getDeleteRow().addActionListener(l -> {
            JTable table = mainFrame.getLogTable();
            int id = (int) tableModel.getValueAt(table.getSelectedRow(), 0);
            int delete_confirm = JOptionPane.showConfirmDialog(mainFrame, "Delete?", "Delete confirm", JOptionPane.OK_CANCEL_OPTION);
            if (delete_confirm == JOptionPane.OK_OPTION) {
                deletedRowInLogTable.add(id);
                database.deleteObjectByID(id);
                refreshLogTable();
            }
        });
        mainFrame.getEditRow().addActionListener(l -> {
            JTable table = mainFrame.getLogTable();
            int id = (int) tableModel.getValueAt(table.getSelectedRow(), 0);
            editObject = database.findObjectByID(id);
            editMode = true;
            mainFrame.getInput().setText(generateRawInput(editObject.getArr()));
            mainFrame.getHeader().setSelected(editObject.getHeader());
            mainFrame.getIndex().setSelected(editObject.getIndex());
            outputFrame.getOutputTA().setText(editObject.getTable());
            mainFrame.getTabPane().setSelectedIndex(0);
        });
        mainFrame.getLogTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JTable table = mainFrame.getLogTable();
                    int row = table.rowAtPoint(e.getPoint());
                    table.getSelectionModel().setSelectionInterval(row, row);
                    mainFrame.getPopupMenu().show(table, e.getX(), e.getY());
                }
            }
        });
    }

    //Utils Function
    private void refreshLogTable() {
        tableModel.fireTableDataChanged();
    }

    private String[][] preProcessInput(String rawInput) {
        String[] rows = rawInput.split("\n");
        String[][] input = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            String[] cells = row.split(",");
            input[i] = new String[cells.length];
            for (int j = 0; j < cells.length; j++) {
                String tmp = (cells[j].trim().length() > 0) ? cells[j].trim() : null;
                input[i][j] = tmp;
            }
        }
        return input;
    }

    private String generateRawInput(String[][] input) {
        StringBuilder rawInput = new StringBuilder();
        for (String[] row : input) {
            String tmp = String.join(", ", row);
            rawInput.append(tmp).append("\n");
        }
        return rawInput.toString();
    }

}
