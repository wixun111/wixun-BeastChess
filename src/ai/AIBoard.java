package ai;

import model.ChessPiece;
import model.PlayerColor;
import java.util.Stack;
public class AIBoard {

    private int redCount,blueCount;
    private final int[][][] grid = new int[9][7][4];
    //    name rank value player
    private final int[][][] terrain = new int[9][7][2];
    private final Stack<int[][]> stack = new Stack<>();
    boolean over = false;
    public AIBoard(){
        terrain[3][1] = new int[]{3,0};
        terrain[3][2] = new int[]{3,0};
        terrain[4][1] = new int[]{3,0};
        terrain[4][2] = new int[]{3,0};
        terrain[5][1] = new int[]{3,0};
        terrain[5][2] = new int[]{3,0};
        terrain[3][4] = new int[]{3,0};
        terrain[3][5] = new int[]{3,0};
        terrain[4][4] = new int[]{3,0};
        terrain[4][5] = new int[]{3,0};
        terrain[5][4] = new int[]{3,0};
        terrain[5][5] = new int[]{3,0};
        terrain[8][2] = new int[]{1,1};
        terrain[8][4] = new int[]{1,1};
        terrain[7][3] = new int[]{1,1};
        terrain[0][2] = new int[]{1,-1};
        terrain[0][4] = new int[]{1,-1};
        terrain[1][3] = new int[]{1,-1};
        terrain[0][3] = new int[]{2,-1};
        terrain[8][3] = new int[]{2,1};
    }

    public int[] getTerrainAt(int[] point) {
        return terrain[point[0]][point[1]];
    }
    public Stack<int[][]> getStack() {
        return stack;
    }


