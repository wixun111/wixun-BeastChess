package ai;

import controller.GameController;
import model.ChessPiece;
import model.Chessboard;
import model.ChessboardPoint;
import model.PlayerColor;

import java.util.ArrayList;


public class AI {
    private static final int INF = 999999;
    private Chessboard model;
    private GameController game;
    private int diffculty;
    public AI(Chessboard model,GameController gameController,int diffculty){
        this.game = gameController;
        this.model = model;
        this.diffculty = diffculty;
    }
    public void AiTurn(){
        int max = -INF;
        int row = 0,col = 0,x = 0,y = 0;
        int[] dirx ={1,0,0,-1};int[] diry = {0,1,-1,0};
        long current1=System.currentTimeMillis();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint src = new ChessboardPoint(i, j);
                ChessPiece chess = model.getChessPieceAt(src);
                if (chess == null || chess.getOwner() == PlayerColor.BLUE) continue;
                for (int k = 0; k < 4; k++) {
                    if (i + dirx[k] < 0 || i + dirx[k] > 8 || j + diry[k] < 0 || j + diry[k] > 6) continue;
                    ChessboardPoint des = new ChessboardPoint(i + dirx[k], j + diry[k]);
                    ChessPiece target = model.getChessPieceAt(des);
                    if ((chess.getName().equals("Lion") || chess.getName().equals("Tiger")) && game.getView().riverCell.contains(des)) {
                        des = model.Jump(game.getView(), src, des);
                        if (des == null) continue;
                        target = model.getChessPieceAt(des);
                        if (target != null && !model.isValidCapture(game.getView(), src, des)) continue;
                        game.AIConcludeMove(src, des, target);
                    } else if (!model.isValidMove(game.getView(), src, des) && !model.isValidCapture(game.getView(), src, des)) continue;
                    else game.AIConcludeMove(src, des, target);
                    int temp = alphabeta(1, -INF, INF, 1);
                    game.AIUndo();
                    if(temp>max){
                        max = temp;
                        row = i;
                        col = j;
                        x = des.getRow();
                        y = des.getCol();
                    }
                }
            }
        }
        System.out.println("Score:" + max);
        ChessboardPoint src = new ChessboardPoint(row, col);
        ChessboardPoint des = new ChessboardPoint(x, y);
        System.out.println("src:"+src+"   des:"+des);
        game.concludeMove(src, des, model.getChessPieceAt(des), false);
        long current2=System.currentTimeMillis();
        System.out.printf("The running time is %.3f second",(current2-current1)/1000.0d);
    }
    private int alphabeta(int player,int alpha,int beta, int depth){
        if(depth == diffculty||game.isOver()){
            int score = getScore();
            //System.out.println(score);
            return score;
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint src = new ChessboardPoint(i,j);
                ChessPiece chess = model.getChessPieceAt(src);
                if(chess==null||chess.getOwner()==(player==1?PlayerColor.RED:PlayerColor.BLUE)) continue;
                int[] dirx ={1,0,0,-1};int[] diry = {0,1,-1,0};
                for (int k = 0; k < 4; k++) {
                    if(i+dirx[k]<0||i+dirx[k]>8||j+diry[k]<0||j+diry[k]>6) continue;
                    ChessboardPoint des = new ChessboardPoint(i+dirx[k],j+diry[k]);
                    ChessPiece target = model.getChessPieceAt(des);
                    if ((chess.getName().equals("Lion") || chess.getName().equals("Tiger")) && game.getView().riverCell.contains(des)) {
                        des = model.Jump(game.getView(), src, des);
                        if (des == null) continue;
                        target = model.getChessPieceAt(des);
                        if (target != null && !model.isValidCapture(game.getView(), src, des)) continue;
                        game.AIConcludeMove(src, des, target);;
                    }
                    else if(!model.isValidMove(game.getView(),src,des)&&!model.isValidCapture(game.getView(),src,des)) continue;
                    else game.AIConcludeMove(src, des, target);
                    int temp = alphabeta(-1*player,alpha,beta,depth+1);
                    game.AIUndo();
                    if (player==-1) alpha = Math.max(alpha,temp);
                    else beta = Math.min(beta,temp);
                    if(beta <= alpha){
                        if (player==-1)
                            return alpha;
                        return beta;
                    }
                }
            }
        }
        if(player==-1)
            return alpha;
        return beta;
    }
    private int getScore(){
        int Score = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint point = new ChessboardPoint(i,j);
                ChessPiece chessPiece = model.getChessPieceAt(point);
                if (chessPiece==null) continue;
                String name = chessPiece.getName();
                int value = chessPiece.getValue();
                Boolean own = chessPiece.getOwner().equals(PlayerColor.RED);
                Score += own?value:-value;
                Score += own?value/200*i:-value/200*(8-i);
                if(name.equals("Tiger")||name.equals("Lion")||name.equals("Rat")){
                    if(i<6&i>1) Score+=own?150:-150;
                }
                if(game.getView().getRiverCell().contains(point)&&game.getView().getGridComponentAt(point).getPlayerColor()!=PlayerColor.RED) {
                    Score += own?1000:-1000;
                }
                else if(game.getView().getDenCell().contains(point)) {
                    Score += own?10000:-10000;
                }
            }
        }
        return Score;
    }
}
