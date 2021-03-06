public class ClientDataName extends ClientData{
    
    private static final long serialVersionUID = 1L;
    
    final private String name;

    public ClientDataName(Language language, String name) {
        super(language, ServerAction.HELLO);
        this.name = name;
    }

    final public String getName() {
        return name;
    }    
}
