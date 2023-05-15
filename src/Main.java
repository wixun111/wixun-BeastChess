import view.ChooseMode;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChooseMode(700, 600));
    }
}
