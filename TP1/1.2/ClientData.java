import java.io.Serializable;

/**
 * ClientData
 * 
 * A dataclass to store the info the client need to send to
 * the server
 */
public class ClientData implements Serializable {

    private static final long serialVersionUID = 2;
    
    final private Language language;
    final private ServerAction serverAction;
    final private String name;

    public ClientData(final Language language, final String name, final ServerAction serverAction){
        this.language = language;
        this.name = name;
        this.serverAction = serverAction;
    }

    final public Language getLanguage() {
        return language;
    }

    final public String getName() {
        return name;
    }

    final public ServerAction getServerAction() {
        return serverAction;
    }   
}