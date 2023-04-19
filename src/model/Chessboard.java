package model;

import view.ChessboardComponent;


/**
 * This class store the real chess information.
 * The Chessboard has 9*7 cells, and each cell has a position for chess
 */
public class Chessboard {
    private Cell[][] grid;

    public Chessboard(boolean isLoad) {
        this.grid = new Cell[Constant.CHESSBOARD_ROW_SIZE.getNum()][Constant.CHESSBOARD_COL_SIZE.getNum()];//19X19
        initGrid();
        if(!isLoad){
            initPieces();
        }
    }

    private void initGrid() {
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    private void initPieces() {
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
    public void setGrid(int row, int col, int rank, String color, String name){
        grid[row][col].setPiece(new ChessPiece(color.equals("BLUE")?PlayerColor.BLUE:PlayerColor.RED,name,rank));
    }

    public ChessPiece getChessPieceAt(ChessboardPoint point) {
        return getGridAt(point).getPiece();
    }

    private Cell getGridAt(ChessboardPoint point) {
        return grid[point.getRow()][point.getCol()];
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
        view.removeChessComponentAtGrid(dest);
    }

    public Cell[][] getGrid() {
        return grid;
    }
    public PlayerColor getChessPieceOwner(ChessboardPoint point) {
        return getGridAt(point).getPiece().getOwner();
    }

    public boolean isValidMove(ChessboardComponent view,ChessboardPoint src, ChessboardPoint dest) {
        if(getChessPieceAt(src) != null&&(getChessPieceAt(src).getName().equals("Tiger")||getChessPieceAt(src).getName().equals("Lion"))&&!(calculateDistance(src, dest)==1)){
            return canJump(view,src,dest);
        }
        if (getChessPieceAt(src) == null || getChessPieceAt(dest) != null||(view.riverCell.contains(dest)&&!getChessPieceAt(src).getName().equals("Rat"))) {
            System.out.println(("Illegal chess move!"));
            return false;
        }
        //return true;
        return calculateDistance(src, dest) == 1;
    }


    public boolean isValidCapture(ChessboardComponent view,ChessboardPoint src, ChessboardPoint dest) {
        if(getChessPieceAt(src) != null&&(getChessPieceAt(src).getName().equals("Tiger")||getChessPieceAt(src).getName().equals("Lion"))&&!(calculateDistance(src, dest)==1)){
            return canJump(view,src,dest);
        }
        if((view.riverCell.contains(dest)||view.riverCell.contains(src))&&!getChessPieceAt(src).getName().equals("Rat")){
            return false;
        }
        return calculateDistance(src, dest) == 1;
        //return true;
    }
    public boolean canJump(ChessboardComponent view,ChessboardPoint src, ChessboardPoint dest){
        System.out.println(dest.toString());
        if(src.getRow()==dest.getRow()){
            ChessboardPoint temp = new ChessboardPoint(src.getRow(),Math.min(src.getCol(),dest.getCol()));
            for (int i = temp.getCol() + 1; i < Math.max(src.getCol(),dest.getCol()); i++) {
                temp.setCol(i);
                if(!view.riverCell.contains(temp)||getChessPieceAt(temp) != null){
                    return false;
                }
            }
        }
        else if(src.getCol()==dest.getCol()){
            ChessboardPoint temp = new ChessboardPoint(Math.min(src.getRow(),dest.getRow()),dest.getCol());
            for (int i = temp.getRow() + 1; i < Math.max(src.getRow(),dest.getRow()); i++) {
                temp.setRow(i);
                if(!view.riverCell.contains(temp)||getChessPieceAt(temp) != null){
                    return false;
                }
            }
        }
        return !view.riverCell.contains(dest)&&(src.getRow()==dest.getRow()||src.getCol()==dest.getCol());
    }
}
