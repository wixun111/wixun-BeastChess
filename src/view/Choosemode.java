package view;
import Net.Client;
import Net.Server;
import controller.GameController;
import model.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Choosemode extends JFrame {
    private final int WIDTH;
    private final int HEIGTH;

    private Socket accept;
    public Choosemode(int width, int height){
        this.HEIGTH = height;
        this.WIDTH = width;
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        addNetButton();
        addSinglePlayerButton();
    }
    private void addNetButton() {
        JButton button = new JButton("联网模式");
        button.addActionListener((e) -> {
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            invalidate();
            addClientButton();
            addServerButton();
            repaint();
        });
        button.setLocation(HEIGTH/2, HEIGTH /2-120);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addSinglePlayerButton() {
        JButton button = new JButton("单机模式");
        button.addActionListener((e) -> {
            start(0,null);
            dispose();
        });
        button.setLocation(HEIGTH/2, HEIGTH /2);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    public void start(int mode,Socket socket){
        ChessGameFrame mainFrame = new ChessGameFrame(1100, 750, mode);
        GameController gameController;
        try {
            gameController = new GameController(mainFrame.getChessboardComponent(), new Chessboard(false),false,mode,socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainFrame.setGame(gameController);
        mainFrame.setVisible(true);
    }
    private void addServerButton() {
        JButton button = new JButton("创建主机");
        button.addActionListener((e) -> {
            Server server = new Server(8888);
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            addLabel();
            repaint();
            try {
                ServerSocket serverSocket = new ServerSocket(8888);
                //JOptionPane.showMessageDialog(this, "你的主机地址："+InetAddress.getLocalHost().getHostAddress()+"\n你的端口号： "+server.getPort());
                new Thread(() -> {
                    try {
                        accept = serverSocket.accept();
                        System.out.println("主机已被链接！！！");
                        start(1,accept);
                        dispose();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                }).start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        button.setLocation(HEIGTH/2, HEIGTH /2-120);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addClientButton() {
        JButton button = new JButton("链接主机");
        button.addActionListener((e) -> {
//            String host = JOptionPane.showInputDialog(null,"请输入目标主机的IP：");
//            String port = JOptionPane.showInputDialog(null,"请输入目标主机的端口：");
            String host = null;
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
            String port = "8888";
            if((port!=null&&!port.equals(""))&&(host!=null&&!host.equals(""))){
                Client client = new Client(host,Integer.parseInt(port));
                Socket socket = client.game();
                if(socket!=null){
                    System.out.println("链接成功");
                    start(2,socket);
                    dispose();
                }
                else JOptionPane.showMessageDialog(this,"链接失败");
            }else JOptionPane.showMessageDialog(this,"链接失败");
            repaint();
        });
        button.setLocation(HEIGTH/2, HEIGTH /2);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addLabel() {
        JLabel statusLabel = new JLabel("连接中...");
        statusLabel.setLocation(HEIGTH/2+20, HEIGTH/2-50);
        statusLabel.setSize(300, 80);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 60));
        add(statusLabel);
    }
}