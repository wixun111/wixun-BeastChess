package listener;

import model.ChessboardPoint;
import view.CellComponent;
import view.ChessComponent;
import view.ChessComponent;

import java.io.IOException;

public interface GameListener {

    void onPlayerClickCell(ChessboardPoint point, CellComponent component) throws IOException, ClassNotFoundException;


    void onPlayerClickChessPiece(ChessboardPoint point, ChessComponent component);

}
