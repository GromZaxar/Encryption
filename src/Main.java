import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EncryptionApp app = new EncryptionApp();
            app.setVisible(true);
        });
    }
}