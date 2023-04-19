import view.Choosemode;

import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Choosemode frame = new Choosemode(700,400);
        });
    }
}
