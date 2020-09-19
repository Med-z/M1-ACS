import java.io.Serializable;

/**
 * ClientData
 * 
 * A dataclass to store the info the client need to send to
 * the server
 */
public class ClientData implements Serializable {

    private static final long serialVersionUID = 2L;


    final private Language language;
    final private String name;

    public ClientData(final Language language,String name) {
        this.language = language;
        this.name = name;
    }

    final public Language getLanguage() {
        return language;
    }

    final public String getName() {
        return name;
    }
    
}