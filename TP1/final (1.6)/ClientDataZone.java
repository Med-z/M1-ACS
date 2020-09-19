
public class ClientDataZone extends ClientDataAction {

    private static final long serialVersionUID = 1L;

    final private int zone;

    public ClientDataZone(int zone) {
        super(ServerAction.TIME);
        this.zone = zone;
    }

    final public int getZone() {
        return zone;
    }
}
