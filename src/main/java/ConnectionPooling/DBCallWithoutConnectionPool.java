package ConnectionPooling;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class DBCallWithoutConnectionPool implements Callable<String> {
    private final String query;
    private final String type;
    private final ConnectionPoolImp connectionPoolImp;

    public DBCallWithoutConnectionPool(String dbCall, String type, ConnectionPoolImp connectionPoolImp) {
        this.query = dbCall;
        this.type = type;
        this.connectionPoolImp = connectionPoolImp;
    }

    @Override
    public String call() throws InterruptedException {
        System.out.println("Making db query using without connection pool");
        System.out.println("Using thread : " + Thread.currentThread().getName());

        StringBuilder result = new StringBuilder();
        int rowsAffected;
        if (type.equals("SELECT")) {
            System.out.println("Running select query");
            try (ResultSet resultSet = connectionPoolImp.executeQuery(query)) {
                while (resultSet.next()) {
                    result.append(resultSet.getString("name")).append(", ");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Running update query");
            rowsAffected = connectionPoolImp.executeUpdateQuery(query);
            result.append(rowsAffected);
        }

        return result.toString();
    }
}
