package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class StartFrame extends JFrame {
    private final int WIDTH;
    private final int HEIGHT;
    private boolean isBack;
    protected Font pixel;
    private final JLayeredPane layeredPane;
    public StartFrame(int width, int height){
        this.layeredPane = new JLayeredPane();
        this.setContentPane(layeredPane);
        this.HEIGHT = height;
        this.WIDTH = width;
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        try {
            pixel = Font.createFont(Font.TRUETYPE_FONT,new File("resource\\Character\\pixel4.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        Image image = new ImageIcon("resource/Picture/bg chooseframe.gif").getImage();
        image = image.getScaledInstance(1000, 750,Image.SCALE_DEFAULT);
        ImageIcon icon = new ImageIcon(image);
        JLabel background = new JLabel(icon);
        background.setSize(1000, 750);
        background.setLocation(0, 0);
        background.setLayout(null);
        layeredPane.add(background,JLayeredPane.DEFAULT_LAYER);
        addStartButton();
        addQuitButton();
        addTitleLabel();
    }

    public void setBack(boolean back) {
        isBack = back;
    }

    public Font getPixel(int style, int size){
        return pixel.deriveFont(style,size);
    }
    private void addStartButton(){
        JButton button = new JButton("开始游戏");
        button.addActionListener((e) -> {
            new ChooseFrame(700, 750, this);
            setVisible(false);
        });
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setLocation(350,450);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button darkgreen big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addQuitButton(){
        JButton button = new JButton("退出游戏");
        button.addActionListener((e) -> {
            System.exit(0);
        });
        button.setSize(300, 100);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setLocation(350,570);
        button.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
        button.setIcon(new ImageIcon("resource\\Picture\\button darkgreen big.png"));
        button.setFont(getPixel(Font.BOLD,30));
        layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
    }
    private void addTitleLabel() {
        JLabel statusLabel = new JLabel("斗兽棋");
        statusLabel.setLocation(330, 200);
        statusLabel.setSize(400, 120);
        statusLabel.setFont(getPixel(Font.BOLD,100));
        layeredPane.add(statusLabel, JLayeredPane.PALETTE_LAYER);
    }
}
