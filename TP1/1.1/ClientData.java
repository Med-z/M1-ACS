import java.io.Serializable;

/**
 * ClientData
 * 
 * A dataclass to store the info the client need to send to
 * the server
 */
public class ClientData implements Serializable {

    private static final long serialVersionUID = 1L;


    final private Language language;

    public ClientData(final Language language) {
        this.language = language;
    }

    final public Language getLanguage() {
        return language;
    }
    
}