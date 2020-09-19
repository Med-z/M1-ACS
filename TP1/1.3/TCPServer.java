import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Thread lauched for each client
 * 
 * @author pmeseure
 */
class ServerProcess implements Runnable {
    /** Socket towards the current client */
    private Socket client_socket;

    private Language languageClient;

    public ServerProcess(Socket socket) {
        client_socket = socket;
        // we use null as "not yet given by the client "
        // and check at every connection; if the value
        // is at null, this is the first connection for
        // the client.
        this.languageClient = null;
    }

    /** main routine */
    @Override
    public void run() {
        try {
            InputStream input = client_socket.getInputStream();
            OutputStream output = client_socket.getOutputStream();
            System.out.println(
                    "Connexion request from " + client_socket.getInetAddress() + ":" + client_socket.getPort());

            ObjectInputStream objinput = new ObjectInputStream(input);
            DataOutputStream dataoutput = new DataOutputStream(output);

            if (this.languageClient == null) {

            } else {
                handleConnectedRequest(objinput, dataoutput);
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void handleConnectedRequest(ObjectInputStream objinput, DataOutputStream dataoutput) {
        try {
            // retrieve the data class
            ClientData data = (ClientData) objinput.readObject();

            if (data.getServerAction() == ServerAction.HELLO) {

                System.out.println("Request Hello...");

                var message = this.getHelloString(data.getLanguage()) + " " + data.getName();

                // send an error code
                dataoutput.writeInt(Protocol.OK);

                // send the message to the client
                dataoutput.writeUTF(message);

                System.out.println("Response send to client");
            } else {

                System.out.println("Request time...");

                var newData = (ClientDataZone) data;
                // check if zone is within a correct interval
                final var zone = newData.getZone();

                if (zone > 14 || zone < -12) {
                    System.out.println("Error : Incorrect time zone");
                    // send the appropriate error code
                    dataoutput.writeInt(Protocol.ERROR_ZONE);
                } else {
                    // extract data and get appropriate date
                    var message = this.getTimeString(data.getLanguage(), zone);

                    // send an error code
                    dataoutput.writeInt(Protocol.OK);

                    // send the message to the client
                    dataoutput.writeUTF(message);

                    System.out.println("Response send to client");
                }
            }
        } catch (Exception e) {
            // send an error code
            System.out.println("Error Language unknown");
            try {
                dataoutput.writeInt(Protocol.ERROR_LANG);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    private String getHelloString(Language language) throws Error {
        // we should consider moving those strings into
        // static variables
        switch (language) {
            case ENGLISH:
                return "Hello";
            case FRENCH:
                return "Bonjour";
            case SPANISH:
                return "Holla";
            default:
                throw new Error("Langue inconnue");
        }
    }

    private String getTimeString(Language language, int zone) throws Error {
        // code copy/paste from TP specification
        SimpleDateFormat dateformatter = new SimpleDateFormat("hh:mm:ss");
        dateformatter.setTimeZone(TimeZone.getTimeZone("GMT+" + zone));
        // zone is the selected time zone
        Date now = new Date();
        String displaytime = dateformatter.format(now);

        switch (language) {
            case ENGLISH:
                return "It's " + displaytime;
            case FRENCH:
                return "Il est " + displaytime;
            case SPANISH:
                return "Es " + displaytime;
            default:
                throw new Error("Langue inconnue");
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
