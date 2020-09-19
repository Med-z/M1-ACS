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
    final private int zone;

    public ClientData(final Language language, final int zone) {
        this.language = language;
        this.zone = zone;
    }

    final public Language getLanguage() {
        return language;
    }

    public int getZone() {
        return zone;
    }
   
}