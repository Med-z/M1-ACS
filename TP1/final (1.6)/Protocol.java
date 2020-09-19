
/**
 * Public class to support all the protocol's parameters
 * 
 * @author pmeseure
 */
public class Protocol {
    /** Server Port */
    static public final int PORT = 1234;

    // Include here any other value that must be shared between server and clients

    /************************
           STATUS CODES
    *************************/

    //OK STATUS
    static public final int OK = 200;
    static public final int OK_GOT_LANG = 201;
    static public final int OK_DISCONNECT = 202;
    //CLIENT ERROR
    static public final int ERROR = 400;
    static public final int ERROR_LANG = 402;
    static public final int ERROR_ZONE = 401;
    static public final int ERROR_SERVER_ACTION = 403;
    //SERVER ERROR
    static public final int ERROR_SERVER = 500;
};
