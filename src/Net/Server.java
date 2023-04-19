package Net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port = 8888;
    public Server(int port){
        this.port = port;
    }

    public Server() {
    }

    public void service(){
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("等待客户端链接");
            Socket socket = server.accept();
            System.out.println("接受客户端成功！");
        } catch (IOException e) {
            System.out.println("接受客户端失败！");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server().service();
    }
}