    public void setGrid(int i, int j, ChessPiece chess) {
        String name = chess.getName();
        grid[i][j]=switch (name) {
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
        grid[i][j][3] = chess.getOwner()==PlayerColor.RED? -1:1;
        if(terrain[i][j][0]==1&&terrain[i][j][1]!=grid[i][j][3]) grid[i][j][1]=0;
    }

    public void captureChessPiece(int[] src,int[] des) {
        setChessPiece(des, removeChessPiece(src));
        grid[src[0]][src[1]] = new int[]{0,0,0,0};
    }
    private int[] removeChessPiece(int[] point) {
        return getChessPieceAt(point);
    }
    public void moveChessPiece(int[] src,int[] des) {
        setChessPiece(des, removeChessPiece(src));
        grid[src[0]][src[1]] = new int[]{0,0,0,0};
    }
    public void setChessPiece(int[] point,int[] chess) {
        grid[point[0]][point[1]] = new int[]{chess[0],chess[1],chess[2],chess[3]};
    }
    public void addCount(Boolean flag) {
        if(flag) blueCount++;
        else  redCount++;
    }
    public void minusCount(Boolean flag) {
        if(flag) blueCount--;
        else  redCount--;
    }
    public boolean isValidMove(int[] src,int[] des) {
        if(getChessPieceAt(src)[0]!=0&&(getChessPieceAt(src)[0]==6||getChessPieceAt(src)[0]==7)&&calculateDistance(src,des)!=1){
            return !canJump(src, des);
        }
        if(getTerrainAt(des)[0]==2&&getTerrainAt(des)[1]==getChessPieceAt(src)[3]) return true;
        if(getChessPieceAt(src)[0]==0||getChessPieceAt(des)[0]!=0||(getTerrainAt(des)[0]==3&&getChessPieceAt(src)[0]!=1)) {
            return true;
        }
        return calculateDistance(src, des) != 1;
    }
    public int[] getChessPieceAt(int[] point) {
        return grid[point[0]][point[1]];
    }
    private int calculateDistance(int[] src,int[] des) {
        return Math.abs(src[0] - des[0]) + Math.abs(src[1] - des[1]);
    }
    public boolean isValidCapture(int[] src,int[] des) {
        int[] chess = getChessPieceAt(src);
        int[] target = getChessPieceAt(des);
        if(target[0]==0||chess[0]==0) return false;
        if(!canCapture(chess,target)) return false;
        if(calculateDistance(src, des)!=1&&(chess[0]==6||chess[0]==7)){
            return canJump(src,des);
        }
        if(getTerrainAt(src)[0]==3&&chess[0]==1&&target[0]==8){
            return false;
        }
        if(getTerrainAt(des)[0]==3&&chess[0]!=1){
            return false;
        }
        return calculateDistance(src, des) == 1;
    }
    public boolean canJump(int[] src,int[] des){
        if(src[0]==des[0]){
            int[] temp = new int[]{src[0],Math.min(src[1],des[1])};
            for (int i = temp[1] + 1; i < Math.max(src[1],des[1]); i++) {
                temp[1] = i;
                if(getTerrainAt(temp)[0]!=3||getChessPieceAt(temp)[0] != 0){
                    return false;
                }
            }
        }
        else if(src[1]==des[1]){
            int[] temp = new int[]{Math.min(src[0],des[0]),des[1]};
            for (int i = temp[0] + 1; i < Math.max(src[0],des[0]); i++) {
                temp[0] = i;
                if(getTerrainAt(temp)[0]!=3||getChessPieceAt(temp)[0] != 0){
                    return false;
                }
            }
        }
        return getTerrainAt(des)[0]!=3&&(src[0]==des[0]||src[1]==des[1]);
    }
    public int[] Jump(int[] src,int[] des){
        if(src[0]==des[0]){
            int row = des[0];
            int col = des[1];
            int dir = des[1]-src[1];
            for (int i = col; i != col+2*dir; i+=dir) {
                int[] temp = new int[]{row,i};
                if(getChessPieceAt(temp)[0] != 0) return null;
            }
            return new int[]{row,col+2*dir};
        }
        else if(src[1]==des[1]){
            int row = des[0];
            int col = des[1];
            int dir = des[0]-src[0];
            for (int i = row; i != row+3*dir; i+=dir) {
                int[] temp = new int[]{i,col};
                if(getChessPieceAt(temp)[0] != 0) return null;
            }
            return new int[]{row+3*dir,col};
        }
        return null;
    }
    public void push(int[] src,int[] des,int[] chess){
        stack.push(new int[][]{src,des,chess});
    }
    public void AIConcludeMove(int[] src,int[] des,int[] target){
        push(src,des,target);
        int[] chess = getChessPieceAt(src);
        if(target[0]!=0&&isValidCapture(src,des)){
            minusCount(target[3]==1);
            captureChessPiece(src,des);
        }else moveChessPiece(src,des);
        onTrap(src,des,chess);
        win(des,chess);
    }
    public void AIUndo(){
        int[][] output = getStack().pop();
        int[] src = output[0];
        int[] des = output[1];
        int[] target = output[2];
        int[] chess = getChessPieceAt(des);
        if(getTerrainAt(des)[1]!=chess[3]){
            if(getTerrainAt(src)[1]!=chess[3]&&getTerrainAt(src)[0]==1){
                chess[1]=0;
            }
            else if(getTerrainAt(des)[0]==1){
                chess[1]=chess[0];
            }
        }
        moveChessPiece(des,src);
        if(target[0]!=0){
            addCount(target[3]==1);
            setChessPiece(des,target);
        }
        over = false;
    }
    private void onTrap(int[] src,int[] des,int[] chess){
        if(getTerrainAt(des)[1]!=chess[3]){
            if(getTerrainAt(des)[0]==1){
                chess[1]=0;
            }
            else if(getTerrainAt(src)[0]==1){
                chess[1]=chess[0];
            }
        }
    }
    public boolean canCapture(int[] chess,int[] target) {
        if(chess[3]!=target[3]){
            if(target[1]==8&&chess[1]==1){
                return true;
            }

            return target[1] <= chess[1] && !(chess[1] == 8 && target[1] == 1);
        }
        return false;
    }
    public int getScore(int Player){
        int Score = 0;
        for (int i = 0; i <9; i++) {
            for (int j = 0; j < 7; j++) {
                int[] point = new int[]{i,j};
                int[] chess = getChessPieceAt(point);
                if(chess[0] == 0) continue;
                int value = chess[2];
                int name = chess[0];
                boolean own = chess[3] == Player;
                if(Player==-1){
                    Score += own?value/100*i:-value/100*(8-i);
                }else {
                    Score += own?value/100*(8-i):-value/100*i;
                }
                if(name==6||name==7||name==1){
                if(i<6&i>1) Score+=own?150:-150;
                }
                if(getTerrainAt(point)[0]==1&&getTerrainAt(point)[1]!=chess[3]) {
                Score += own?1000:-1000;
                }
                else if(getTerrainAt(point)[0]==2&&getTerrainAt(point)[1]!=chess[3]) {
                Score += own?60000:-30000;
                }
            }
        }
        return Score;
    }
    public void win(int[]des, int[] chess) {
        if((getTerrainAt(des)[0]==2&&getTerrainAt(des)[1]!=chess[3])|| blueCount ==0||redCount==0){
//                System.out.printf("(red: %d    blue: %d)\n",redCount,blueCount);
            over = true;
        }
    }
    public void  setCount(int redCount,int blueCount){
        this.blueCount = blueCount;
        this.redCount = redCount;
    }

    public boolean isOver() {
        return over;
    }
}
