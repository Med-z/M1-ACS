import java.io.DataInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.net.InetAddress;
import java.net.Socket;

import javax.sql.rowset.serial.SerialException;

/**
 * Main client class
 * 
 * @author pmeseure
 */
public class TCPClient {
    static public void dialog(String hostname) {
        try {
            Socket socket; // Socket connected to the server
            OutputStream output; // Output stream to send requests
            InputStream input; // Input stream to get responses

            // Get Server Address
            InetAddress svraddr = InetAddress.getByName(hostname);

            // Create Socket to Address using the port given by the protocol
            socket = new Socket(svraddr, Protocol.PORT);

            // Get streams
            output = socket.getOutputStream();
            input = socket.getInputStream();

            ObjectOutputStream objoutput = new ObjectOutputStream(output);
            // initialise the data
            ClientData objToSend = new ClientData(Language.SPANISH);
            // send the object
            objoutput.writeObject(objToSend);


            // process the responce of the server
            DataInputStream datainput = new DataInputStream(input);
            int errCode = datainput.readInt();
            if (errCode == Protocol.OK) {
                String returnMsg = datainput.readUTF();
                System.out.println(returnMsg);
            } else {
                System.out.println("Error ; Code : " + errCode);
            }    

            //close the socket
            socket.close();

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage : java " + TCPClient.class.getName() + " hostname");
            System.exit(1);
        }
        dialog(args[0]);
    }
}
