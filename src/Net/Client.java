package Net;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private String host = "localhost";
    private int port = 8888;
    public Client(String host,int port){
        this.host = host;
        this.port = port;
    }
    public Client(){}
    public void game(){
        try {
            Socket socket = new Socket(host,port);
            System.out.println("连接成功！");
        } catch (IOException e) {
            System.out.println("连接失败！");
        }
    }

    public static void main(String[] args) {
        new Client().game();
    }
}
