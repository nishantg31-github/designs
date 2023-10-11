package ConnectionPooling;

import java.sql.*;

public class DAO {

    public static ResultSet executeSelectQuery(Connection connection, String query) throws SQLException {
        System.out.println("Running select query");
        Statement statement = null;
        ResultSet resultSet;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            assert statement != null;
//            statement.close();
        }
        return resultSet;
    }

    public static int executeUpdateQuery(Connection connection, String query) throws SQLException {
        System.out.println("Running DML query");
        PreparedStatement statement = null;
        int rowsUpdated;
        try {
            statement = connection.prepareStatement(query);
            rowsUpdated = statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            assert statement != null;
            statement.close();
        }
        return rowsUpdated;
    }
}
