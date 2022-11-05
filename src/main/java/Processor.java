import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class Processor extends Thread {
    private final Socket socket;
    private final HttpRequest request;

    public Processor(Socket socket, HttpRequest request) {
        this.socket = socket;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process() throws IOException {
        System.out.println("Got request:");
        System.out.println(request.toString());
        System.out.flush();

        PrintWriter output = new PrintWriter(socket.getOutputStream());

        if(request.getRequestLine().contains("/create/itemid"))
        {
            TemplateCase.createID(output);
            socket.close();
        }

        else if(request.getRequestLine().contains("/delete/itemid"))
        {
            TemplateCase.deleteID(output);
            socket.close();
        }

        else if(request.getRequestLine().contains("/exec/params"))
        {
            TemplateCase.params(output);
            socket.close();
        }
        else
        {
            TemplateCase.defaultPage(output);
            socket.close();
        }
    }
}