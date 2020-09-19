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
            var input = client_socket.getInputStream();
            var output = client_socket.getOutputStream();
            var objinput = new ObjectInputStream(input);
            var dataoutput = new DataOutputStream(output);
            
            for (int i = 0; i < 2; i++) {
                printWithInfos("Connection");

                if (this.languageClient == null) {
                    // case of first connection
                    try {

                        printWithInfos("First connection");

                        var data = (ClientDataInit) objinput.readObject();
                        this.languageClient = data.getLanguage();

                        // send confirmation to the client that
                        // we have the language register and that he
                        // could send another request
                        dataoutput.writeInt(Protocol.OK_GOT_LANG);

                        printWithInfos("Language acquired; \nResponse send to client");

                    } catch (Exception e) {
                        printWithInfos("Error Language unknown (or may be wrong object given)");
                        dataoutput.writeInt(Protocol.ERROR_LANG);
                    }
                } else {
                    printWithInfos("Known client");

                    handleConnectedRequest(objinput, dataoutput);
                }
            }
            client_socket.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }

    private void handleConnectedRequest(ObjectInputStream objinput, DataOutputStream dataoutput) {
        try {
            // retrieve the data class
            var data = (ClientDataAction) objinput.readObject();

            if (data.getServerAction() == ServerAction.HELLO) {

                printWithInfos("Request Hello...");

                var newData = (ClientDataName) data;

                var message = this.getHelloString() + " " + newData.getName();

                // send an error code
                dataoutput.writeInt(Protocol.OK);

                // send the message to the client
                dataoutput.writeUTF(message);

                printWithInfos("Response send to client");
            } else {

                printWithInfos("Request the time...");

                var newData = (ClientDataZone) data;

                // check if zone is within a correct interval
                var zone = newData.getZone();

                if (zone > 14 || zone < -12) {
                    printWithInfos("Error : Incorrect time zone");
                    // send the appropriate error code
                    dataoutput.writeInt(Protocol.ERROR_ZONE);
                } else {
                    // extract data and get appropriate date
                    var message = this.getTimeString(zone);

                    // send an error code
                    dataoutput.writeInt(Protocol.OK);

                    // send the message to the client
                    dataoutput.writeUTF(message);

                    printWithInfos("Response send to client");
                }
            }
        } catch (Exception e) {
            // send an error code
            printWithInfos("Error Language unknown");
            try {
                dataoutput.writeInt(Protocol.ERROR_LANG);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    private String getHelloString() throws Error {
        // we should consider moving those strings into
        // static variables
        switch (this.languageClient) {
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

    private String getTimeString(int zone) throws Error {
        // code copy/paste from TP specification
        var dateformatter = new SimpleDateFormat("hh:mm:ss");
        dateformatter.setTimeZone(TimeZone.getTimeZone("GMT+" + zone));
        // zone is the selected time zone
        var now = new Date();
        var displaytime = dateformatter.format(now);

        switch (this.languageClient) {
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

    private void printWithInfos(String msg) {
        System.out.println(new SimpleDateFormat("hh:mm:ss").format(new Date()) + " :: From "
                + client_socket.getInetAddress() + ":" + client_socket.getPort() + " \n==> " + msg);
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
