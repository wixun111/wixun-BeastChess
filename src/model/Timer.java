package model;

import controller.GameController;

import javax.swing.*;

public class Timer extends Thread{
    private int time = 5;
    private final GameController game;
    private JLabel timeLabel;
    private boolean pause;
    @Override
    public void run(){
        while (true){
            PlayerColor player = game.getCurrentPlayer();
            while(true) {
                try {
                    while (pause){
                        sleep(50);
                    }
                    sleep(1000);
                    if(time==0){
                        if((game.getMode()==1&&game.getCurrentPlayer()==PlayerColor.RED)||(game.getMode()==2&&game.getCurrentPlayer()==PlayerColor.BLUE)) {
                            pause = true;
                            break;
                        }
                        game.getComputer().AiTurn(game.getModel(),game);
                        if(game.getMode()==3) game.getComputer().AiTurn(game.getModel(),game);
                        break;
                    }else time--;
                    if (game.getCurrentPlayer() != player){
                        break;
                    }
                    timeLabel.setText("时间:" + time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (pause){
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if(game.isOver()) break;
            timeLabel.setText("时间:" + time);
        }
    }

    public void setTimeLabel(JLabel timeLabel) {
        this.timeLabel = timeLabel;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public JLabel getTimeLabel() {
        return timeLabel;
    }

    public Timer(GameController controller){
        this.game = controller;
    }
}
