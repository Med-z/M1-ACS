import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Thread lauched for each client
 * 
 * @author pmeseure
 */
class ServerProcess implements Runnable {
    /** Socket towards the current client */
    private Socket client_socket;

    public ServerProcess(Socket socket) {
        client_socket = socket;
    }

    /** main routine */
    @Override
    public void run() {
        try {
            InputStream input = client_socket.getInputStream();
            OutputStream output = client_socket.getOutputStream();
            System.out.println(
                    "Connexion request from " + client_socket.getInetAddress() + ":" + client_socket.getPort());

            // Insert code to process request here : get requests from input stream
            // and send response to output stream

            ObjectInputStream objinput = new ObjectInputStream(input);
            ClientData data = (ClientData) objinput.readObject();
            System.out.println(data.getLanguage() == Language.ENGLISH);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}

/**
 * Server main class
 * 
 * @author pmeseure
 */
public class TCPServer {
    static public void launch(int port) {
        try {
            ServerSocket listen_socket;
            // get server's local address (Not necessary)
            // InetAddress iplocal=InetAddress.getLocalHost();
            // Create socket to get requests for all clients
            listen_socket = new ServerSocket(port, 10 /* ,iplocal */ );
            System.out.println("Server is waiting...");

            // Infinite loop to get requests sequentially
            while (true) {
                Socket socket;
                // get a socket corresponding to the client of the incoming request
                socket = listen_socket.accept();
                System.out.println("Connexion request received...");
                // Create a thread to process the incoming request
                Thread thread = new Thread(new ServerProcess(socket));
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            System.err.println("Usage: java " + TCPServer.class.getName());
            System.exit(1);
        }
        System.out.println("Launching of the server...");
        launch(Protocol.PORT);
    }
}
