package ConnectionPooling;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws Exception {
        // Create an HttpServer on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        ConnectionPoolImp connectionPool = new ConnectionPoolImp(2);

        // Create a context for the "/users" endpoint and set a handler
        server.createContext("/users", new RequestHandler(connectionPool));

        // Start the server
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server started on port 8001");
    }
}
