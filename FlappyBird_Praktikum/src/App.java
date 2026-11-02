import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            Logic logic = new Logic();
            View tampilan = new View(logic);
            logic.setView(tampilan);

            frame.add(tampilan);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            tampilan.requestFocusInWindow();
        });
    }
}
