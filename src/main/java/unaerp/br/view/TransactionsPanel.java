package unaerp.br.view;

import unaerp.br.controller.CategoryController;
import unaerp.br.controller.FinancialSummaryController;
import unaerp.br.controller.TransactionController;
import unaerp.br.model.dto.FinancialSummary;
import unaerp.br.model.entity.Category;
import unaerp.br.model.entity.Transaction;
import unaerp.br.model.entity.User;
import unaerp.br.model.enums.TransactionType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransactionsPanel extends JPanel {
    private User currentUser;
    private JFrame ownerFrame;

    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<CategoryWrapper> categoryFilterComboBox;
    private JComboBox<TransactionTypeWrapper> typeFilterComboBox;
    private JButton applyFiltersButton;
    private JButton resetFiltersButton;

    private TransactionController transactionController;
    private CategoryController categoryController;
    private FinancialSummaryController summaryController;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final CategoryWrapper ALL_CATEGORIES_OPTION = new CategoryWrapper(null, "-- Todas as Categorias --");
    private final TransactionTypeWrapper ALL_TYPES_OPTION = new TransactionTypeWrapper(null, "-- Todos os Tipos --");

    private JLabel totalIncomeLabel;
    private JLabel totalExpenseLabel;
    private JLabel balanceLabel;
    private JPanel summaryDisplayPanel;


    public TransactionsPanel(User user, JFrame owner) {
        this.currentUser = user;
        this.ownerFrame = owner;
        this.transactionController = new TransactionController();
        this.categoryController = new CategoryController();
        this.summaryController = new FinancialSummaryController();

        setLayout(new BorderLayout(10, 10));
        initComponents();
        createFilterPanel();
        createSummaryDisplayPanel();
        layoutComponents();
        attachListeners();

        setDefaultDatesForFilters();
        populateCategoryFilter();
        populateTypeFilter();
        loadTransactionsAndSummary();
    }

    private void initComponents() {
        String[] columnNames = {"ID", "Descrição", "Valor", "Tipo", "Data", "Categoria"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumnModel columnModel = transactionTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(30);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(80);
        columnModel.getColumn(3).setPreferredWidth(70);
        columnModel.getColumn(4).setPreferredWidth(80);
        columnModel.getColumn(5).setPreferredWidth(100);

        addButton = new JButton("Adicionar Transação");
        editButton = new JButton("Editar Selecionada");
        deleteButton = new JButton("Excluir Selecionada");

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        startDateField = new JTextField(10);
        startDateField.setToolTipText("AAAA-MM-DD");
        endDateField = new JTextField(10);
        endDateField.setToolTipText("AAAA-MM-DD");
        categoryFilterComboBox = new JComboBox<>();
        typeFilterComboBox = new JComboBox<>();
        applyFiltersButton = new JButton("Aplicar Filtros");
        resetFiltersButton = new JButton("Limpar Filtros");

        Font summaryFont = new Font("Arial", Font.BOLD, 14);
        totalIncomeLabel = new JLabel("Receitas: R$ 0,00");
        totalIncomeLabel.setFont(summaryFont);
        totalExpenseLabel = new JLabel("Despesas: R$ 0,00");
        totalExpenseLabel.setFont(summaryFont);
        balanceLabel = new JLabel("Saldo: R$ 0,00");
        balanceLabel.setFont(summaryFont);
    }

    private void setDefaultDatesForFilters() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        startDateField.setText(firstDayOfMonth.format(dateFormatter));
        endDateField.setText(today.format(dateFormatter));
    }

    private void createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros e Período do Resumo"));

        filterPanel.add(new JLabel("Data Inicial:"));
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("Data Final:"));
        filterPanel.add(endDateField);
        filterPanel.add(new JLabel("Categoria:"));
        filterPanel.add(categoryFilterComboBox);
        filterPanel.add(new JLabel("Tipo:"));
        filterPanel.add(typeFilterComboBox);
        filterPanel.add(applyFiltersButton);
        filterPanel.add(resetFiltersButton);

        add(filterPanel, BorderLayout.NORTH);
    }

    private void createSummaryDisplayPanel() {
        summaryDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        summaryDisplayPanel.setBorder(BorderFactory.createTitledBorder("Resumo do Período Selecionado"));
        summaryDisplayPanel.add(totalIncomeLabel);
        summaryDisplayPanel.add(totalExpenseLabel);
        summaryDisplayPanel.add(balanceLabel);
    }

    private void populateCategoryFilter() {
        categoryFilterComboBox.addItem(ALL_CATEGORIES_OPTION);
        try {
            List<Category> categories = categoryController.getCategoriesByUser(currentUser);
            for (Category cat : categories) {
                categoryFilterComboBox.addItem(new CategoryWrapper(cat, cat.getName()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ownerFrame, "Erro ao carregar categorias para filtro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTypeFilter() {
        typeFilterComboBox.addItem(ALL_TYPES_OPTION);
        for (TransactionType type : TransactionType.values()) {
            typeFilterComboBox.addItem(new TransactionTypeWrapper(type, type.getDisplayName()));
        }
    }

    private void layoutComponents() {
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel southOuterPanel = new JPanel(new BorderLayout(5,5));
        southOuterPanel.add(summaryDisplayPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        southOuterPanel.add(buttonPanel, BorderLayout.CENTER);

        add(southOuterPanel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
    }

    private void attachListeners() {
        addButton.addActionListener(e -> openAddEditTransactionDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long transactionId = (Long) tableModel.getValueAt(selectedRow, 0);
                Optional<Transaction> transactionOpt = transactionController.getTransactionByIdAndUser(transactionId, currentUser);
                transactionOpt.ifPresent(this::openAddEditTransactionDialog);
                if (!transactionOpt.isPresent()){
                    JOptionPane.showMessageDialog(ownerFrame, "Não foi possível recuperar a transação selecionada para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(ownerFrame, "Por favor, selecione uma transação para editar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> deleteSelectedTransaction());
        transactionTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                boolean isSelected = transactionTable.getSelectedRow() != -1;
                editButton.setEnabled(isSelected);
                deleteButton.setEnabled(isSelected);
            }
        });

        applyFiltersButton.addActionListener(e -> loadTransactionsAndSummary());
        resetFiltersButton.addActionListener(e -> {
            setDefaultDatesForFilters();
            categoryFilterComboBox.setSelectedItem(ALL_CATEGORIES_OPTION);
            typeFilterComboBox.setSelectedItem(ALL_TYPES_OPTION);
            loadTransactionsAndSummary();
        });
    }

    public void loadTransactionsAndSummary() {
        loadTransactions();
        updateSummaryDisplay();
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> transactions;

        LocalDate startDate = null;
        LocalDate endDate = null;
        Category selectedCategory = null;
        TransactionType selectedType = null;

        try {
            if (!startDateField.getText().trim().isEmpty()) {
                startDate = LocalDate.parse(startDateField.getText().trim(), dateFormatter);
            }
            if (!endDateField.getText().trim().isEmpty()) {
                endDate = LocalDate.parse(endDateField.getText().trim(), dateFormatter);
            }
            if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(ownerFrame, "Data final não pode ser anterior à data inicial.", "Erro de Filtro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(ownerFrame, "Formato de data inválido. Use AAAA-MM-DD.", "Erro de Filtro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CategoryWrapper categoryWrapper = (CategoryWrapper) categoryFilterComboBox.getSelectedItem();
        if (categoryWrapper != null && categoryWrapper.getCategory() != null) {
            selectedCategory = categoryWrapper.getCategory();
        }

        TransactionTypeWrapper typeWrapper = (TransactionTypeWrapper) typeFilterComboBox.getSelectedItem();
        if (typeWrapper != null && typeWrapper.getType() != null) {
            selectedType = typeWrapper.getType();
        }

        try {
            if (startDate != null && endDate != null) {
                transactions = transactionController.getTransactionsByUserAndDateRange(currentUser, startDate, endDate);
            } else if (startDate != null) {
                LocalDate finalStartDate = startDate;
                transactions = transactionController.getTransactionsByUser(currentUser).stream()
                        .filter(t -> !t.getTransactionDate().isBefore(finalStartDate))
                        .collect(Collectors.toList());
            } else if (endDate != null) {
                LocalDate finalEndDate = endDate;
                transactions = transactionController.getTransactionsByUser(currentUser).stream()
                        .filter(t -> !t.getTransactionDate().isAfter(finalEndDate))
                        .collect(Collectors.toList());
            } else {
                transactions = transactionController.getTransactionsByUser(currentUser);
            }

            if (selectedCategory != null) {
                final Category filterCat = selectedCategory;
                transactions = transactions.stream()
                        .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(filterCat.getId()))
                        .collect(Collectors.toList());
            }

            if (selectedType != null) {
                final TransactionType filterType = selectedType;
                transactions = transactions.stream()
                        .filter(t -> t.getTransactionType() == filterType)
                        .collect(Collectors.toList());
            }

            for (Transaction tx : transactions) {
                tableModel.addRow(new Object[]{
                        tx.getId(),
                        tx.getDescription(),
                        tx.getAmount(),
                        tx.getTransactionType().getDisplayName(),
                        tx.getTransactionDate().format(dateFormatter),
                        tx.getCategory() != null ? tx.getCategory().getName() : "N/A"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ownerFrame, "Erro ao carregar transações: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateSummaryDisplay() {
        LocalDate startDate = null;
        LocalDate endDate = null;

        try {
            if (!startDateField.getText().trim().isEmpty()) {
                startDate = LocalDate.parse(startDateField.getText().trim(), dateFormatter);
            }
            if (!endDateField.getText().trim().isEmpty()) {
                endDate = LocalDate.parse(endDateField.getText().trim(), dateFormatter);
            }

            if (startDate == null || endDate == null) {
                totalIncomeLabel.setText("Receitas: " + currencyFormatter.format(0));
                totalExpenseLabel.setText("Despesas: " + currencyFormatter.format(0));
                balanceLabel.setText("Saldo: " + currencyFormatter.format(0));
                balanceLabel.setForeground(Color.BLACK);
                return;
            }

            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(ownerFrame, "Data final do resumo não pode ser anterior à data inicial.", "Erro de Data do Resumo", JOptionPane.ERROR_MESSAGE);
                return;
            }

            FinancialSummary summary = summaryController.getFinancialSummary(currentUser, startDate, endDate);
            totalIncomeLabel.setText("Receitas: " + currencyFormatter.format(summary.getTotalIncome()));
            totalExpenseLabel.setText("Despesas: " + currencyFormatter.format(summary.getTotalExpense()));
            balanceLabel.setText("Saldo: " + currencyFormatter.format(summary.getBalance()));

            if (summary.getBalance().compareTo(java.math.BigDecimal.ZERO) < 0) {
                balanceLabel.setForeground(Color.RED);
            } else {
                balanceLabel.setForeground(new Color(0, 100, 0));
            }

        } catch (DateTimeParseException ex) {
            totalIncomeLabel.setText("Receitas: (data inválida)");
            totalExpenseLabel.setText("Despesas: (data inválida)");
            balanceLabel.setText("Saldo: (data inválida)");
            balanceLabel.setForeground(Color.ORANGE);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(ownerFrame, "Erro ao gerar resumo: " + e.getMessage(), "Erro no Resumo", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            totalIncomeLabel.setText("Receitas: Erro");
            totalExpenseLabel.setText("Despesas: Erro");
            balanceLabel.setText("Saldo: Erro");
            balanceLabel.setForeground(Color.RED);
        }
    }


    private void openAddEditTransactionDialog(Transaction transactionToEdit) {
        AddEditTransactionDialog dialog = new AddEditTransactionDialog(ownerFrame, transactionController, currentUser, transactionToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadTransactionsAndSummary();
        }
    }

    private void deleteSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(ownerFrame, "Por favor, selecione uma transação para excluir.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long transactionId = (Long) tableModel.getValueAt(selectedRow, 0);
        String transactionDesc = (String) tableModel.getValueAt(selectedRow, 1);
        int confirmation = JOptionPane.showConfirmDialog(ownerFrame,
                "Tem certeza que deseja excluir a transação '" + transactionDesc + "' (ID: " + transactionId + ")?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                boolean success = transactionController.deleteTransaction(transactionId, currentUser);
                if (success) {
                    JOptionPane.showMessageDialog(ownerFrame, "Transação excluída com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    loadTransactionsAndSummary();
                } else {
                    JOptionPane.showMessageDialog(ownerFrame, "Não foi possível excluir a transação.", "Falha na Exclusão", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ownerFrame, "Erro ao excluir transação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public void closeResources() {
        if (transactionController != null) {
            transactionController.close();
            System.out.println("TransactionsPanel fechado, recursos do TransactionController liberados.");
        }
        if (categoryController != null) {
            categoryController.close();
            System.out.println("TransactionsPanel fechado, recursos do CategoryController (para filtro) liberados.");
        }
        if (summaryController != null) {
            summaryController.close();
            System.out.println("TransactionsPanel fechado, recursos do FinancialSummaryController liberados.");
        }
    }

    private static class CategoryWrapper {
        private Category category;
        private String displayName;
        public CategoryWrapper(Category category, String displayName) {
            this.category = category;
            this.displayName = displayName;
        }
        public Category getCategory() { return category; }
        @Override public String toString() { return displayName; }
    }

    private static class TransactionTypeWrapper {
        private TransactionType type;
        private String displayName;
        public TransactionTypeWrapper(TransactionType type, String displayName) {
            this.type = type;
            this.displayName = displayName;
        }
        public TransactionType getType() { return type; }
        @Override public String toString() { return displayName; }
    }
}