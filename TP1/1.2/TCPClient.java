import java.io.DataInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Main client class
 * 
 * @author pmeseure
 */
public class TCPClient {
    static public void dialog(final String hostname) {
        try {
            Socket socket; // Socket connected to the server
            OutputStream output; // Output stream to send requests
            InputStream input; // Input stream to get responses

            // Get Server Address
            final InetAddress svraddr = InetAddress.getByName(hostname);

            // Create Socket to Address using the port given by the protocol
            socket = new Socket(svraddr, Protocol.PORT);

            // Get streams
            output = socket.getOutputStream();
            input = socket.getInputStream();

            final ObjectOutputStream objoutput = new ObjectOutputStream(output);
            // initialise the data
            final var zone = 12;
            final var name = "Julien";
            final var language = Language.ENGLISH;
            final var serverAction = ServerAction.TIME;
            final ClientData objToSend = new ClientDataZone(language,serverAction, zone);
            // send the object
            objoutput.writeObject(objToSend);

            // process the responce of the server
            final DataInputStream datainput = new DataInputStream(input);
            final int errCode = datainput.readInt();
            if (errCode == Protocol.OK) {
                final String returnMsg = datainput.readUTF();
                System.out.println(returnMsg);
            } else {
                System.out.println("Error ; Code : " + errCode);
            }

            // close the socket
            socket.close();

        } catch (final Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void main(final String[] args) {
        if (args.length != 1) {
            System.err.println("Usage : java " + TCPClient.class.getName() + " hostname");
            System.exit(1);
        }
        dialog(args[0]);
    }
}
