package Model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private final String[] colName = new String[]{"ID", "Input", "Output", "Date"};
    private List<HTMLObject> myObjects = new ArrayList<>();

    public TableModel() {
    }

    public void setMyObjects(List<HTMLObject> myObjects) {
        this.myObjects = myObjects;
    }

    @Override
    public String getColumnName(int column) {
        return colName[column];
    }

    @Override
    public int getRowCount() {
        return myObjects.size();
    }

    @Override
    public int getColumnCount() {
        return colName.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        HTMLObject object = myObjects.get(rowIndex);
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
