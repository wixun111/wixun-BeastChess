package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */

public class ChessGameFrame extends JFrame {
    private final int WIDTH;
    private final int HEIGHT;

    private final int ONE_CHESS_SIZE;
    public JLabel background;

    private ChessboardComponent chessboardComponent;
    private GameController game;
    private Font pixel;
    private JLayeredPane layeredPane;
    private JLabel timeLabel;
    private JLabel playerLabel;
    private JLabel bgDesert;
    private JLabel bgGrassland;
    private JLabel bgIceField;
    private ChooseFrame chooseFrame;
    int theme = 1;

    public ChessGameFrame(int width, int height,int mode) {
        setTitle("斗兽棋"); //设置标题
        this.layeredPane = new JLayeredPane();
        this.setContentPane(layeredPane);
        this.WIDTH = width;
        this.HEIGHT = height;
        this.ONE_CHESS_SIZE = HEIGHT / 9-4;
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);
        try {
            pixel = Font.createFont(Font.TRUETYPE_FONT,new File("resource\\Character\\pixel4.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        addChessboard();
        addLabel();
        addModeLabel(mode);
        if(mode!=4){
            addRestartButton();
            addSaveButton();
            addLoadButton();
            addCapitulateButton();
            addUndoButton();
            addReplayButton();
            addChangeThemeButton();
            if(mode==0||mode==3) addBackButton();
        }
        Image image = new ImageIcon("resource/Picture/bg grassland.jpg").getImage();
        image = image.getScaledInstance(1100, 750,Image.SCALE_DEFAULT);
        ImageIcon icon = new ImageIcon(image);
        bgGrassland = new JLabel(icon);
        bgGrassland.setSize(1100, 750);
        bgGrassland.setLocation(0, 0);
        image = new ImageIcon("resource/Picture/bg desert.jpg").getImage();
        image = image.getScaledInstance(1100, 750,Image.SCALE_DEFAULT);
        icon = new ImageIcon(image);
        bgDesert = new JLabel(icon);
        bgDesert.setSize(1100, 750);
        bgDesert.setLocation(0, 0);
        image = new ImageIcon("resource/Picture/bg ice field.jpg").getImage();
        image = image.getScaledInstance(1100, 750,Image.SCALE_DEFAULT);
        icon = new ImageIcon(image);
        bgIceField = new JLabel(icon);
        bgIceField.setSize(1100, 750);
        bgIceField.setLocation(0, 0);
        background = bgGrassland;
        add(background);
    }

    public ChessboardComponent getChessboardComponent() {
        return chessboardComponent;
    }

    public void setGame(GameController game) {
        this.game = game;
        addTimeLabel();
        addPlayerLabel();
    }

    public void setChooseFrame(ChooseFrame chooseFrame) {
        this.chooseFrame = chooseFrame;
    }

    public Font getPixel(int style, int size){
        return pixel.deriveFont(style,size);
    }

    public JLabel getPlayerLabel() {
        return playerLabel;
    }

    public JLabel getTimeLabel() {
        return timeLabel;
    }

    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        chessboardComponent = new ChessboardComponent(ONE_CHESS_SIZE);
        chessboardComponent.setLocation(250, 0);
        add(chessboardComponent);
    }
    /**
     * 在游戏面板中添加标签
     */
    private void addLabel() {
        JLabel statusLabel = new JLabel("斗兽棋");
        statusLabel.setLocation(25, HEIGHT / 10-30);
        statusLabel.setSize(200, 120);
        statusLabel.setFont(getPixel(Font.BOLD,60));
        layeredPane.add(statusLabel, JLayeredPane.PALETTE_LAYER);
    }
    private void addModeLabel(int mode) {
        String modeName ="";
        Color color = null;
        if(mode==0){
            modeName = "单机模式";
        }else if(mode==1){
            modeName = "联网模式";
            color = Color.BLUE;
        }else if(mode==2){
            modeName = "联网模式";
            color = Color.RED;
        }else if(mode==3){
            modeName = "观战模式";
        }
        JLabel statusLabel = new JLabel(modeName);
        if (mode!= 0)statusLabel.setLocation(25, HEIGHT / 10+40);
        else statusLabel.setLocation(25, HEIGHT / 10+40);
        statusLabel.setSize(400, 140);
        Font font = getPixel(Font.BOLD,50);
        if(color!=null) statusLabel.setForeground(color);
        statusLabel.setFont(font);
        layeredPane.add(statusLabel, JLayeredPane.PALETTE_LAYER);
    }
    private void addPlayerLabel(){
        playerLabel = new JLabel("走子方");
        playerLabel.setForeground(game.getCurrentPlayer().getColor());
        playerLabel.setLocation(50, HEIGHT / 10+280);
        playerLabel.setSize(200, 60);
        playerLabel.setFont(getPixel(Font.BOLD,50));
        layeredPane.add(playerLabel, JLayeredPane.PALETTE_LAYER);
    }
    private void addTimeLabel(){
        timeLabel = new JLabel("时间:45");
        timeLabel.setLocation(25, HEIGHT / 10+200);
        timeLabel.setSize(200, 60);
        timeLabel.setFont(getPixel(Font.BOLD,50));
        layeredPane.add(timeLabel, JLayeredPane.PALETTE_LAYER);
    }
    private void addRestartButton() {
        JButton button = new JButton("重新开始");
        button.addActionListener((e) -> {
            if(game.getMode()==1||game.getMode()==2)
                if(!game.isOver()) return;
                else{
                    game.setAskType(1);
                    game.assume();
                    return;
                }
            game.restart(false);
        });
        button.setLocation(HEIGHT+100, HEIGHT / 10-40);
        button.setSize(200, 60);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addSaveButton() {
        JButton button = new JButton("保存");
        button.addActionListener((e) -> {
            try {
                game.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        button.setLocation(HEIGHT+100, HEIGHT / 10 + 40);
        button.setSize(200, 60);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addLoadButton() {
        JButton button = new JButton("读取");
        button.addActionListener((e) -> {
            game.restart(true);
        });
        button.setLocation(HEIGHT+100, HEIGHT / 10 + 120);
        button.setSize(200, 60);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addUndoButton() {
        JButton button = new JButton("悔棋");
        button.addActionListener((e) ->{
            if(game.getMode()==3) game.undo();
            game.undo();
        });
        button.setLocation(HEIGHT+100, HEIGHT / 10 + 200);
        button.setSize(200, 60);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addCapitulateButton() {
        JButton button = new JButton("认输");
        button.addActionListener((e) -> {
            game.capitulate();
        });
        button.setLocation(HEIGHT+100, HEIGHT / 10 + 280);
        button.setSize(200, 60);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addReplayButton() {
        JButton button = new JButton("重演");
        button.addActionListener((e) ->{
            game.replay();
        });
        button.setLocation(HEIGHT+100, HEIGHT / 10 + 360);
        button.setSize(200, 60);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addChangeThemeButton() {
        JButton button = new JButton("草地");
        button.setLocation(HEIGHT+100, HEIGHT / 10 + 440);
        button.setSize(200, 60);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green.png"));
        button.setFont(getPixel(Font.BOLD,30));
        add(button);
        button.addActionListener(e -> {
            theme++;
            if(theme==4) theme=1;
            String text =switch (theme) {
                case 1 -> "草地";
                case 2 -> "沙漠";
                case 3 -> "雪原";
                default -> "";
            };
            changeTheme();
            button.setText(text);
        });
    }
    private void addBackButton() {
        JButton button = new JButton("返回");
        button.setLocation(HEIGHT+100, HEIGHT / 10 + 520);
        button.setSize(200, 60);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button green.png"));
        button.setFont(getPixel(Font.BOLD,30));
        add(button);
        button.addActionListener(e -> {
            dispose();
            chooseFrame.setVisible(true);
        });
    }
    private void changeTheme(){
        String picture = "";
        remove(background);
        if(theme==1){
            picture = "resource\\Picture\\button green.png";
            background = bgGrassland;
        }else if(theme==2){
            picture = "resource\\Picture\\button yellow.png";
            background = bgDesert;
        }else if(theme==3) {
            picture = "resource\\Picture\\button blue.png";
            background = bgIceField;
        }
        add(background);
        int n = layeredPane.getComponentCount();
        for (int i = 0; i < n; i++) {
            if(layeredPane.getComponent(i).getClass() == JButton.class){
                JButton temp = (JButton) layeredPane.getComponent(i);
                temp.setIcon(new ImageIcon(picture));
            }
        }
        game.getView().changeTheme(theme);
        revalidate();
        repaint();
    }
//    private void addLoadButton() {
//        JButton button = new JButton("Load");
//        button.setLocation(HEIGTH, HEIGTH / 10 + 240);
//        button.setSize(200, 60);
//        button.setFont(new Font("Rockwell", Font.BOLD, 20));
//        add(button);
//
//        button.addActionListener(e -> {
//            System.out.println("Click load");
//            String path = JOptionPane.showInputDialog(this,"Input Path here");
//            gameController.loadGameFromFile(path);
//        });
//    }
}
