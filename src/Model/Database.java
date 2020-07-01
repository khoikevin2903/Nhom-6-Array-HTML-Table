package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Database {

    /**
     * Tên của database
     */
    private static final String DB_NAME = "ArrayToHTMLTable";

    /**
     * Đối tượng duy nhất giữ kết nối đến database
     */
    private static Database instance = null;

    /**
     * List dùng để lưu dữ liệu ở trong database để hiển thị ra LogTable
     */
    private final List<HTMLObject> myObjects;

    /**
     * Đối tượng thuộc lớp Connection, dùng để thao tác với database
     */
    private Connection cnn = null;

    private Database() {
        if (instance != null)
            throw new RuntimeException("Use getInstance method!");
        myObjects = new ArrayList<>();
    }

    public static Database getInstance() {
        if (instance == null)
            instance = new Database();
        return instance;
    }

    /*
     * Các phương thức dưới đây tác động vào dữ liệu trong database
     * */

    /**
     * Mở kết nối tới cơ sở dữ liệu
     *
     * @throws ClassNotFoundException khi không nạp được driver
     * @throws SQLException khi không thể kết nối vào database, nên kiểm tra lại chuỗi kết nối
     */
    public void connectDB() throws ClassNotFoundException, SQLException {
        if (cnn == null) {
            Class.forName("org.sqlite.JDBC");
            cnn = DriverManager.getConnection("jdbc:sqlite:db/" + DB_NAME + ".db");
            createDB();
        }
    }

    /**
     * Đóng kết nối tới database
     *
     * @throws SQLException khi việc truy cập vào database có lỗi xảy ra
     */
    public void disconnectDB() throws SQLException {
        if (cnn != null)
            this.cnn.close();
    }

    /**
     * Có nhiệm vụ tạo database trong lần chạy đầu tiên
     *
     * @throws SQLException khi có lỗi truy cập vào database
     */
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

    /**
     * Lấy dữ liệu từ database và lưu vào list
     *
     * @throws SQLException khi truy cập database có lỗi
     */
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
            HTMLObject.idIdentify = Math.max(HTMLObject.idIdentify, id);
        }
        HTMLObject.idIdentify++;
        resultSet.close();
        selectStm.close();
    }

    /**
     * Lưu một đối tượng vào database. Nếu đã tồn tại thì tiến hành cập nhật,
     * ngược lại thì thêm mới.
     *
     * @param object Đối tượng chứa dữ liệu cần lưu vào database
     * @throws SQLException khi có lỗi truy cập database
     */
    public void saveToDB(HTMLObject object) throws SQLException {
        if (cnn == null) return;
        //Kiểm tra sự tồn tại của đối tượng trong database thông qua Id
        if (isExistObjectHasID(object.getId()))
            //Nếu đã tồn tại, tiến hành cập nhật lại
            updateExistedObject(object);
        else
            //Nếu chưa, tiến hành thêm mới
            insertNewObject(object);
    }

    /**
     * Kiểm tra sự tồn tại của đối tượng trong database thông qua ID
     *
     * @param id id cần kiểm tra
     * @return true - đã tồn tại, ngược lại - false
     * @throws SQLException khi có lỗi truy cập
     */
    private boolean isExistObjectHasID(int id) throws SQLException {
        String selectQuery = "select count(*) from Log where id = ?";
        boolean isExist;
        try (PreparedStatement selectStm = cnn.prepareStatement(selectQuery)) {
            selectStm.setInt(1, id);
            try (ResultSet selectResult = selectStm.executeQuery()) {
                selectResult.next();
                isExist = selectResult.getInt(1) != 0;
            }
        }
        return isExist;
    }

    private void updateExistedObject(HTMLObject object) throws SQLException {
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

    private void insertNewObject(HTMLObject object) throws SQLException {
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

    /**
     * Xóa một bản ghi lịch sử trong database thông qua Id
     *
     * @param id Id của bản ghi cần xóa trong database
     * @throws SQLException khi có lỗi truy cập
     */
    public void deleteRowInLogTableByID(int id) throws SQLException {
        String deleteQuery = "delete from Log where id = " + id;
        Statement deleteStm = cnn.createStatement();
        deleteStm.executeUpdate(deleteQuery);
        deleteStm.close();
    }

    /*
     * Các phương thức dưới đây tác động vào list chứa dữ liệu để hiển thị ra LogTable.
     * Không tác động đến dữ liệu trong database
     * */

    /**
     * Tìm một đối tượng trong list thông qua Id
     *
     * @param id Id của đối tượng cần tìm
     * @return trả về đối tượng được tìm thấy, nếu không tìm thấy trả về null
     */
    public HTMLObject findObjectByID(int id) {
        for (HTMLObject myObject : myObjects)
            if (myObject.getId() == id)
                return myObject;
        return null;
    }

    /**
     * Thêm một đối tượng mới vào list hiện tại để hiển thị ra LogTable
     *
     * @param object Đối tượng chứa dữ liệu
     */
    public void addNewObject(HTMLObject object) {
        this.myObjects.add(object);
    }

    /**
     * Xóa một đối tượng trong list hiện tại bằng Id
     *
     * @param id Id của đối tượng cần xóa
     */
    public void deleteObjectByID(int id) {
        myObjects.removeIf(o -> o.getId() == id);
    }

    /**
     * Thay thế một đối tượng đã tồn tại bằng một đối tượng mới thông qua Id
     *
     * @param newObject đối tượng mới
     */
    public void replaceObjectByID(HTMLObject newObject) {
        int length = myObjects.size();
        for (int i = 0; i < length; i++) {
            if (myObjects.get(i).getId() == newObject.getId())
                myObjects.set(i, newObject);
        }
    }

    /**
     * Dùng để lấy dữ liệu hiển thị ra LogTable
     *
     * @return list chứa các đối tượng hiện tại
     */
    public List<HTMLObject> getDataInDB() {
        return Collections.unmodifiableList(myObjects);
    }

}
