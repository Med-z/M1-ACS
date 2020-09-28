package ACS;

public class ClientDataInit extends ClientData{

    private static final long serialVersionUID = 1L;

    final private Language language;

    public ClientDataInit(final Language language){
        this.language = language;
    }

    final public Language getLanguage() {
        return language;
    }
    
}
