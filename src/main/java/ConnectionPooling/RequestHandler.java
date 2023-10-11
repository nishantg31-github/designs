package ConnectionPooling;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class RequestHandler implements HttpHandler {
    ConnectionPoolImp connectionPool;

    public RequestHandler(ConnectionPoolImp connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if("GET".equals(exchange.getRequestMethod())) {
            System.out.println("Got a GET request");
        }

        String response = handleUserRequest(connectionPool);

        sendResponse(exchange, response);

    }

    private String handleUserRequest(ConnectionPoolImp connectionPool) {
//        String query = "SELECT * FROM users";
//        String query = "INSERT INTO users (name, age, address) VALUES ('Nisha', 23, 'Mumbai');";
        String query = "SELECT SLEEP(10) as name";

        // Using connection Pool
        Callable<String> dbPoolQueryCall = new DBCallUsingConnectionPool(query, "SELECT", connectionPool);
        FutureTask<String> futurePoolTask = new FutureTask<>(dbPoolQueryCall);
        Thread dbPoolThread = new Thread(futurePoolTask);
        dbPoolThread.start();

        // Without Connection Pool
//        Callable<String> dbQueryCall = new DBCallWithoutConnectionPool(query, "INSERT", connectionPool);
//        FutureTask<String> futureTask = new FutureTask<>(dbQueryCall);
//        Thread dbThread = new Thread(futureTask);
//        dbThread.start();

        String result = "x";
        try {
            result = Objects.nonNull(futurePoolTask.get()) ? futurePoolTask.get() : result;
            System.out.println("Query Result: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.flush();
        os.close();
    }
}
