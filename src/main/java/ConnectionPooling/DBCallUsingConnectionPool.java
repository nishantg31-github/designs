package ConnectionPooling;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class DBCallUsingConnectionPool implements Callable<String> {
    private final String query;
    private final String type;
    private final ConnectionPoolImp connectionPoolImp;

    public DBCallUsingConnectionPool(String dbCall, String type, ConnectionPoolImp connectionPoolImp) {
        this.query = dbCall;
        this.type = type;
        this.connectionPoolImp = connectionPoolImp;
    }

    @Override
    public String call() throws InterruptedException {

        StringBuilder result = new StringBuilder();
        int rowsAffected;
        if (type.equals("SELECT")) {
            System.out.println("Running select query from pool");
            try (ResultSet resultSet = connectionPoolImp.executeQueryFromPool(query)) {
                while (resultSet.next()) {
                    result.append(resultSet.getString("name")).append(", ");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Running update query from pool");
            rowsAffected = connectionPoolImp.executeUpdateQueryFromPool(query);
            result.append(rowsAffected);
        }

        return result.toString();
    }
}
