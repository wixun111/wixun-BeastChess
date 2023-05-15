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
    private final int HEIGTH;

    private final int ONE_CHESS_SIZE;

    private ChessboardComponent chessboardComponent;
    private GameController game;
    public ChessGameFrame(int width, int height,int mode) {
        setTitle("斗兽棋"); //设置标题
        this.WIDTH = width;
        this.HEIGTH = height;
        this.ONE_CHESS_SIZE = (HEIGTH * 4 / 5) / 9;

        setSize(WIDTH, HEIGTH);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);

        addChessboard();
        addLabel();
        addModeLabel(mode);
        addRestartButton();
        addSaveButton();
        addLoadButton();
        addCapitulateButton();
        addUndoButton();
    }

    public ChessboardComponent getChessboardComponent() {
        return chessboardComponent;
    }

    public void setGame(GameController game) {
        this.game = game;
        addPlayerLabel();
    }

    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        chessboardComponent = new ChessboardComponent(ONE_CHESS_SIZE);
        chessboardComponent.setLocation(WIDTH / 15, HEIGTH / 10);
        add(chessboardComponent);
    }

    /**
     * 在游戏面板中添加标签
     */
    private void addLabel() {
        JLabel statusLabel = new JLabel("斗兽棋");
        statusLabel.setLocation(HEIGTH, HEIGTH / 10-40);
        statusLabel.setSize(200, 60);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 40));
        add(statusLabel);
    }
    private void addModeLabel(int mode) {
        String modeName ="";
        Color color = null;
        if(mode==0){
            modeName = "单机模式";
            color = Color.black;
        }else if(mode==1){
            modeName = "联机模式（主机蓝）";
            color = Color.BLUE;
        }else if(mode==2){
            modeName = "联机模式（客户红）";
            color = Color.RED;
        }
        JLabel statusLabel = new JLabel(modeName);
        if (mode!= 0)statusLabel.setLocation(HEIGTH-100, HEIGTH / 10+40);
        else statusLabel.setLocation(HEIGTH, HEIGTH / 10+40);
        statusLabel.setSize(400, 60);
        Font font = new Font("Rockwell", Font.BOLD, 40);
        statusLabel.setForeground(color);
        statusLabel.setFont(font);
        add(statusLabel);
    }
    private void addPlayerLabel(){
        JLabel statusLabel = new JLabel("走子方");
        statusLabel.setForeground(game.getCurrentPlayer().getColor());
        statusLabel.setLocation(HEIGTH, HEIGTH / 10+520);
        statusLabel.setSize(200, 60);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 40));
        add(statusLabel);
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
        button.setLocation(HEIGTH, HEIGTH / 10 + 120);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addCapitulateButton() {
        JButton button = new JButton("认输");
        button.addActionListener((e) -> {
            game.capitulate();
        });
        button.setLocation(HEIGTH, HEIGTH / 10 + 440);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
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
        button.setLocation(HEIGTH, HEIGTH / 10 + 200);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addLoadButton() {
        JButton button = new JButton("读取");
        button.addActionListener((e) -> {
            game.restart(true);
        });
        button.setLocation(HEIGTH, HEIGTH / 10 + 280);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
    private void addUndoButton() {
        JButton button = new JButton("悔棋");
        button.addActionListener((e) ->{
            if(game.getMode()==3) game.undo();
            game.undo();
        });
        button.setLocation(HEIGTH, HEIGTH / 10 + 360);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
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
