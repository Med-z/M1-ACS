public class ClientDataLanguage extends ClientDataAction{
    
    private static final long serialVersionUID = 1L;

    final private Language language;
    
    public ClientDataLanguage(Language language){
        super(ServerAction.CHANGE_LANG);
        this.language = language;
    }

    final public Language getLanguage() {
        return language;
    }
    
}
