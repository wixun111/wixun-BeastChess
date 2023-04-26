package ai;

import model.Cell;
import model.ChessPiece;
import model.ChessboardPoint;
import model.PlayerColor;
import view.ChessboardComponent;

import java.util.Stack;

public class AIBoard {

    private int redCount,blueCount;
    private ChessPiece grid[][] = new ChessPiece[9][7];
    private Stack<Object[]> stack = new Stack<>();
    private Object[] output;
    public AIBoard(){
    }
    public void moveChessPiece(ChessboardPoint src, ChessboardPoint dest) {
        setChessPiece(dest, removeChessPiece(src));
    }

    public Stack<Object[]> getStack() {
        return stack;
    }

    public void captureChessPiece(ChessboardComponent view, ChessboardPoint src, ChessboardPoint dest) {
        setChessPiece(dest, removeChessPiece(src));
    }
    private ChessPiece removeChessPiece(ChessboardPoint point) {
        ChessPiece chessPiece = getChessPieceAt(point);
        grid[point.getRow()][point.getCol()]=null;
        return chessPiece;
    }
    public void setChessPiece(ChessboardPoint point, ChessPiece chessPiece) {
        grid[point.getRow()][point.getCol()]=chessPiece;
    }
    public void addCount(Boolean flag) {
        if(flag) blueCount++;
        else  redCount++;
    }
    public void minusCount(Boolean flag) {
        if(flag) blueCount--;
        else  redCount--;
    }
    public boolean isValidMove(ChessboardComponent view, ChessboardPoint src, ChessboardPoint dest) {
        if(getChessPieceAt(src)!=null&&(getChessPieceAt(src).getName().equals("Tiger")||getChessPieceAt(src).getName().equals("Lion"))&&!(calculateDistance(src, dest)==1)){
            return canJump(view,src,dest);
        }
        if (view.getDenCell().contains(dest)&&view.getGridComponentAt(dest).getPlayerColor()==getChessPieceAt(src).getOwner()) return false;
        if (getChessPieceAt(src)==null||getChessPieceAt(dest)!=null||(view.riverCell.contains(dest)&&!getChessPieceAt(src).getName().equals("Rat"))) {
            return false;
        }
        return calculateDistance(src, dest) == 1;
    }
    public ChessPiece getChessPieceAt(ChessboardPoint point) {
        return grid[point.getRow()][point.getCol()];
    }
    private int calculateDistance(ChessboardPoint src, ChessboardPoint dest) {
        return Math.abs(src.getRow() - dest.getRow()) + Math.abs(src.getCol() - dest.getCol());
    }
    public boolean isValidCapture(ChessboardComponent view,ChessboardPoint src, ChessboardPoint dest) {
        ChessPiece chess = getChessPieceAt(src);
        ChessPiece target = getChessPieceAt(dest);
        if(target==null) return false;
        if(!chess.canCapture(target)) return false;
        if(!(calculateDistance(src, dest)==1)&&chess!=null&&(chess.getName().equals("Tiger")||chess.getName().equals("Lion"))){
            return canJump(view,src,dest);
        }
        if(view.riverCell.contains(src)&&chess.getName().equals("Rat")&&target.getName().equals("Elephant")){
            return false;
        }
        if(view.riverCell.contains(dest)&&!chess.getName().equals("Rat")){
            return false;
        }
        return calculateDistance(src, dest) == 1;
    }
    public boolean canJump(ChessboardComponent view,ChessboardPoint src, ChessboardPoint des){
        if(src.getRow()==des.getRow()){
            ChessboardPoint temp = new ChessboardPoint(src.getRow(),Math.min(src.getCol(),des.getCol()));
            for (int i = temp.getCol() + 1; i < Math.max(src.getCol(),des.getCol()); i++) {
                temp.setCol(i);
                if(!view.riverCell.contains(temp)||getChessPieceAt(temp) != null){
                    return false;
                }
            }
        }
        else if(src.getCol()==des.getCol()){
            ChessboardPoint temp = new ChessboardPoint(Math.min(src.getRow(),des.getRow()),des.getCol());
            for (int i = temp.getRow() + 1; i < Math.max(src.getRow(),des.getRow()); i++) {
                temp.setRow(i);
                if(!view.riverCell.contains(temp)||getChessPieceAt(temp) != null){
                    return false;
                }
            }
        }
        return !view.riverCell.contains(des)&&(src.getRow()==des.getRow()||src.getCol()==des.getCol());
    }
    public ChessboardPoint Jump(ChessboardComponent view,ChessboardPoint src, ChessboardPoint des){
        if(src.getRow()==des.getRow()){
            int row = des.getRow();
            int col = des.getCol();
            int dir = des.getCol()-src.getCol();
            for (int i = col; i != col+2*dir; i+=dir) {
                ChessboardPoint temp = new ChessboardPoint(row,i);
                if(getChessPieceAt(temp) != null) return null;
            }
            return new ChessboardPoint(row,col+2*dir);
        }
        else if(src.getCol()==des.getCol()){
            int row = des.getRow();
            int col = des.getCol();
            int dir = des.getRow()-src.getRow();
            for (int i = row; i != row+3*dir; i+=dir) {
                ChessboardPoint temp = new ChessboardPoint(i,col);
                if(getChessPieceAt(temp) != null) return null;
            }
            return new ChessboardPoint(row+3*dir,col);
        }
        return null;
    }
    public void push(ChessboardPoint src,ChessboardPoint des, ChessPiece target){
        output = new Object[]{src, des, target};
        stack.push(output);
    }

}
