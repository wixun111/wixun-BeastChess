package view;

import model.PlayerColor;

import javax.swing.*;
import java.awt.*;

/**
 * This is the equivalent of the Cell class,
 * but this class only cares how to draw Cells on ChessboardComponent
 */

public class CellComponent extends JPanel {
    private final Image image;
    private final PlayerColor playerColor;

    public CellComponent(Image background, Point location, int size,PlayerColor playerColor) {
        setLayout(new GridLayout(1,1));
        setLocation(location);
        setSize(size, size);
        this.image = background;
        this.playerColor = playerColor;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
