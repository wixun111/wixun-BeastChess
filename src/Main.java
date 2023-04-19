import Net.Client;
import controller.GameController;
import model.Chessboard;
import view.ChessGameFrame;
import view.Choosemode;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Choosemode frame = new Choosemode(700,400);
//            ChessGameFrame mainFrame = new ChessGameFrame(1100, 750);
//            GameController gameController = null;
//            try {
//                gameController = new GameController(mainFrame.getChessboardComponent(), new Chessboard(false),false);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            mainFrame.setGame(gameController);
//            mainFrame.setVisible(true);
        });
    }
}
