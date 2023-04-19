package Net;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private String host = "localhost";
    private int port = 8888;
    private boolean isLinked = false;
    public Client(String host,int port){
        this.host = host;
        this.port = port;
    }
    public Socket game(){
        try {
            Socket socket = new Socket(host,port);
            return socket;
        } catch (IOException e) {
            System.out.println("连接失败！");
            return null;
        }
    }
}
