package ConnectionPooling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class ConnectionPoolImp {
    private static final String HOST = "localhost";
    private static final String DATABASE = "test";
    private static final String URL = "jdbc:mysql://" + HOST + "/" + DATABASE;
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    private static int LIMIT = 2;
    public static Queue<Connection> pool;

    public ConnectionPoolImp() {
        init();
        if (Objects.isNull(pool)) {
            pool = new LinkedList<>();
        }
        addConnectionInPool();
    }

    public ConnectionPoolImp(int limit) {
        init();
        LIMIT = limit;
        if (Objects.isNull(pool)) {
            pool = new LinkedList<>();
        }
        addConnectionInPool();
        System.out.println("Connection Pool size : " + pool.size());
    }

    public void init() {
        try {
            String driverName = "com.mysql.cj.jdbc.Driver";
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQueryFromPool(String query) {
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = getConnectionFromPool();
            resultSet = DAO.executeSelectQuery(connection, query);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            pool.offer(connection);
        }
        return resultSet;
    }

    public int executeUpdateQueryFromPool(String query) throws InterruptedException {
        int rowsAffected = 0;
        Connection connection = getConnectionFromPool();
        try {
            rowsAffected = DAO.executeUpdateQuery(connection, query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.offer(connection);
        }
        return rowsAffected;
    }

    public ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = getConnection();
            resultSet = DAO.executeSelectQuery(connection, query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert connection != null;
            releaseConnection(connection);
        }
        return resultSet;
    }

    public int executeUpdateQuery(String query) {
        int rowsAffected = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            rowsAffected = DAO.executeUpdateQuery(connection, query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert connection != null;
            releaseConnection(connection);
        }
        return rowsAffected;
    }

    private void releaseConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addConnectionInPool() {
        try {
            for (int i = 0; i < LIMIT; i++) {
                pool.offer(getConnection());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnectionFromPool() throws InterruptedException {
        Connection connection = null;
        while (true) {
            if(!pool.isEmpty()) {
                connection = pool.poll();
                System.out.println("Pool size after giving connection : " + pool.size());
                break;
            }
            System.out.println("Waiting for a connection ..");
            Thread.sleep(1000);
        }
        return connection;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
