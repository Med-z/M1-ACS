package ACS;

public class ClientDataName extends ClientData{
    
    private static final long serialVersionUID = 1L;
    
    final private String name;

    public ClientDataName(String name) {
        super();
        this.name = name;
    }

    final public String getName() {
        return name;
    }    
}
