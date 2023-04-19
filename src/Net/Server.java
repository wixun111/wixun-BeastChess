package Net;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private int port = 8888;
    private Socket accept;
    private ServerSocket server;
    public Server(int port){
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public Socket getAccept() {
        return accept;
    }
}
