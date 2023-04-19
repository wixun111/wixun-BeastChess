package view;


import model.ChessPiece;
import model.PlayerColor;

import javax.swing.*;
import java.awt.*;

/**
 * This is the equivalent of the ChessPiece class,
 * but this class only cares how to draw Chess on ChessboardComponent
 */
public class ChessComponent extends JComponent {
    private PlayerColor owner;
    private ChessPiece piece;
    private boolean selected;

    public ChessComponent(ChessPiece piece, PlayerColor owner, int size) {
        this.piece = piece;
        this.owner = owner;
        this.selected = false;
        setSize(size/2, size/2);
        setLocation(0,0);
        setVisible(true);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("楷体", Font.PLAIN, getWidth() / 2);
        g2.setFont(font);
        g2.setColor(owner.getColor());
        if(piece.getName().equals("Elephant")) {
            g2.drawString("象", getWidth() / 4, getHeight() * 5 / 8+4);
        }else if(piece.getName().equals("Lion")){
            g2.drawString("狮", getWidth() / 4, getHeight() * 5 / 8+4);
        }else if(piece.getName().equals("Tiger")){
            g2.drawString("虎", getWidth() / 4, getHeight() * 5 / 8+4);
        }else if(piece.getName().equals("Leopard")){
            g2.drawString("豹", getWidth() / 4, getHeight() * 5 / 8+4);
        }else if(piece.getName().equals("Wolf")){
            g2.drawString("狼", getWidth() / 4, getHeight() * 5 / 8+4);
        }else if(piece.getName().equals("Dog")){
            g2.drawString("狗", getWidth() / 4, getHeight() * 5 / 8+4);
        }else if(piece.getName().equals("Cat")){
            g2.drawString("猫", getWidth() / 4, getHeight() * 5 / 8+4);
        }else if(piece.getName().equals("Rat")){
            g2.drawString("鼠", getWidth() / 4, getHeight() * 5 / 8+4);
        }
        if (isSelected()) { // Highlights the model if selected.
            g.drawOval(5, 5, getWidth()-10, getHeight()-10);
        }
    }
}
