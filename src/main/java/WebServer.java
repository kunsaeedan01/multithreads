import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Simple web server.
 */
public class WebServer {
    public static void main(String[] args) {
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8000;
        int queueLength = args.length > 2 ? Integer.parseInt(args[2]) : 50;
        int numOfThreads = args.length > 1 ? Integer.parseInt(args[1]) : 4;

        try (ServerSocket serverSocket = new ServerSocket(port, queueLength)) {
            System.out.println("Web Server is starting up, listening at port " + port + ".");
            System.out.println("You can access http://localhost:" + port + " now.");
            ThreadSafeQueue<Processor> q = new ThreadSafeQueue<>();

            for (int i = 0; i < numOfThreads; i++)
            {
                Consumer consumer = new Consumer(i, q);
                consumer.start();
            }

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("Got connection!");

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                try {
                    HttpRequest request = HttpRequest.parse(input);
                    q.add(new Processor(socket, request));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            System.out.println("Server has been shutdown!");
        }
    }
}