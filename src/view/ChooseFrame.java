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
    private int stage;
    private boolean isPlay;
    private StartFrame startFrame;
    private Socket accept;
    protected Font pixel;
    protected User user;
    private Server server;
    private final JLayeredPane layeredPane;
    private ChessGameFrame mainFrame;
    private GameController gameController;
    private ServerSocket serverSocket;
    private JButton NetButton;
    private JButton MultiPlayerButton;
    private JButton SinglePlayerButton;
    private JButton ServerButton;
    private JButton ClientButton;
    private JButton ObserverButton;
    private JButton LoginButton;
    private JButton RegisterButton;
    private JButton RankButton;
    private JButton SimpleButton;
    private JButton NormalButton;
    private JButton DifficultButton;
    private JButton BackButton;

    public ChooseFrame(int width, int height, StartFrame startFrame){
        this.startFrame = startFrame;
        this.stage = 0;
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
        Image image = new ImageIcon("resource/Picture/chooseMode bg.jpeg").getImage();
        image = image.getScaledInstance(700, 750,Image.SCALE_DEFAULT);
        ImageIcon icon = new ImageIcon(image);
        JLabel background = new JLabel(icon);
        background.setSize(700, 750);
        background.setLocation(0, 0);
        background.setLayout(null);
        addButton();
        layeredPane.add(background,JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(NetButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(MultiPlayerButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(SinglePlayerButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(BackButton, JLayeredPane.PALETTE_LAYER);
    }
    public Font getPixel(int style, int size){
        return pixel.deriveFont(style,size);
    }
    private void addNetButton() {
        JButton button = new JButton("联网模式");
        button.addActionListener((e) -> {
            stage = 1;
            layeredPane.remove(NetButton);
            layeredPane.remove(MultiPlayerButton);
            layeredPane.remove(SinglePlayerButton);
            layeredPane.add(ServerButton, JLayeredPane.PALETTE_LAYER);
            layeredPane.add(ClientButton, JLayeredPane.PALETTE_LAYER);
            layeredPane.add(ObserverButton, JLayeredPane.PALETTE_LAYER);
            invalidate();
            repaint();
        });
        setButton(button,240,150);
        NetButton = button;
    }

    private void addMultiPlayerButton() {
        JButton button = new JButton("双人模式");
        button.addActionListener((e) -> {
            start(0,null,2);
            setVisible(false);
        });
        setButton(button,240,250);
        MultiPlayerButton = button;
    }
    private void addSinglePlayerButton() {
        JButton button = new JButton("单机模式");
        button.addActionListener((e) -> {
            stage = 2;
            layeredPane.remove(NetButton);
            layeredPane.remove(MultiPlayerButton);
            layeredPane.remove(SinglePlayerButton);
            layeredPane.add(LoginButton, JLayeredPane.PALETTE_LAYER);
            layeredPane.add(RegisterButton, JLayeredPane.PALETTE_LAYER);
            layeredPane.add(RankButton, JLayeredPane.PALETTE_LAYER);
            invalidate();
            repaint();
        });
        setButton(button,240,350);
        SinglePlayerButton = button;
    }
    private void addServerButton() {
        JButton button = new JButton("创建主机");
        button.addActionListener((e) -> {
            server = new Server();
            layeredPane.remove(ServerButton);
            layeredPane.remove(ClientButton);
            layeredPane.remove(ObserverButton);
            layeredPane.remove(BackButton);
            addLabel();
            repaint();
            try {
                serverSocket = new ServerSocket(server.getPort());
                addIpLabel();
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
        setButton(button,240,150);
        ServerButton = button;
    }
    private void addClientButton() {
        JButton button = new JButton("链接主机");
        button.addActionListener((e) -> {
            String host = JOptionPane.showInputDialog(null,"请输入目标主机的IP：");
            int port = 8888;
            if(host != null && !host.equals("")){
                Client client = new Client(host,port);
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
        setButton(button,240,250);
        ClientButton = button;
    }
    private void addObserverButton() {
        JButton button = new JButton("观战");
        button.addActionListener((e) -> {
            String host = JOptionPane.showInputDialog(null,"请输入目标主机的IP：");
            String port = "8889";
            if(host != null && !host.equals("")){
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
        setButton(button,240,350);
        ObserverButton = button;
    }
    private void addLoginButton() {
        JButton button = new JButton("登录");
        button.addActionListener((e) -> {
            new LoginFrame(this);
        });
        setButton(button,240,150);
        LoginButton = button;
    }
    private void addRegisterButton() {
        JButton button = new JButton("注册");
        button.addActionListener((e) -> {
            new RegisterFrame(this);
        });
        setButton(button,240,250);
        RegisterButton = button;
    }
    private void addRankButton() {
        JButton button = new JButton("排行榜");
        button.addActionListener((e) -> {
            Collections.sort(users);
            StringBuilder rank = new StringBuilder("排名             名称                分数\n");
            for (int i = 0; i < users.size(); i++){
                rank.append(String.format("   %-18d %-15s" + " ".repeat(Math.max(0, 10 - users.get(i).getName().length())) + "%-10d \n", i + 1, users.get(i).getName(), users.get(i).getScore()));
            }
            JOptionPane.showMessageDialog(null, rank.toString(), "排行榜",JOptionPane.INFORMATION_MESSAGE);
        });
        setButton(button,240,350);
        RankButton = button;
    }
    private void addSimpleButton() {
        JButton button = new JButton("简单");
        button.addActionListener((e) -> {
            start(3,null,Constant.EASY.getNum());
            setVisible(false);
        });
        setButton(button,240,150);
        SimpleButton = button;
    }
    private void addNormalButton() {
        JButton button = new JButton("普通");
        button.addActionListener((e) -> {
            start(3,null,Constant.NORMAL.getNum());
            setVisible(false);
        });
        setButton(button,240,250);
        NormalButton = button;
    }
    private void addDifficultButton() {
        JButton button = new JButton("困难");
        button.addActionListener((e) -> {
            start(3,null,Constant.DIFFICULT.getNum());
            setVisible(false);
        });
        setButton(button,240,350);
        DifficultButton = button;
    }
    private void addBackButton() {
        JButton button = new JButton("返回");
        button.addActionListener((e) -> {
            back();
        });
        setButton(button,240,450);
        BackButton = button;
    }
    private void addIpLabel() throws UnknownHostException {
        JLabel statusLabel = new JLabel("IP:"+InetAddress.getLocalHost().getHostAddress());
        statusLabel.setLocation(WIDTH /2-150, HEIGHT /2-150);
        statusLabel.setSize(300, 80);
        statusLabel.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(statusLabel, JLayeredPane.PALETTE_LAYER);
    }
    private void addLabel() {
        JLabel statusLabel = new JLabel("连接中...");
        statusLabel.setLocation(WIDTH /2-150, HEIGHT /2-50);
        statusLabel.setSize(300, 80);
        statusLabel.setFont(getPixel(Font.BOLD,60));
        layeredPane.add(statusLabel, JLayeredPane.PALETTE_LAYER);
    }
    private void setButton(JButton button,int x,int y) {
        button.setSize(230, 80);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setLocation(x,y);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green big.png"));
        button.setFont(getPixel(Font.BOLD,30));
    }
    private void addButton(){
        addNetButton();
        addSinglePlayerButton();
        addMultiPlayerButton();
        addBackButton();
        addClientButton();
        addServerButton();
        addObserverButton();
        addLoginButton();
        addRegisterButton();
        addRankButton();
        addSimpleButton();
        addNormalButton();
        addDifficultButton();
    }
    private void setUsers(){
        try {
            File file = new File("users.txt");
            System.out.println(file.exists());
            if(!file.exists()){
                file.createNewFile();
            }
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            users = (ArrayList<User>) ois.readObject();
        } catch (Exception ignored){}
    }
    private void back(){
        if(stage==0){
            setVisible(false);
            startFrame.setBack(true);
            startFrame.setVisible(true);
        }else if (stage==1){
            layeredPane.remove(ServerButton);
            layeredPane.remove(ClientButton);
            layeredPane.remove(ObserverButton);
            layeredPane.add(NetButton, JLayeredPane.PALETTE_LAYER);
            layeredPane.add(MultiPlayerButton, JLayeredPane.PALETTE_LAYER);
            layeredPane.add(SinglePlayerButton, JLayeredPane.PALETTE_LAYER);
            stage = 0;
        } else if (stage==2){
            layeredPane.remove(LoginButton);
            layeredPane.remove(RegisterButton);
            layeredPane.remove(RankButton);
            layeredPane.add(NetButton, JLayeredPane.PALETTE_LAYER);
            layeredPane.add(MultiPlayerButton, JLayeredPane.PALETTE_LAYER);
            layeredPane.add(SinglePlayerButton, JLayeredPane.PALETTE_LAYER);
            stage = 0;
        }else  {
            layeredPane.remove(SimpleButton);
            layeredPane.remove(NormalButton);
            layeredPane.remove(DifficultButton);
            layeredPane.add(LoginButton, JLayeredPane.PALETTE_LAYER);
            layeredPane.add(RegisterButton, JLayeredPane.PALETTE_LAYER);
            layeredPane.add(RankButton, JLayeredPane.PALETTE_LAYER);
            stage = 2;
        }
        revalidate();
        repaint();
    }
    public void login(){
        stage = 3;
        layeredPane.remove(LoginButton);
        layeredPane.remove(RegisterButton);
        layeredPane.remove(RankButton);
        layeredPane.add(SimpleButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(NormalButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(DifficultButton, JLayeredPane.PALETTE_LAYER);
        invalidate();
        repaint();
    }
    public void start(int mode,Socket socket,int difficulty){
        if(isPlay){
            mainFrame.setVisible(true);
            gameController.restart(false);
            gameController.setMode(mode);
            mainFrame.setModeLabel(mode);
            gameController.getComputer().setDifficulty(difficulty);
            gameController.setDifficulty(difficulty);
            return;
        }
        isPlay = true;
        mainFrame = new ChessGameFrame(1100, 750, mode);
        try {
            gameController = new GameController(mainFrame.getChessboardComponent(), new Chessboard(false),mode,socket,difficulty);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gameController.setServerSocket(serverSocket);
        gameController.setUser(user);
        gameController.setUsers(users);
        mainFrame.setGame(gameController);
        mainFrame.setChooseFrame(this);
        gameController.setPlayerLabel(mainFrame.getPlayerLabel());
        gameController.setTurnLabel(mainFrame.getTurnLabel());
        gameController.getTimer().setTimeLabel(mainFrame.getTimeLabel());
        mainFrame.setVisible(true);
    }
}