package view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Choosemode extends JFrame {
    private final int WIDTH;
    private final int HEIGTH;

    public Choosemode(int width, int height){
        this.HEIGTH = height;
        this.WIDTH = width;
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        addSaveButton();
    }
    private void addSaveButton() {
        JButton button = new JButton("保存");
        button.addActionListener((e) -> {});
        button.setLocation(HEIGTH/10, HEIGTH /10);
        button.setSize(10, 30);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }
}
