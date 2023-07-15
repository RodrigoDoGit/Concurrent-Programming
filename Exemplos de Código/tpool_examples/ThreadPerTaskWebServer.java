

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ThreadPerTaskWebServer {
  static final int PORT = 80;
    public static void main(String[] args) throws IOException {
        @SuppressWarnings("resource")
        ServerSocket socket = new ServerSocket(PORT);
        while (true) {
            Socket connection = socket.accept();
            Runnable task = () -> handleRequest(connection);
            new Thread(task).start();
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}