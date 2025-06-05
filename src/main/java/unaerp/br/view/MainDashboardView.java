package unaerp.br.view;

import unaerp.br.model.entity.User;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainDashboardView extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private TransactionsPanel transactionsPanel;
    private CategoriesPanel categoriesPanel;
    private SummaryPanel summaryPanel;

    public MainDashboardView(User user) {
        this.currentUser = user;

        setTitle("Gerenciador Financeiro Pessoal - Bem-vindo(a) " + currentUser.getUsername());
        setSize(1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        attachListeners();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        transactionsPanel = new TransactionsPanel(currentUser, this);
        categoriesPanel = new CategoriesPanel(currentUser, this);
        summaryPanel = new SummaryPanel(currentUser, this);

        tabbedPane.addTab("Transações", transactionsPanel);
        tabbedPane.addTab("Categorias", categoriesPanel);
        tabbedPane.addTab("Resumo", summaryPanel);
    }

    private void layoutComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem logoutMenuItem = new JMenuItem("Sair");
        JMenuItem exitMenuItem = new JMenuItem("Sair do Programa");

        fileMenu.add(logoutMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        add(tabbedPane);

        logoutMenuItem.addActionListener(e -> performLogout());
        exitMenuItem.addActionListener(e -> System.exit(0));
    }

    private void attachListeners() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                transactionsPanel.closeResources();
                categoriesPanel.closeResources();
                summaryPanel.closeResources();
                System.out.println("MainDashboardView fechada, recursos dos painéis devem ser liberados.");
            }
        });
    }

    private void performLogout() {
        transactionsPanel.closeResources();
        categoriesPanel.closeResources();
        summaryPanel.closeResources();

        this.dispose();
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }
}