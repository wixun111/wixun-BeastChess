package model;

import view.ChessboardComponent;

import java.io.Serializable;


/**
 * This class store the real chess information.
 * The Chessboard has 9*7 cells, and each cell has a position for chess
 */
public class Chessboard implements Serializable {
    private static final long serialVersionUID = 245L;
    private final Cell[][] grid;
    private int redCount,blueCount;

    public Chessboard(boolean isLoad) {
        this.grid = new Cell[Constant.CHESSBOARD_ROW_SIZE.getNum()][Constant.CHESSBOARD_COL_SIZE.getNum()];//19X19
        initGrid();
        if(!isLoad) initPieces();
    }
    public Chessboard(Cell[][] grid,int redCount,int blueCount) {
        this.grid = grid;
        this.redCount = redCount;
        this.blueCount = blueCount;
    }

    private void initGrid() {
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    public void initPieces() {
        redCount=8;blueCount=8;
        grid[0][0].setPiece(new ChessPiece(PlayerColor.RED, "Lion",7));
        grid[0][6].setPiece(new ChessPiece(PlayerColor.RED, "Tiger",6));
        grid[1][1].setPiece(new ChessPiece(PlayerColor.RED, "Dog",3));
        grid[1][5].setPiece(new ChessPiece(PlayerColor.RED, "Cat",2));
        grid[2][0].setPiece(new ChessPiece(PlayerColor.RED, "Rat",1));
        grid[2][2].setPiece(new ChessPiece(PlayerColor.RED, "Leopard",5));
        grid[2][4].setPiece(new ChessPiece(PlayerColor.RED, "Wolf",4));
        grid[2][6].setPiece(new ChessPiece(PlayerColor.RED, "Elephant",8));
        grid[8][6].setPiece(new ChessPiece(PlayerColor.BLUE, "Lion",7));
        grid[8][0].setPiece(new ChessPiece(PlayerColor.BLUE, "Tiger",6));
        grid[7][5].setPiece(new ChessPiece(PlayerColor.BLUE, "Dog",3));
        grid[7][1].setPiece(new ChessPiece(PlayerColor.BLUE, "Cat",2));
        grid[6][6].setPiece(new ChessPiece(PlayerColor.BLUE, "Rat",1));
        grid[6][4].setPiece(new ChessPiece(PlayerColor.BLUE, "Leopard",5));
        grid[6][2].setPiece(new ChessPiece(PlayerColor.BLUE, "Wolf",4));
        grid[6][0].setPiece(new ChessPiece(PlayerColor.BLUE, "Elephant",8));
    }
    public void removePieces(){
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
                grid[i][j].removePiece();
            }
        }
    }
    public void setGrid(int row, int col, int rank, String color, String name){
        grid[row][col].setPiece(new ChessPiece(color.equals("BLUE")?PlayerColor.BLUE:PlayerColor.RED,name,rank));
    }

    public ChessPiece getChessPieceAt(ChessboardPoint point) {
        return getGridAt(point).getPiece();
    }
    private Cell getGridAt(ChessboardPoint point) {
        return grid[point.getRow()][point.getCol()];
    }

    public int getBlueCount() {
        return blueCount;
    }

    public int getRedCount() {
        return redCount;
    }

    public void addCount(Boolean flag) {
        if(flag) blueCount++;
        else  redCount++;
    }
    public void minusCount(Boolean flag) {
        if(flag) blueCount--;
        else  redCount--;
    }

    private int calculateDistance(ChessboardPoint src, ChessboardPoint dest) {
        return Math.abs(src.getRow() - dest.getRow()) + Math.abs(src.getCol() - dest.getCol());
    }

    private ChessPiece removeChessPiece(ChessboardPoint point) {
        ChessPiece chessPiece = getChessPieceAt(point);
        getGridAt(point).removePiece();
        return chessPiece;
    }

    public void setChessPiece(ChessboardPoint point, ChessPiece chessPiece) {
        getGridAt(point).setPiece(chessPiece);
    }

    public void moveChessPiece(ChessboardPoint src, ChessboardPoint dest) {
        setChessPiece(dest, removeChessPiece(src));
    }

    public void captureChessPiece(ChessboardComponent view,ChessboardPoint src, ChessboardPoint dest) {
        setChessPiece(dest, removeChessPiece(src));
    }

    public Cell[][] getGrid() {
        return grid;
    }
    public PlayerColor getChessPieceOwner(ChessboardPoint point) {
        return getGridAt(point).getPiece().getOwner();
    }

    public boolean isValidMove(ChessboardComponent view,ChessboardPoint src, ChessboardPoint dest) {
        if(getChessPieceAt(src)!=null&&(getChessPieceAt(src).getName().equals("Tiger")||getChessPieceAt(src).getName().equals("Lion"))&&!(calculateDistance(src, dest)==1)){
            return canJump(view,src,dest);
        }
        if (view.getDenCell().contains(dest)&&view.getGridComponentAt(dest).getPlayerColor()==getChessPieceOwner(src)) return false;
        if (getChessPieceAt(src)==null||getChessPieceAt(dest)!=null||(view.riverCell.contains(dest)&&!getChessPieceAt(src).getName().equals("Rat"))) {
            return false;
        }
        return calculateDistance(src, dest) == 1;
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
}