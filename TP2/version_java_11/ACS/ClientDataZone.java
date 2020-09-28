package ACS;

public class ClientDataZone extends ClientData {

    private static final long serialVersionUID = 1L;

    final private int zone;

    public ClientDataZone(int zone) {
        super();
        this.zone = zone;
    }

    final public int getZone() {
        return zone;
    }
}
