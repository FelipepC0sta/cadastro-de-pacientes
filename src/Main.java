import view.TelaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    null,
                    "Ocorreu um erro inesperado na aplicação:\n" + throwable.getMessage(),
                    "Erro no Sistema",
                    JOptionPane.ERROR_MESSAGE
            ));
        });

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            TelaPrincipal telaPrincipal = new TelaPrincipal();
            telaPrincipal.setVisible(true);
        });
    }
}
