
public class ClientDataZone extends ClientData {

    private static final long serialVersionUID = 1L;

    final private int zone;

    public ClientDataZone(Language language, String name, ServerAction serverAction, int zone) {
        super(language, name , serverAction);
        this.zone = zone;
    }

    final public int getZone() {
        return zone;
    }
}
