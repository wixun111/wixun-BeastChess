package view;


import controller.GameController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static model.Constant.CHESSBOARD_COL_SIZE;
import static model.Constant.CHESSBOARD_ROW_SIZE;

/**
 * This class represents the checkerboard component object on the panel
 */
public class ChessboardComponent extends JComponent implements Serializable {
    @Serial
    private static final long serialVersionUID = 23L;
    private final CellComponent[][] gridComponents = new CellComponent[CHESSBOARD_ROW_SIZE.getNum()][CHESSBOARD_COL_SIZE.getNum()];
    private final int CHESS_SIZE;
    public final Set<ChessboardPoint> riverCell = new HashSet<>();
    public final Set<ChessboardPoint> denCell = new HashSet<>();
    public final Set<ChessboardPoint> trapCell = new HashSet<>();

    private GameController gameController;


    public ChessboardComponent(int chessSize) {
        CHESS_SIZE = chessSize;
        int width = CHESS_SIZE * 7+1;
        int height = CHESS_SIZE * 9+1;
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);// Allow mouse events to occur
        setLayout(null); // Use absolute layout.
        setSize(width, height);
        System.out.printf("chessboard width, height = [%d : %d], chess size = %d\n", width, height, CHESS_SIZE);
        initiateGridComponents();
    }

    public void initiateChessComponent(Chessboard chessboard) {
        Cell[][] grid = chessboard.getGrid();
        for (int i = 0; i < CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < CHESSBOARD_COL_SIZE.getNum(); j++) {
                if (grid[i][j].getPiece() != null) {
                    ChessPiece chessPiece = grid[i][j].getPiece();
                    gridComponents[i][j].add(new ChessComponent(chessPiece,chessPiece.getOwner(), CHESS_SIZE));
                }
            }
        }
    }
    public void removeChessComponent(){
        for (int i = 0; i < CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < CHESSBOARD_COL_SIZE.getNum(); j++) {
                gridComponents[i][j].removeAll();
                gridComponents[i][j].revalidate();
            }
        }
    }
    public int getChessSize(){
        return CHESS_SIZE;
    }

    public Set<ChessboardPoint> getRiverCell() {
        return riverCell;
    }

    public Set<ChessboardPoint> getDenCell() {
        return denCell;
    }

    public void initiateGridComponents() {
        riverCell.add(new ChessboardPoint(3,1));
        riverCell.add(new ChessboardPoint(3,2));
        riverCell.add(new ChessboardPoint(4,1));
        riverCell.add(new ChessboardPoint(4,2));
        riverCell.add(new ChessboardPoint(5,1));
        riverCell.add(new ChessboardPoint(5,2));
        riverCell.add(new ChessboardPoint(3,4));
        riverCell.add(new ChessboardPoint(3,5));
        riverCell.add(new ChessboardPoint(4,4));
        riverCell.add(new ChessboardPoint(4,5));
        riverCell.add(new ChessboardPoint(5,4));
        riverCell.add(new ChessboardPoint(5,5));
        trapCell.add(new ChessboardPoint(8,2));
        trapCell.add(new ChessboardPoint(8,4));
        trapCell.add(new ChessboardPoint(7,3));
        trapCell.add(new ChessboardPoint(0,2));
        trapCell.add(new ChessboardPoint(0,4));
        trapCell.add(new ChessboardPoint(1,3));
        denCell.add(new ChessboardPoint(0,3));
        denCell.add(new ChessboardPoint(8,3));
        for (int i = 0; i < CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < CHESSBOARD_COL_SIZE.getNum(); j++) {
                ChessboardPoint temp = new ChessboardPoint(i, j);
                PlayerColor playerColor = i>4?PlayerColor.BLUE:PlayerColor.RED;
                CellComponent cell;
                if (riverCell.contains(temp)) {
                    cell = new CellComponent(Color.CYAN, calculatePoint(i, j), CHESS_SIZE,null);
                    this.add(cell);
                } else if(denCell.contains(temp)) {
                    cell = new CellComponent(Color.orange, calculatePoint(i, j), CHESS_SIZE,playerColor);
                    this.add(cell);
                } else if(trapCell.contains(temp)) {
                    cell = new CellComponent(Color.GREEN, calculatePoint(i, j), CHESS_SIZE,playerColor);
                    this.add(cell);
                } else {
                    cell = new CellComponent(Color.LIGHT_GRAY, calculatePoint(i, j), CHESS_SIZE,null);
                    this.add(cell);
                }
                gridComponents[i][j] = cell;
            }
        }
    }

    public void registerController(GameController gameController) {
        this.gameController = gameController;
    }

    public void setChessComponentAtGrid(ChessboardPoint point, ChessComponent chess) {
        getGridComponentAt(point).add(chess);
    }
    public ChessComponent removeChessComponentAtGrid(ChessboardPoint point) {
        ChessComponent chess = (ChessComponent) getGridComponentAt(point).getComponents()[0];
        getGridComponentAt(point).removeAll();
        getGridComponentAt(point).revalidate();
        chess.setSelected(false);
        return chess;
    }

    public CellComponent getGridComponentAt(ChessboardPoint point) {
        return gridComponents[point.getRow()][point.getCol()];
    }
    public ChessComponent getChessComponentAt(ChessboardPoint point){
        return (ChessComponent) getGridComponentAt(point).getComponents()[0];
    }
    private ChessboardPoint getChessboardPoint(Point point) {
        System.out.println("[" + point.y/CHESS_SIZE +  ", " +point.x/CHESS_SIZE + "] Clicked");
        return new ChessboardPoint(point.y/CHESS_SIZE, point.x/CHESS_SIZE);
    }
    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.yellow);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        for (int i = 0; i < 9; i++) {
//            for (int j = 0; j < 9; j++) {
//                if(j<3) {
//                    g.setColor(Color.RED);
//                    g.drawRect(i*CHESS_SIZE ,j*CHESS_SIZE ,CHESS_SIZE,CHESS_SIZE);
//                }
//                else if(j>5){
//                    g.setColor(Color.CYAN);
//                    g.drawRect(i*CHESS_SIZE ,j*CHESS_SIZE ,CHESS_SIZE,CHESS_SIZE);
//                }
//            }
//        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            JComponent clickedComponent = (JComponent) getComponentAt(e.getX(), e.getY());
            if (clickedComponent.getComponentCount() == 0) {
                System.out.print("None chess here and ");
                try {
                    gameController.onPlayerClickCell(getChessboardPoint(e.getPoint()), (CellComponent) clickedComponent);
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                System.out.print("One chess here and ");
                ChessPiece chessPiece = gameController.getModel().getChessPieceAt(getChessboardPoint(e.getPoint()));
                System.out.print("Piece:"+chessPiece+"  Rank:" + chessPiece.getRank()+"\n");
                gameController.onPlayerClickChessPiece(getChessboardPoint(e.getPoint()), (ChessComponent) clickedComponent.getComponents()[0]);
            }
        }
    }
}
