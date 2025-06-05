package unaerp.br.view;

import unaerp.br.controller.CategoryController;
import unaerp.br.controller.TransactionController;
import unaerp.br.model.entity.Category;
import unaerp.br.model.entity.Transaction;
import unaerp.br.model.entity.User;
import unaerp.br.model.enums.TransactionType;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class AddEditTransactionDialog extends JDialog {
    private JTextField descriptionField;
    private JFormattedTextField amountField;
    private JComboBox<TransactionType> typeComboBox;
    private JTextField dateField;
    private JComboBox<CategoryWrapper> categoryComboBox;
    private JButton saveButton;
    private JButton cancelButton;

    private TransactionController transactionController;
    private CategoryController categoryController;
    private User currentUser;
    private Transaction transactionToEdit;
    private boolean saved = false;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AddEditTransactionDialog(Frame owner, TransactionController txController, User user, Transaction transaction) {
        super(owner, (transaction == null ? "Adicionar" : "Editar") + " Transação", true);
        this.transactionController = txController;
        this.categoryController = new CategoryController();
        this.currentUser = user;
        this.transactionToEdit = transaction;

        setSize(450, 350);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initComponents();
        layoutComponents();
        populateFields();
        attachListeners();
    }

    private void initComponents() {
        descriptionField = new JTextField(20);

        NumberFormat currencyFormat = DecimalFormat.getNumberInstance(new Locale("pt", "BR"));
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        amountField = new JFormattedTextField(currencyFormat);
        amountField.setColumns(10);
        amountField.setValue(BigDecimal.ZERO);


        typeComboBox = new JComboBox<>(TransactionType.values());
        categoryComboBox = new JComboBox<>();

        dateField = new JTextField(10);
        dateField.setToolTipText("Insira a data como AAAA-MM-DD");


        saveButton = new JButton("Salvar");
        cancelButton = new JButton("Cancelar");
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        panel.add(new JLabel("Descrição:"), gbcPosition(gbc, 0, y));
        panel.add(descriptionField, gbcPosition(gbc, 1, y++, GridBagConstraints.HORIZONTAL));

        panel.add(new JLabel("Valor:"), gbcPosition(gbc, 0, y));
        panel.add(amountField, gbcPosition(gbc, 1, y++, GridBagConstraints.HORIZONTAL));

        panel.add(new JLabel("Tipo:"), gbcPosition(gbc, 0, y));
        panel.add(typeComboBox, gbcPosition(gbc, 1, y++, GridBagConstraints.HORIZONTAL));

        panel.add(new JLabel("Data (AAAA-MM-DD):"), gbcPosition(gbc, 0, y));
        panel.add(dateField, gbcPosition(gbc, 1, y++, GridBagConstraints.HORIZONTAL));


        panel.add(new JLabel("Categoria:"), gbcPosition(gbc, 0, y));
        panel.add(categoryComboBox, gbcPosition(gbc, 1, y++, GridBagConstraints.HORIZONTAL));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private GridBagConstraints gbcPosition(GridBagConstraints gbc, int x, int y, int fill){
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.fill = fill;
        gbc.gridwidth = 1;
        return gbc;
    }
    private GridBagConstraints gbcPosition(GridBagConstraints gbc, int x, int y){
        return gbcPosition(gbc, x,y, GridBagConstraints.NONE);
    }


    private void populateFields() {
        try {
            List<Category> categories = categoryController.getCategoriesByUser(currentUser);
            if (categories.isEmpty()) {
                categoryComboBox.addItem(new CategoryWrapper(null, "Nenhuma categoria cadastrada"));
                categoryComboBox.setEnabled(false);
                saveButton.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Não há categorias cadastradas. Por favor, cadastre uma categoria primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            } else {
                for (Category cat : categories) {
                    categoryComboBox.addItem(new CategoryWrapper(cat));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar categorias: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        if (transactionToEdit != null) {
            descriptionField.setText(transactionToEdit.getDescription());
            amountField.setValue(transactionToEdit.getAmount());
            typeComboBox.setSelectedItem(transactionToEdit.getTransactionType());
            dateField.setText(transactionToEdit.getTransactionDate().format(dateFormatter));

            if (transactionToEdit.getCategory() != null) {
                for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                    CategoryWrapper wrapper = categoryComboBox.getItemAt(i);
                    if (wrapper.getCategory() != null && wrapper.getCategory().getId().equals(transactionToEdit.getCategory().getId())) {
                        categoryComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } else {
            dateField.setText(LocalDate.now().format(dateFormatter));
            if (categoryComboBox.getItemCount() > 0 && categoryComboBox.getItemAt(0).getCategory() == null) {
            } else if (categoryComboBox.getItemCount() > 0) {
                categoryComboBox.setSelectedIndex(0);
            }
        }
    }

    private void attachListeners() {
        saveButton.addActionListener(e -> performSave());
        cancelButton.addActionListener(e -> {
            if (categoryController != null) {
                categoryController.close();
            }
            dispose();
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (categoryController != null) {
                    categoryController.close();
                }
            }
        });
    }

    private void performSave() {
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A descrição não pode estar vazia.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            Object value = amountField.getValue();
            if (value instanceof BigDecimal) {
                amount = (BigDecimal) value;
            } else if (value instanceof Number) {
                amount = new BigDecimal(((Number)value).toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else if (value instanceof String) {
                NumberFormat nf = DecimalFormat.getNumberInstance(new Locale("pt", "BR"));
                amount = new BigDecimal(nf.parse((String)value).toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            else {
                amount = new BigDecimal(amountField.getText().replace(".", "").replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_UP);
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "O valor deve ser maior que zero.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de valor inválido. Use o formato numérico correto para sua localidade (Ex: 1.234,56).", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return;
        }


        TransactionType type = (TransactionType) typeComboBox.getSelectedItem();

        CategoryWrapper selectedCategoryWrapper = (CategoryWrapper) categoryComboBox.getSelectedItem();
        if (selectedCategoryWrapper == null || selectedCategoryWrapper.getCategory() == null) {
            JOptionPane.showMessageDialog(this, "A categoria não pode estar vazia. Por favor, cadastre ou selecione uma categoria válida.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Category selectedCategory = selectedCategoryWrapper.getCategory();


        LocalDate transactionDate;
        try {
            transactionDate = LocalDate.parse(dateField.getText(), dateFormatter);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use AAAA-MM-DD.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success;
        try {
            if (transactionToEdit == null) {
                success = transactionController.addTransaction(description, amount, type, transactionDate, selectedCategory, currentUser);
            } else {
                transactionToEdit.setTransactionType(type);
                success = transactionController.updateTransaction(transactionToEdit.getId(), description, amount, transactionDate, selectedCategory, currentUser);
            }

            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this, "Transação salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                if (categoryController != null) {
                    categoryController.close();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao salvar transação.", "Falha ao Salvar", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar transação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private static class CategoryWrapper {
        private Category category;
        private String displayName;

        public CategoryWrapper(Category category) {
            this.category = category;
            this.displayName = (category != null) ? category.getName() : "Erro - Categoria Nula";
        }

        public CategoryWrapper(Category category, String displayName) {
            this.category = category;
            this.displayName = displayName;
        }

        public Category getCategory() {
            return category;
        }
        @Override
        public String toString() {
            return displayName;
        }
    }
}