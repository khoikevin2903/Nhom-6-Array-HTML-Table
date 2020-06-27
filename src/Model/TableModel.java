package Model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private final String[] colNames = new String[]{"ID", "Input", "Output", "Date"};
    private List<HTMLObject> logTableData = new ArrayList<>();

    public TableModel() {
    }

    public void setLogTableData(List<HTMLObject> data) {
        this.logTableData = data;
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }

    @Override
    public int getRowCount() {
        return logTableData.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        HTMLObject object = logTableData.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return object.getId();
            case 1:
                return object.getWritableData()[0];
            case 2:
                return object.getTable();
            case 3:
                return object.getDate();
        }
        return null;
    }
}
