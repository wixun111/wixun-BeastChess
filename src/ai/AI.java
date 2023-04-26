package ai;

import controller.GameController;
import model.ChessPiece;
import model.Chessboard;
import model.ChessboardPoint;
import model.PlayerColor;
import view.ChessboardComponent;

import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class AI {

    private static final int INF = 999999;
    private Chessboard model;
    private GameController game;
    private ChessboardComponent view;
    private int difficulty;
    private boolean over;

    AtomicInteger maxAtomic = new AtomicInteger();
    AtomicInteger rowAtomic = new AtomicInteger();
    AtomicInteger colAtomic = new AtomicInteger();
    AtomicInteger xAtomic = new AtomicInteger();
    AtomicInteger yAtomic = new AtomicInteger();

    public AI(Chessboard model,GameController gameController,int difficulty){
        this.game = gameController;
        this.model = model;
        this.difficulty = difficulty;
        this.view = game.getView();
    }
    public void AiTurn(){
        maxAtomic.set(-INF);
        int row = 0,col = 0,x = 0,y = 0;
        int[] dirx ={1,0,0,-1};int[] diry = {0,1,-1,0};
        long current1=System.currentTimeMillis();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint src = new ChessboardPoint(i, j);
                ChessPiece chess = model.getChessPieceAt(src);
                if (chess == null || chess.getOwner() == PlayerColor.BLUE) continue;
//                System.out.printf("(%d %d)\n",i+1,j+1);
                ExecutorService executor = Executors.newFixedThreadPool(4);
                for (int k = 0; k < 4; k++) {
                    int finalI = i;
                    int finalJ = j;
                    int finalK = k;
                    AIBoard aiBoard = transform(model,game);
                    ChessPiece finalChess = aiBoard.getChessPieceAt(src);
                    executor.submit(() -> {
                        if (finalI + dirx[finalK] < 0 || finalI + dirx[finalK] > 8 || finalJ + diry[finalK] < 0 || finalJ + diry[finalK] > 6) return;
                        ChessboardPoint des = new ChessboardPoint(finalI + dirx[finalK], finalJ + diry[finalK]);
                        ChessPiece target = aiBoard.getChessPieceAt(des);
                        if ((finalChess.getName().equals("Lion") || finalChess.getName().equals("Tiger")) && view.riverCell.contains(des)) {
                            des = aiBoard.Jump(view, src, des);
                            if (des == null) return;
                            target = aiBoard.getChessPieceAt(des);
                            if (target != null && !aiBoard.isValidCapture(view, src, des)) return;
                            AIConcludeMove(src, des, target,aiBoard);
                        } else if (!aiBoard.isValidMove(view, src, des) && !aiBoard.isValidCapture(view, src, des)) return;
                        else AIConcludeMove(src, des, target,aiBoard);
                        int temp = alphabeta(1, -INF, INF, 1,aiBoard);
                        AIUndo(aiBoard);
                        if(temp>maxAtomic.get()){
                            maxAtomic.set(temp);
                            rowAtomic.set(finalI);
                            colAtomic.set(finalJ);
                            xAtomic.set(des.getRow());
                            yAtomic.set(des.getCol());
                        }
                    });
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("Score:" + maxAtomic.get());
        ChessboardPoint src = new ChessboardPoint(rowAtomic.get(), colAtomic.get());
        ChessboardPoint des = new ChessboardPoint(xAtomic.get(), yAtomic.get());
        System.out.println("src:"+src+"   des:"+des);
        System.out.println(model.getChessPieceAt(src));
        game.concludeMove(src, des, model.getChessPieceAt(des), false);
        long current2=System.currentTimeMillis();
        System.out.printf("The running time is %.3f second",(current2-current1)/1000.0d);
    }
    private int alphabeta(int player,int alpha,int beta, int depth,AIBoard aiBoard){
        if(depth == 8||game.isOver()){
            int score = getScore(aiBoard);
            return score;
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint src = new ChessboardPoint(i,j);
                ChessPiece chess = aiBoard.getChessPieceAt(src);
                if(chess==null||chess.getOwner()==(player==1?PlayerColor.RED:PlayerColor.BLUE)) continue;
                int[] dirx ={1,0,0,-1};int[] diry = {0,1,-1,0};
                for (int k = 0; k < 4; k++) {
                    if(i+dirx[k]<0||i+dirx[k]>8||j+diry[k]<0||j+diry[k]>6) continue;
                    ChessboardPoint des = new ChessboardPoint(i+dirx[k],j+diry[k]);
                    ChessPiece target = aiBoard.getChessPieceAt(des);
                    if ((chess.getName().equals("Lion") || chess.getName().equals("Tiger")) && view.riverCell.contains(des)) {
                        des = aiBoard.Jump(view, src, des);
                        if (des == null) continue;
                        target = aiBoard.getChessPieceAt(des);
                        if (target != null && !aiBoard.isValidCapture(view, src, des)) continue;
                        AIConcludeMove(src, des, target,aiBoard);
                    }
                    else if(!aiBoard.isValidMove(view,src,des)&&!aiBoard.isValidCapture(view,src,des)) continue;
                    else AIConcludeMove(src, des, target,aiBoard);
                    int temp = alphabeta(-1*player,alpha,beta,depth+1,aiBoard);
                    AIUndo(aiBoard);
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
    private int getScore(AIBoard aiBoard){
        int Score = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint point = new ChessboardPoint(i,j);
                ChessPiece chessPiece = aiBoard.getChessPieceAt(point);
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
    public void AIConcludeMove(ChessboardPoint src,ChessboardPoint des,ChessPiece target,AIBoard aiBoard){
        aiBoard.push(src,des,target);
        ChessPiece chess = aiBoard.getChessPieceAt(src);
        if(target!=null&&aiBoard.isValidCapture(view,src,des)){
            aiBoard.minusCount(target.getOwner()==PlayerColor.RED);
            aiBoard.captureChessPiece(view,src,des);
        }else aiBoard.moveChessPiece(src, des);
        onTrap(src,des,chess);
    }
    public void AIUndo(AIBoard aiBoard){
        Object[] output = aiBoard.getStack().pop();
        ChessboardPoint src = (ChessboardPoint)output[0];
        ChessboardPoint des = (ChessboardPoint)output[1];
        ChessPiece target = (ChessPiece)output[2];
        ChessPiece chess = aiBoard.getChessPieceAt(des);
        if(chess!=null&&view.getGridComponentAt(des).getPlayerColor()!=chess.getOwner()){
            if(view.getGridComponentAt(src).getPlayerColor()!=chess.getOwner()&&view.trapCell.contains(src)){
                chess.setRank(0);
            }
            else if(view.trapCell.contains(des)){
                chess.setRank();
            }
        }
        aiBoard.moveChessPiece(des,src);
        if(target!=null){
            aiBoard.addCount(target.getOwner().equals(PlayerColor.BLUE));
            aiBoard.setChessPiece(des,target);
        }
        over = false;
    }
    private void onTrap(ChessboardPoint selectedPoint,ChessboardPoint point,ChessPiece chess){
        if(view.getGridComponentAt(point).getPlayerColor()!=chess.getOwner()){
            if(view.trapCell.contains(point)){
                chess.setRank(0);
            }
            else if(view.trapCell.contains(selectedPoint)){
                chess.setRank();
            }
        }
    }
    public AIBoard transform(Chessboard model,GameController game){
        AIBoard aiBoard = new AIBoard();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint point = new ChessboardPoint(i,j);
                ChessPiece chessPiece = model.getChessPieceAt(point);
                if(chessPiece!=null){
                    aiBoard.setChessPiece(point,new ChessPiece(chessPiece.getOwner(),chessPiece.getName(),chessPiece.getRank()));
                }
            }
        }
        for (Object[] objArray : game.getStack()) {
            aiBoard.getStack().push(objArray.clone());
        }
        return aiBoard;
    }
}
