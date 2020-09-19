public class ClientDataAction extends ClientData{

    private static final long serialVersionUID = 1L;    
    
    final private ServerAction serverAction;

    public ClientDataAction(final ServerAction serverAction){
        this.serverAction = serverAction;
    }

    final public ServerAction getServerAction() {
        return serverAction;
    }
}
