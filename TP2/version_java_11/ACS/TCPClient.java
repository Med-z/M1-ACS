package ACS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

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

            scenario1(output, input);

            // close the socket
            socket.close();

        } catch (final Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * The scenario goes like this:
     * -init with spanish
     * -ask hello with "NAME"
     * -change language to english
     * -ask for time with zone = 12
     * -disconnect
     * @throws Exception
     */
    static void scenario1(OutputStream output, InputStream input) throws Exception{

        Gson gson = new GsonBuilder().setPrettyPrinting().create(); 

        // initialise the data
        var zone = 12;
        var name = "NAME";
        var language = Language.SPANISH;

        // send the initialisation object
        output.write( 
            gson.toJson(new ClientDataInit(language)).getBytes("UTF-8")
        );

        /*PrintWriter writer=new PrintWriter(new OutputStreamWriter(output,"UTF-8"));
        writer.println(gson.toJson(new ClientDataInit(language)));*/
        
        // process the responce of the server
        var reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        String line;
        var complete_string = "";
        do {        
            line = reader.readLine();    
            System.out.println(line);        
            if (line != null)
                complete_string += line;
        } while (line != null);

        System.out.println(complete_string);
    
        var returnCode = 200;

        if (returnCode == Protocol.OK_GOT_LANG || returnCode == Protocol.OK) {
            
            System.out.println("Got Return Code : " + returnCode);
            // send the object
            /*objoutput.writeObject(new ClientDataName(name));
            
            final var returnCode2 = datainput.readInt();

            if (returnCode2 == Protocol.OK) {
                System.out.println("Got Return Code : " + returnCode2);
                System.out.println(datainput.readUTF());

                //third call to server
                objoutput.writeObject(new ClientDataLanguage(Language.ENGLISH));
                System.out.println("Got Return Code : " + datainput.readInt());//should check this return code

                //Thread.sleep(5 * 1000);//allow me to start other clients
                                    
                // fourth call to server
                objoutput.writeObject(new ClientDataZone(zone));
                System.out.println("Got Return Code : " + datainput.readInt());//should check this return code
                System.out.println(datainput.readUTF());

                // fifth and last call to server
                objoutput.writeObject(new ClientDataDiconnect());
                System.out.println("Got Return Code : " + datainput.readInt());//should check this return code                   
                
            } else {
                System.out.println("Error ; Code : " + returnCode2);
            }        
            */
        } else {
            System.out.println("Error ; Code : " + returnCode);
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
