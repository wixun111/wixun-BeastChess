package Net;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private final String host;
    private final int port;
    public Client(String host,int port){
        this.host = host;
        this.port = port;
    }
    public Socket game(){
        try {
            return new Socket(host,port);
        } catch (IOException e) {
            System.out.println("连接失败！");
            return null;
        }
    }
}
