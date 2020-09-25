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

            // send the initialisation object
            objoutput.writeObject(new ClientDataInit(language));

            // process the responce of the server
            final var datainput = new DataInputStream(input);
            final var returnCode = datainput.readInt();
            if (returnCode == Protocol.OK_GOT_LANG || returnCode == Protocol.OK) {
                
                System.out.println("Got Return Code : " + returnCode);
                // send the object
                objoutput.writeObject(new ClientDataName(name));
                
                final var returnCode2 = datainput.readInt();

                if (returnCode2 == Protocol.OK) {
                    System.out.println("Got Return Code : " + returnCode2);
                    System.out.println(datainput.readUTF());

                    //third call to server
                    objoutput.writeObject(new ClientDataLanguage(Language.ENGLISH));
                    System.out.println("Got Return Code : " + datainput.readInt());//should check this return code

                    //TEMP:
                    Thread.sleep(5 * 1000);//allow me to start other clients
                                        
                    // fourth call to server
                    objoutput.writeObject(new ClientDataZone(zone));
                    System.out.println("Got Return Code : " + datainput.readInt());//should check this return code
                    System.out.println(datainput.readUTF());

                    // fifth and last call to server
                    objoutput.writeObject(new ClientDataDisconnect());
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
