import utils.SoundPlay;
import view.ChooseFrame;
import view.StartFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.noddraw", "true");
        SwingUtilities.invokeLater(() -> {
            new StartFrame(1000, 750);
            new SoundPlay().playBgm("resource\\Sound\\bg.wav");
        });
    }
}
