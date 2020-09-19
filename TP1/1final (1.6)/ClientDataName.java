public class ClientDataName extends ClientDataAction{
    
    private static final long serialVersionUID = 1L;
    
    final private String name;

    public ClientDataName(String name) {
        super(ServerAction.HELLO);
        this.name = name;
    }

    final public String getName() {
        return name;
    }    
}
