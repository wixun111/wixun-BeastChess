import utils.SoundPlay;
import view.ChooseFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.noddraw", "true");
        SwingUtilities.invokeLater(() -> {
            new ChooseFrame(700, 600);
            new SoundPlay().playBgm("resource\\Sound\\bg.wav");
        });
    }
}
