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
            final var language = Language.SPANISH;

            final ClientData objToSend = new ClientDataInit(language);
            // send the initialisation object
            objoutput.writeObject(objToSend);

            // process the responce of the server
            final var datainput = new DataInputStream(input);
            final var returnCode = datainput.readInt();
            if (returnCode == Protocol.OK_GOT_LANG || returnCode == Protocol.OK) {
                
                System.out.println("Got Return Code : " + returnCode);

                final ClientData objToSend2 = new ClientDataName(name);
                // send the object
                objoutput.writeObject(objToSend2);
                
                final var returnCode2 = datainput.readInt();

                if (returnCode2 == Protocol.OK) {
                    System.out.println("Got Return Code : " + returnCode2);
                    final var returnMsg = datainput.readUTF();
                    System.out.println(returnMsg);

                    //third call to server
                    final ClientData objToSend3 = new ClientDataLanguage(Language.ENGLISH);
                    objoutput.writeObject(objToSend3);
                    System.out.println("Got Return Code : " + datainput.readInt());//should check this return code

                    //TEMP:
                    Thread.sleep(5 * 1000);//allow me to start other clients
                                        
                    // fourth call to server
                    objoutput.writeObject(new ClientDataZone(zone));
                    System.out.println("Got Return Code : " + datainput.readInt());//should check this return code
                    System.out.println(datainput.readUTF());

                    // fifth and last call to server
                    objoutput.writeObject(new ClientDataAction(ServerAction.DISCONNECT));
                    System.out.println("Got Return Code : " + datainput.readInt());//should check this return code                   
                    
                } else {
                    System.out.println("Error ; Code : " + returnCode2);
                }        

            } else {
                System.out.println("Error ; Code : " + returnCode);
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
