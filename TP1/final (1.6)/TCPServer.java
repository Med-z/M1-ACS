import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


class Counter {    
    private int count;
    public Counter(){
        count = 0;
    }
    public void incr(){ count++;}
    public void decr(){ count--;}
    public int getCount(){return count;}
}
/**
 * Thread lauched for each client
 * 
 * @author pmeseure
 */
class ServerProcess implements Runnable {
    /** Socket towards the current client */
    private Socket client_socket;

    private Language languageClient;
    private boolean isClosed;
    Counter processNumber;

    public ServerProcess(Socket socket, Counter processNumber) {
        client_socket = socket;
        // we use null as "not yet given by the client "
        // and check at every connection; if the value
        // is at null, this is the first connection for
        // the client.
        this.languageClient = null;
        this.isClosed = false;
        this.processNumber = processNumber;
    }

    /** main routine */
    @Override
    public void run() {
        synchronized(this.processNumber){            
            this.processNumber.incr();
        }
        try {
            var input = client_socket.getInputStream();
            var output = client_socket.getOutputStream();
            var objinput = new ObjectInputStream(input);
            var dataoutput = new DataOutputStream(output);
            
            do {
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
            } while(!this.isClosed);
            //close client socket
            client_socket.close();
            printWithInfos("Disconnect client");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            synchronized(this.processNumber){
                this.processNumber.decr();
            }
        }

    }

    private void handleConnectedRequest(ObjectInputStream objinput, DataOutputStream dataoutput) throws Exception{
        try {
            // retrieve the data class
            var data = (ClientDataAction) objinput.readObject();

            switch (data.getServerAction()) {
                case HELLO:
                    handleHelloRequest(data, dataoutput);  
                    break;
                case TIME:
                    handleTimeRequest(data, dataoutput);
                    break;
                case DISCONNECT:
                    handleDisconnectRequest(dataoutput);
                    break;
                case CHANGE_LANG:
                    handleChangeLangRequest(data, dataoutput);
                    break;
                default:
                    // send an error code
                    printWithInfos("Server action incorrect");
                    dataoutput.writeInt(Protocol.ERROR_SERVER_ACTION);
                    break;
            }
        } catch (LanguageUnknownExeption e) { 
            printWithInfos("Error Language unknown");
            // send an error code
            try {
                dataoutput.writeInt(Protocol.ERROR_LANG);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (Exception e) {
            printWithInfos("Error Server");
            try {
                dataoutput.writeInt(Protocol.ERROR_SERVER);
            } catch (IOException ioException) {
                //this the case where the client has been disconnected
                ioException.printStackTrace();
            } 
            //retrow the exception so we can update properly the client count 
            throw e;
            
        }

    }

    private void handleTimeRequest(ClientData data, DataOutputStream dataoutput) throws Exception, LanguageUnknownExeption{
        
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

    private void handleHelloRequest(ClientData data, DataOutputStream dataoutput) throws Exception, LanguageUnknownExeption{
        printWithInfos("Request Hello...");

        var newData = (ClientDataName) data;

        var message = this.getHelloString() + " " + newData.getName();

        // send an ok code
        dataoutput.writeInt(Protocol.OK);

        // send the message to the client
        dataoutput.writeUTF(message);

        printWithInfos("Response send to client");
    }

    private void handleDisconnectRequest(DataOutputStream dataoutput) throws Exception, LanguageUnknownExeption{
        printWithInfos("Request disconnect");
        
        this.isClosed = true;
        //send to client that we diconnect 
        dataoutput.writeInt(Protocol.OK_DISCONNECT);
    }

    private void handleChangeLangRequest(ClientData data, DataOutputStream dataoutput) throws Exception, LanguageUnknownExeption{
        printWithInfos("Request change of language");

        // converts data
        var newData = (ClientDataLanguage) data;         
        //asign new language
        this.languageClient = newData.getLanguage();
        //send ok status message
        dataoutput.writeInt(Protocol.OK);
    }

    private String getHelloString() throws LanguageUnknownExeption {
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
                throw new LanguageUnknownExeption();
        }
    }

    private String getTimeString(int zone) throws LanguageUnknownExeption {
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
                throw new LanguageUnknownExeption();
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

            var counter = new Counter();
            final var maxProcess = 2;
            // Infinite loop to get requests sequentially
            while (true) {
                Socket socket;
                // get a socket corresponding to the client of the incoming request
                socket = listen_socket.accept();
                System.out.println("Connexion request received...");
                synchronized(counter){
                    if (counter.getCount() < maxProcess) {
                        System.out.println("Got " + counter.getCount() + " connection(s)");
                        // Create a thread to process the incoming request
                        Thread thread = new Thread(new ServerProcess(socket, counter));
                        thread.start();                        
                    } else {
                        System.out.println("Too many thread;\nReject connection");
                        socket.close();
                    }
                }
                
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
