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
    private final ChessPiece piece;
    private boolean selected;

    public ChessComponent(ChessPiece piece, int size) {
        this.piece = piece;
        this.selected = false;
        setSize(size/2, size/2);
        setLocation(0,0);
        setVisible(true);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(piece.getOwner()==PlayerColor.RED)
            switch (piece.getName()) {
                case "Elephant" -> g.drawImage(new ImageIcon("resource\\Picture\\Elephant.png").getImage(), 0,-5, getWidth(), getHeight(), null);
                case "Lion" -> g.drawImage(new ImageIcon("resource\\Picture\\Lion.png").getImage(), 2, -5, getWidth(), getHeight(), null);
                case "Tiger" -> g.drawImage(new ImageIcon("resource\\Picture\\Tiger.png").getImage(), 2, -5, getWidth(), getHeight(), null);
                case "Leopard" -> g.drawImage(new ImageIcon("resource\\Picture\\Leopard.png").getImage(), 2, -5, getWidth(), getHeight(), null);
                case "Wolf" -> g.drawImage(new ImageIcon("resource\\Picture\\Wolf.png").getImage(), 0, -5, getWidth(), getHeight(), null);
                case "Dog" -> g.drawImage(new ImageIcon("resource\\Picture\\Dog.png").getImage(), 1, -5, getWidth(), getHeight(), null);
                case "Cat" -> g.drawImage(new ImageIcon("resource\\Picture\\Cat.png").getImage(), 2, -5, getWidth(), getHeight(), null);
                case "Rat" -> g.drawImage(new ImageIcon("resource\\Picture\\Rat.png").getImage(), -2, -13, getWidth(), getHeight(), null);
            }
        else
            switch (piece.getName()) {
                case "Elephant" -> g.drawImage(new ImageIcon("resource\\Picture\\Elephant B.png").getImage(), 0, 0, getWidth(), getHeight(), null);
                case "Lion" -> g.drawImage(new ImageIcon("resource\\Picture\\Lion B.png").getImage(), 0, 0, getWidth(), getHeight(), null);
                case "Tiger" -> g.drawImage(new ImageIcon("resource\\Picture\\Tiger B.png").getImage(), 0, 0, getWidth(), getHeight(), null);
                case "Leopard" -> g.drawImage(new ImageIcon("resource\\Picture\\Leopard B.png").getImage(), 0, 0, getWidth(), getHeight(), null);
                case "Wolf" -> g.drawImage(new ImageIcon("resource\\Picture\\Wolf B.png").getImage(), 0, 0, getWidth(), getHeight(), null);
                case "Dog" -> g.drawImage(new ImageIcon("resource\\Picture\\Dog B.png").getImage(), 0, -8, getWidth(), getHeight(), null);
                case "Cat" -> g.drawImage(new ImageIcon("resource\\Picture\\Cat B.png").getImage(), 2, -9, getWidth(), getHeight(), null);
                case "Rat" -> g.drawImage(new ImageIcon("resource\\Picture\\Rat B.png").getImage(), 3, -15, getWidth(), getHeight(), null);
            }
        if(selected){
            g.drawImage(new ImageIcon("resource\\Picture\\Selected.png").getImage(), 0, 0, getWidth(), getHeight(), null);
        }
    }
    @Override
    public String toString() {
        return this.piece.getName();
    }
}
