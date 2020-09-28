package ACS;

public class ClientDataLanguage extends ClientData{
    
    private static final long serialVersionUID = 1L;

    final private Language language;
    
    public ClientDataLanguage(Language language){
        super();
        this.language = language;
    }

    final public Language getLanguage() {
        return language;
    }
    
}
