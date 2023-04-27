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
    private final int HEIGHT;

    private Socket accept;
    public Choosemode(int width, int height){
        this.HEIGHT = height;
        this.WIDTH = width;
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        addNetButton();
        addSinglePlayerButton();
        addMultiPlayerButton();
        addTitleLabel();
    }
    private void addNetButton() {
        JButton button = new JButton("联网模式");
        button.addActionListener((e) -> {
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            invalidate();
            addClientButton();
            addServerButton();
            repaint();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2-120);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addMultiPlayerButton() {
        JButton button = new JButton("双人模式");
        button.addActionListener((e) -> {
            start(0,null,0);
            dispose();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addSinglePlayerButton() {
        JButton button = new JButton("单机模式");
        button.addActionListener((e) -> {
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            invalidate();
            addSimpleButton();
            addNormalButton();
            addDiffcultButton();
            repaint();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2+120);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    public void start(int mode,Socket socket,int diffcult){
        ChessGameFrame mainFrame = new ChessGameFrame(1100, 750, mode);
        GameController gameController;
        try {
            gameController = new GameController(mainFrame.getChessboardComponent(), new Chessboard(false),mode,socket,diffcult);
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
                        start(1,accept,0);
                        dispose();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                }).start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2-120);
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
                    start(2,socket,0);
                    dispose();
                }
                else JOptionPane.showMessageDialog(this,"链接失败");
            }else JOptionPane.showMessageDialog(this,"链接失败");
            repaint();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addSimpleButton() {
        JButton button = new JButton("简单");
        button.addActionListener((e) -> {
            start(3,null,4);
            dispose();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2-120);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addNormalButton() {
        JButton button = new JButton("普通");
        button.addActionListener((e) -> {
            start(3,null,6);
            dispose();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addDiffcultButton() {
        JButton button = new JButton("困难");
        button.addActionListener((e) -> {
            start(3,null,8);
            dispose();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2+120);
        button.setSize(300, 100);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addLabel() {
        JLabel statusLabel = new JLabel("连接中...");
        statusLabel.setLocation(WIDTH /2-150, HEIGHT /2-50);
        statusLabel.setSize(300, 80);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 60));
        add(statusLabel);
    }
    private void addTitleLabel() {
        JLabel statusLabel = new JLabel("斗兽棋");
        statusLabel.setLocation(WIDTH /2-100, HEIGHT /2-250);
        statusLabel.setSize(300, 80);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 60));
        add(statusLabel);
    }
}