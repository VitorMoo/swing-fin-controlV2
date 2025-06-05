package unaerp.br;

import unaerp.br.config.HibernateUtil;
import unaerp.br.view.LoginView; // Assuming LoginView is created in a 'view' package
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        try {
            HibernateUtil.getEntityManager().close();
            System.out.println("Hibernate initialized successfully.");
        } catch (Throwable ex) {
            System.err.println("Initial Hibernate SessionFactory creation failed: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to initialize the database connection. The application may not function correctly or will now exit.\nError: " + ex.getMessage(),
                    "Database Initialization Error",
                    JOptionPane.ERROR_MESSAGE);

        }

        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            HibernateUtil.close();
        }));
    }
}