package Model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HTMLObject {
    private static final transient Gson GSON = new Gson();
    public static int idIdentify = 1;
    private final String[][] arr;
    private final Boolean header, index;
    private final String table;
    private final String date;
    private transient int id;

    public HTMLObject(String[][] input, boolean[] flag, String date) {
        this.arr = input;
        this.header = flag[0];
        this.index = flag[1];
        this.date = date;
        this.table = this.toTable();
    }

    /**
     * Cho phép tạo một đối tượng từ dữ liệu trong database
     *
     * @param id     Id của đối tượng
     * @param input  Đầu vào
     * @param output Đầu ra
     * @param date   Thời điểm lưu đối tượng
     * @return đối tượng được tạo
     */
    public static HTMLObject createObjectFromProperty(int id, String input, String output, String date) {
        String[] tmp = input.split(", ");
        String arr = "\"arr\":" + tmp[0];
        String header = "\"header\":" + tmp[1];
        String index = "\"index\":" + tmp[2];
        String table = "\"table\":" + "\"" + output + "\"";
        date = "\"date\":" + date;
        String[] properties = new String[]{arr, header, index, table, date};
        final String json = "{" + String.join(",", properties) + "}";
        HTMLObject object = GSON.fromJson(json, HTMLObject.class);
        object.setId(id);
        return object;
    }

    /**
     * Thực thi thuật toán theo yêu cầu đề bài
     *
     * @return chuỗi biểu diện đầu vào dưới dạng các thẻ HTML
     */
    private String toTable() {
        StringBuilder table = new StringBuilder("<table>\n");
        if (header) {
            StringBuilder head = new StringBuilder("    <thead>\n        <tr>\n");
            if (index)
                head.append("            <th></th>\n");
            for (Object item : arr[0])
                head.append("            <th>").append(item != null ? item : "").append("</th>\n");
            head.append("        </tr>\n    </thead>\n");
            table.append(head);
        }
        StringBuilder body = new StringBuilder("    <tbody>\n");
        int base_index = 1;
        int start_index = (header ? 1 : 0);
        for (int i = start_index; i < arr.length; i++) {
            StringBuilder row = new StringBuilder("        <tr>\n");
            if (index)
                row.append("            <td>").append(base_index++).append("</td>\n");
            for (Object item : arr[i])
                row.append("            <td>").append(item != null ? item : "").append("</td>\n");
            row.append("        </tr>\n");
            body.append(row);
        }
        body.append("    </tbody>\n");
        table.append(body).append("</table>");

        return table.toString();
    }

    /**
     * @return Mảng chứa dữ liệu dùng để lưu vào database
     */
    public Object[] getWritableData() {
        String json = GSON.toJson(this);
        JsonObject jsonObject = GSON.fromJson(json, JsonObject.class);
        JsonElement arr = jsonObject.get("arr");
        boolean header = jsonObject.get("header").getAsBoolean();
        boolean index = jsonObject.get("index").getAsBoolean();
        String table = jsonObject.get("table").getAsString();
        JsonElement date = jsonObject.get("date");
        return new Object[]{arr.toString() + ", " + header + ", " + index, table, date.toString()};
    }

    /* Getter and Setter */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[][] getArr() {
        return arr;
    }

    public Boolean getHeader() {
        return header;
    }

    public Boolean getIndex() {
        return index;
    }

    public String getDate() {
        return date;
    }

    public String getTable() {
        return table;
    }

}
