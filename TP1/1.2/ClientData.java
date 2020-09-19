import java.io.Serializable;

/**
 * ClientData
 * 
 * A dataclass to store the info the client need to send to
 * the server
 */
public abstract class ClientData implements Serializable {

    private static final long serialVersionUID = 2;
    
    final private Language language;
    final private ServerAction serverAction;

    public ClientData(final Language language, final ServerAction serverAction){
        this.language = language;
        this.serverAction = serverAction;
    }

    final public Language getLanguage() {
        return language;
    }

    final public ServerAction getServerAction() {
        return serverAction;
    }   
}