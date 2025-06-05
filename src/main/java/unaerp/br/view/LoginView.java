package unaerp.br.view;

import unaerp.br.config.HibernateUtil;
import unaerp.br.controller.UserController;
import unaerp.br.model.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserController userController;

    public LoginView() {
        userController = new UserController();

        setTitle("Login - Gerenciador Financeiro Pessoal");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        attachListeners();
    }

    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Entrar");
        registerButton = new JButton("Cadastrar");
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nome de Usuário:"), gbc);

        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void attachListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterView();
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                userController.close();
                System.out.println("LoginView fechada, recursos do UserController liberados.");
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome de usuário e senha não podem estar vazios.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<User> userOpt = userController.login(username, password);

        if (userOpt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            MainDashboardView dashboard = new MainDashboardView(userOpt.get());
            dashboard.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Nome de usuário ou senha inválidos.", "Falha no Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegisterView() {
        RegisterView registerView = new RegisterView(this);
        registerView.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                HibernateUtil.getEntityManager().close();
            } catch (Throwable ex) {
                System.err.println("Falha na inicialização do Hibernate: " + ex.getMessage());
            }
            new LoginView().setVisible(true);
        });
    }
}