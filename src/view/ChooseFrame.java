package view;
import Net.Client;
import Net.Server;
import controller.GameController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

public class ChooseFrame extends JFrame {
    protected ArrayList<User> users = new ArrayList<>();
    private final int WIDTH;
    private final int HEIGHT;
    private boolean isPlay;
    protected boolean isLogin;

    private Socket accept;
    protected Font pixel;
    private JLabel background;
    protected User user;
    private JLayeredPane layeredPane;
    private ChessGameFrame mainFrame;
    private GameController gameController;
    private ServerSocket serverSocket;


    public ChooseFrame(int width, int height){
        this.layeredPane = new JLayeredPane();
        this.setContentPane(layeredPane);
        this.HEIGHT = height;
        this.WIDTH = width;
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        setUsers();
        try {
            pixel = Font.createFont(Font.TRUETYPE_FONT,new File("resource\\Character\\pixel4.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        addNetButton();
        addSinglePlayerButton();
        addMultiPlayerButton();
        addTitleLabel();

        Image image = new ImageIcon("resource/Picture/ChooseMode bg.jpeg").getImage();
        image = image.getScaledInstance(700, 600,Image.SCALE_DEFAULT);
        ImageIcon icon = new ImageIcon(image);
        background = new JLabel(icon);
        background.setSize(700, 600);
        background.setLocation(0, 0);
        background.setLayout(null);
        layeredPane.add(background,JLayeredPane.DEFAULT_LAYER);
    }
    public Font getPixel(int style, int size){
        return pixel.deriveFont(style,size);
    }
    private void addRegisterButton() {
        JButton button = new JButton("注册");
        button.addActionListener((e) -> {
            new RegisterFrame(this);
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addLoginButton() {
        JButton button = new JButton("登录");
        button.addActionListener((e) -> {
            if(isLogin){
                remove(getContentPane().getComponent(0));
                remove(getContentPane().getComponent(0));
                remove(getContentPane().getComponent(0));
                remove(getContentPane().getComponent(0));
                invalidate();
                addSimpleButton();
                addNormalButton();
                addDifficultButton();
                repaint();
            }else {
                new LoginFrame(this);
            }
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2-120);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addRankButton() {
        JButton button = new JButton("排行榜");
        button.addActionListener((e) -> {
            Collections.sort(users);
            String rank = "排名             名称                分数\n";
            for (int i = 0; i < users.size(); i++){
//                rank += String.format("%-10s %-10s %-10s\n", "Alice", "32", "85");
                String temp = "";
                for (int j = 0; j < 10-users.get(i).getName().length(); j++) {
                    temp += " ";
                }
                rank += String.format("   %-18d %-15s" + temp + "%-10d \n",i+1,users.get(i).getName(),users.get(i).getScore());
            }
            JOptionPane.showMessageDialog(null, rank, "排行榜",JOptionPane.INFORMATION_MESSAGE);
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2+120);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
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
            addObserverButton();
            repaint();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2-120);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addMultiPlayerButton() {
        JButton button = new JButton("双人模式");
        button.addActionListener((e) -> {
            start(0,null,2);
            setVisible(false);
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addSinglePlayerButton() {
        JButton button = new JButton("单机模式");
        button.addActionListener((e) -> {
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            invalidate();
            addLoginButton();
            addRegisterButton();
            addRankButton();
            repaint();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2+120);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addServerButton() {
        JButton button = new JButton("创建主机");
        button.addActionListener((e) -> {
            Server server = new Server(8888);
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            remove(getContentPane().getComponent(0));
            addLabel();
            repaint();
            try {
                serverSocket = new ServerSocket(8888);
                //JOptionPane.showMessageDialog(this, "你的主机地址："+InetAddress.getLocalHost().getHostAddress()+"\n你的端口号： "+server.getPort());
                new Thread(() -> {
                    try {
                        accept = serverSocket.accept();
                        System.out.println("主机已被链接！！！");
                        start(1,accept,2);
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
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
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
                    start(2,socket,2);
                    dispose();
                }
                else JOptionPane.showMessageDialog(this,"链接失败");
            }else JOptionPane.showMessageDialog(this,"链接失败");
            repaint();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addObserverButton() {
        JButton button = new JButton("观战");
        button.addActionListener((e) -> {
//            String host = JOptionPane.showInputDialog(null,"请输入目标主机的IP：");
//            String port = JOptionPane.showInputDialog(null,"请输入目标主机的端口：");
            String host = null;
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
            String port = "8889";
            if((port!=null&&!port.equals(""))&&(host!=null&&!host.equals(""))){
                Client client = new Client(host,Integer.parseInt(port));
                Socket socket = client.game();
                if(socket!=null){
                    System.out.println("链接成功");
                    start(4,socket,2);
                    dispose();
                }
                else JOptionPane.showMessageDialog(this,"棋局未开始！");
            }else JOptionPane.showMessageDialog(this,"棋局未开始！");
            repaint();
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2+120);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addSimpleButton() {
        JButton button = new JButton("简单");
        button.addActionListener((e) -> {
            start(3,null,Constant.EASY.getNum());
            setVisible(false);
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2-120);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addNormalButton() {
        JButton button = new JButton("普通");
        button.addActionListener((e) -> {
            start(3,null,Constant.NORMAL.getNum());
            setVisible(false);
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addDifficultButton() {
        JButton button = new JButton("困难");
        button.addActionListener((e) -> {
            start(3,null,Constant.DIFFICULT.getNum());
            setVisible(false);
        });
        button.setLocation(WIDTH /2-150, HEIGHT /2+120);
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addLabel() {
        JLabel statusLabel = new JLabel("连接中...");
        statusLabel.setLocation(WIDTH /2-150, HEIGHT /2-50);
        statusLabel.setSize(300, 80);
        statusLabel.setFont(getPixel(Font.BOLD,60));
        layeredPane.add(statusLabel, JLayeredPane.PALETTE_LAYER);
    }
    private void addTitleLabel() {
        JLabel statusLabel = new JLabel("斗兽棋");
        statusLabel.setLocation(WIDTH /2-100, HEIGHT /2-250);
        statusLabel.setSize(300, 80);
        statusLabel.setFont(getPixel(Font.BOLD,60));
        layeredPane.add(statusLabel, JLayeredPane.PALETTE_LAYER);
    }
    private void setUsers(){
        try {
            File file = new File("users.txt");
            System.out.println(file.exists());
            if(!file.exists()){
                file.createNewFile();
            }
            String temp;
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            users = (ArrayList<User>) ois.readObject();
        } catch (Exception e){}
    }
    public void start(int mode,Socket socket,int difficult){
        if(isPlay){
            mainFrame.setVisible(true);
            gameController.restart(false);
            return;
        }
        isPlay = true;
        mainFrame = new ChessGameFrame(1100, 750, mode);
        try {
            gameController = new GameController(mainFrame.getChessboardComponent(), new Chessboard(false),mode,socket,difficult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gameController.setServerSocket(serverSocket);
        gameController.setUser(user);
        gameController.setUsers(users);
        mainFrame.setGame(gameController);
        mainFrame.setChooseFrame(this);
        gameController.setPlayerLabel(mainFrame.getPlayerLabel());
        gameController.getTimer().setTimeLabel(mainFrame.getTimeLabel());
        mainFrame.setVisible(true);
    }
}