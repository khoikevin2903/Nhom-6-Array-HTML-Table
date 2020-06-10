package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Database {
    private static final String DB_NAME = "ArrayToHTMLTable";
    private static volatile Database instance = null;
    private final List<HTMLObject> myObjects;
    private Connection cnn = null;

    private Database() {
        if (instance != null)
            throw new RuntimeException("Use getInstance method instead");
        myObjects = new ArrayList<>();
    }

    public static Database getInstance() {
        if (instance == null) synchronized (Database.class) {
            if (instance == null)
                instance = new Database();
        }
        return instance;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (cnn == null) {
            Class.forName("org.sqlite.JDBC");
            cnn = DriverManager.getConnection("jdbc:sqlite:DB/" + DB_NAME + ".db");
            createDB();
        }
    }

    public void disconnect() throws SQLException {
        if (cnn != null)
            this.cnn.close();
    }

    private void createDB() throws SQLException {
        if (cnn == null) return;
        try (Statement stm = cnn.createStatement()) {
            String query = "create table if not exists Log(" +
                    "id integer primary key autoincrement, " +
                    "input text ," +
                    "output text not null ," +
                    "date text not null );";
            stm.execute(query);
        }
    }

    public void loadDB() throws SQLException {
        if (cnn == null) return;
        myObjects.clear();
        String selectQuery = "select [id], [input], [output], [date] from Log order by [id]";
        Statement selectStm = cnn.createStatement();
        ResultSet resultSet = selectStm.executeQuery(selectQuery);
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            String input = resultSet.getString(2);
            String output = resultSet.getString(3);
            String date = resultSet.getString(4);
            myObjects.add(HTMLObject.createObjectFromProperty(id, input, output, date));
            HTMLObject.ID_IDENTIFY = Math.max(HTMLObject.ID_IDENTIFY, id);
        }
        HTMLObject.ID_IDENTIFY++;
        resultSet.close();
        selectStm.close();
    }

    public void saveToDB(HTMLObject object) throws SQLException {
        if (cnn == null) return;
        if (isDataExist(object))
            updateDB(object);
        else
            insertToDB(object);
    }

    private boolean isDataExist(HTMLObject object) throws SQLException {
        String selectQuery = "select count(*) from Log where id = ?";
        boolean res;
        try (PreparedStatement selectStm = cnn.prepareStatement(selectQuery)) {
            selectStm.setInt(1, object.getId());
            try (ResultSet selectResult = selectStm.executeQuery()) {
                selectResult.next();
                res = selectResult.getInt(1) != 0;
            }
        }
        return res;
    }

    private void updateDB(HTMLObject object) throws SQLException {
        String updateQuery = "update Log set [input] = ?, [output] = ?, [date] = ? where [id] = ?";
        try (PreparedStatement updateStm = cnn.prepareStatement(updateQuery)) {
            int index = 1;
            Object[] value = object.getWritableData();
            updateStm.setString(index++, String.valueOf(value[0]));
            updateStm.setString(index++, String.valueOf(value[1]));
            updateStm.setString(index++, String.valueOf(value[2]));
            updateStm.setInt(index, object.getId());
            updateStm.executeUpdate();
        }
    }

    private void insertToDB(HTMLObject object) throws SQLException {
        String insertSql = "insert into Log([input], [output] ,[date]) values (?,?,?)";
        try (PreparedStatement insertStm = cnn.prepareStatement(insertSql)) {
            int index = 1;
            Object[] insertValue = object.getWritableData();
            insertStm.setString(index++, String.valueOf(insertValue[0]));
            insertStm.setString(index++, String.valueOf(insertValue[1]));
            insertStm.setString(index, String.valueOf(insertValue[2]));
            insertStm.executeUpdate();
        }
    }

    private void deleteInDB(int id) throws SQLException {
        String deleteQuery = "delete from Log where id = " + id;
        Statement deleteStm = cnn.createStatement();
        deleteStm.executeUpdate(deleteQuery);
        deleteStm.close();
    }

    public HTMLObject findByID(int id) {
        for (HTMLObject myObject : myObjects)
            if (myObject.getId() == id)
                return myObject;
        return null;
    }

    public void addObject(HTMLObject object) {
        this.myObjects.add(object);
    }

    public void deleteObjectByID(int id) throws SQLException {
        deleteInDB(id);
        myObjects.removeIf(o -> o.getId() == id);
    }

    public void replaceByID(HTMLObject newObject) {
        int length = myObjects.size();
        for (int i = 0; i < length; i++) {
            if (myObjects.get(i).getId() == newObject.getId())
                myObjects.set(i, newObject);
        }
    }

    public List<HTMLObject> getMyObjects() {
        return Collections.unmodifiableList(myObjects);
    }

}
