
public class ClientDataZone extends ClientData {

    private static final long serialVersionUID = 1L;

    final private int zone;

    public ClientDataZone(Language language,int zone) {
        super(language, ServerAction.TIME);
        this.zone = zone;
    }

    final public int getZone() {
        return zone;
    }
}
