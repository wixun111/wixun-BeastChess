package ai;

import controller.GameController;
import model.ChessPiece;
import model.Chessboard;
import model.ChessboardPoint;
import model.PlayerColor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;


public class AI {

    private static final int INF = 999999;
    private int difficulty;

    AtomicInteger maxAtomic = new AtomicInteger();
    AtomicInteger rowAtomic = new AtomicInteger();
    AtomicInteger colAtomic = new AtomicInteger();
    AtomicInteger xAtomic = new AtomicInteger();
    AtomicInteger yAtomic = new AtomicInteger();
    int player = 0;

    public AI(int difficulty){
        this.difficulty = difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void AiTurn(Chessboard model, GameController game){
        maxAtomic.set(-INF);
        if(game.isOver()) return;
        if(game.getCurrentPlayer()==PlayerColor.RED) player = -1;
        else player = 1;
        int[] dirx ={1,0,0,-1};int[] diry = {0,1,-1,0};
        long current1=System.currentTimeMillis();
        AIBoard aiBoard = transform(model,game);
        ExecutorService executor = Executors.newFixedThreadPool(12);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                int[] src = new int[]{i,j};
                int[] chess = aiBoard.getChessPieceAt(src);
                if (chess[0] == 0 || chess[3] != player) continue;
                for (int k = 0; k < 4; k++) {
                    int finalI = i;
                    int finalJ = j;
                    int finalK = k;
                    if (finalI + dirx[finalK] < 0 || finalI + dirx[finalK] > 8 || finalJ + diry[finalK] < 0 || finalJ + diry[finalK] > 6) continue;
                    if (game.getStack().size()>6){
                        ChessboardPoint finalSrc = (ChessboardPoint)game.getStack().get(game.getStack().size()-4)[0];
                        ChessboardPoint finalDes = (ChessboardPoint)game.getStack().get(game.getStack().size()-4)[1];
//                        System.out.printf("(%d %d)\n",finalSrc.getRow()+1,finalSrc.getCol()+1);
//                        System.out.printf("(%d %d)\n",finalDes.getRow()+1,finalDes.getCol()+1);
//                        System.out.printf("(%d %d)\n",i+1,j+1);
//                        System.out.printf("(%d %d)\n\n",i+dirx[k]+1,j+diry[k]+1);
                        if(finalSrc.getRow()==finalI&&finalSrc.getCol()==finalJ&&finalDes.getRow()==finalI+dirx[finalK]&&finalDes.getCol()==finalJ+diry[finalK]){
                            continue;
                        }
                    }
                    AIBoard finalAiBoard = transform(model,game);
                    int[] finalChess = finalAiBoard.getChessPieceAt(src);
                    executor.submit(() -> {
                        int[] des = new int[]{finalI + dirx[finalK], finalJ + diry[finalK]};
                        int[] target = finalAiBoard.getChessPieceAt(des);
                        if ((finalChess[0]==7 || finalChess[0]==6) && finalAiBoard.getTerrainAt(des)[0]==3) {
                            des = finalAiBoard.Jump(src, des);
                            if (des == null) return;
                            target = finalAiBoard.getChessPieceAt(des);
                            if (target[0] != 0 && !finalAiBoard.isValidCapture(src, des)) return;
                            finalAiBoard.AIConcludeMove(src, des, target);
                        } else if (!finalAiBoard.isValidMove(src,des) && !finalAiBoard.isValidCapture(src,des)) return;
                        else finalAiBoard.AIConcludeMove(src, des, target);
//                        for (int m = 0; m < 9; m++) {
//                            for (int n = 0; n < 7; n++) {
//                                int[] point = new int[]{m,n};
//                                int[] chess1 = finalAiBoard.getChessPieceAt(point);
//                                System.out.printf("%2d",chess1[0]*chess1[3]);
//                            }
//                            System.out.println();
//                        }
//                        System.out.println();
                        int temp = alphabeta(player*-1, -INF, INF, 1,finalAiBoard);
//                        System.out.println("depth:"+0+"   score:"+temp);
                        finalAiBoard.AIUndo();
                        if(temp>maxAtomic.get()){
                            maxAtomic.set(temp);
                            rowAtomic.set(finalI);
                            colAtomic.set(finalJ);
                            xAtomic.set(des[0]);
                            yAtomic.set(des[1]);
                        }
                    });
                }
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            if(game.getMode()==3) sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(game.getCurrentPlayer()!=PlayerColor.RED&&game.getMode()==3) return;
        ChessboardPoint src = new ChessboardPoint(rowAtomic.get(), colAtomic.get());
        ChessboardPoint des = new ChessboardPoint(xAtomic.get(), yAtomic.get());
        game.concludeMove(src, des, model.getChessPieceAt(des));
        System.out.println("Score:" + maxAtomic.get());
        long current2=System.currentTimeMillis();
        System.out.printf("The running time is %.3f second\n",(current2-current1)/1000.0d);
    }
    private int alphabeta(int player,int alpha,int beta, int depth,AIBoard aiBoard){
        if(depth == difficulty||aiBoard.isOver()){
            return aiBoard.getScore(this.player);
        }
        if(alpha>20000&&player==-1) return alpha;
        int[] dirx ={1,0,0,-1};int[] diry = {0,1,-1,0};
        if(player==1)
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                int[] src = new int[]{i,j};
                int[] chess = aiBoard.getChessPieceAt(src);
                if(chess[0]==0||chess[3]!=player) continue;
                for (int k = 0; k < 4; k++) {
                    if(i+dirx[k]<0||i+dirx[k]>8||j+diry[k]<0||j+diry[k]>6) continue;
                    int[] des = new int[]{i+dirx[k],j+diry[k]};
                    int[] target = aiBoard.getChessPieceAt(des);
                    if ((chess[0]==7 || chess[0]==6) && aiBoard.getTerrainAt(des)[0]==3) {
                        des = aiBoard.Jump(src, des);
                        if (des == null) continue;
                        target = aiBoard.getChessPieceAt(des);
                        if (target[0] != 0 && !aiBoard.isValidCapture(src, des)) continue;
                        aiBoard.AIConcludeMove(src, des, target);
                    }else if(!aiBoard.isValidMove(src,des)&&!aiBoard.isValidCapture(src,des)) continue;
                    else aiBoard.AIConcludeMove(src, des, target);
//                    if(depth<=2){
//                        for (int m = 0; m < 9; m++) {
//                            for (int n = 0; n < 7; n++) {
//                                int[] point = new int[]{m,n};
//                                int[] chess1 = aiBoard.getChessPieceAt(point);
//                                System.out.printf("%2d",chess1[1]*chess1[3]);
//                            }
//                            System.out.println();
//                        }
//                        System.out.println();
//                    }
                    int temp = alphabeta(-1*player,alpha,beta,depth+1,aiBoard);
//                    if(depth<=2) {
//                        System.out.println("depth:" + depth + "   score:" + temp);
//                        System.out.println();
//                    }
                    aiBoard.AIUndo();
                    beta = Math.min(beta,temp);
                    if(beta <= alpha){
                        return beta;
                    }
                }
            }
        }
        else
            for (int i = 8; i >= 0; i--) {
                for (int j = 6; j >=0; j--) {
                    int[] src = new int[]{i,j};
                    int[] chess = aiBoard.getChessPieceAt(src);
                    if(chess[0]==0||chess[3]!=player) continue;
                    for (int k = 0; k < 4; k++) {
                        if(i+dirx[k]<0||i+dirx[k]>8||j+diry[k]<0||j+diry[k]>6) continue;
                        int[] des = new int[]{i+dirx[k],j+diry[k]};
                        int[] target = aiBoard.getChessPieceAt(des);
                        if ((chess[0]==7 || chess[0]==6) && aiBoard.getTerrainAt(des)[0]==3) {
                            des = aiBoard.Jump(src, des);
                            if (des == null) continue;
                            target = aiBoard.getChessPieceAt(des);
                            if (target[0] != 0 && !aiBoard.isValidCapture(src, des)) continue;
                            aiBoard.AIConcludeMove(src, des, target);
                        }else if(!aiBoard.isValidMove(src,des)&&!aiBoard.isValidCapture(src,des)) continue;
                        else aiBoard.AIConcludeMove(src, des, target);
//                        if(depth<=2){
//                            for (int m = 0; m < 9; m++) {
//                                for (int n = 0; n < 7; n++) {
//                                    int[] point = new int[]{m,n};
//                                    int[] chess1 = aiBoard.getChessPieceAt(point);
//                                    System.out.printf("%2d",chess1[1]*chess1[3]);
//                                }
//                                System.out.println();
//                            }
//                            System.out.println();
//                        }
                        int temp = alphabeta(-1*player,alpha,beta,depth+1,aiBoard);
//                        if(depth<=2) {
//                            System.out.println("depth:" + depth + "   score:" + temp);
//                            System.out.println();
//                        }
                        aiBoard.AIUndo();
                        alpha = Math.max(alpha,temp);
                        if(beta <= alpha){
                            return alpha;
                        }
                    }
                }
            }
        if(player==-1)
            return alpha;
        return beta;
    }
    public AIBoard transform(Chessboard model,GameController game){
        int redCount = 0;int blueCount = 0;
        AIBoard aiBoard = new AIBoard();
        for (Object[] objArray : game.getStack()) {
            ChessboardPoint temp = (ChessboardPoint)objArray[0];
            int[] src = new int[]{temp.getRow(),temp.getCol()};
            temp = (ChessboardPoint)objArray[1];
            int[] des = new int[]{temp.getRow(),temp.getCol()};
            ChessPiece chessPiece = (ChessPiece) objArray[2];
            int[] chess = null;
            if(chessPiece!=null){
                chess = switch (chessPiece.getName()) {
                    case "Elephant" -> new int[]{8,8,2300,0};
                    case "Lion" -> new int[]{7,7,1500,0};
                    case "Tiger" -> new int[]{6,6,1300,0};
                    case "Leopard" -> new int[]{5,5,1000,0};
                    case "Wolf" -> new int[]{4,4,800,0};
                    case "Dog"  -> new int[]{3,3,700,0};
                    case "Cat" -> new int[]{2,2,400,0};
                    case  "Rat"-> new int[]{1,1,800,0};
                    default -> new int[]{0,0,0,0};
                };
                chess[3] = chessPiece.getOwner()==PlayerColor.RED?-1:1;
            }
            aiBoard.getStack().push(new int[][]{src,des,chess});
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessPiece chess = model.getChessPieceAt(new ChessboardPoint(i,j));
                if(chess==null) continue;
                aiBoard.setGrid(i,j,chess);
                if(aiBoard.getChessPieceAt(new int[]{i,j})[3]==1)blueCount++;
                else redCount++;
                aiBoard.setCount(redCount,blueCount);
            }
        }
        return aiBoard;
    }
}
