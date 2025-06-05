package unaerp.br.view;

import unaerp.br.controller.CategoryController;
import unaerp.br.model.entity.Category;
import unaerp.br.model.entity.User;
import unaerp.br.model.enums.TransactionType;

import javax.swing.*;
import java.awt.*;

public class AddEditCategoryDialog extends JDialog {
    private JTextField nameField;
    private JComboBox<TransactionType> typeComboBox;
    private JButton saveButton;
    private JButton cancelButton;

    private CategoryController categoryController;
    private User currentUser;
    private Category categoryToEdit;
    private boolean saved = false;

    public AddEditCategoryDialog(Frame owner, CategoryController controller, User user, Category category) {
        super(owner, (category == null ? "Adicionar" : "Editar") + " Categoria", true);
        this.categoryController = controller;
        this.currentUser = user;
        this.categoryToEdit = category;

        setSize(400, 200);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initComponents();
        layoutComponents();
        attachListeners();

        if (categoryToEdit != null) {
            nameField.setText(categoryToEdit.getName());
            typeComboBox.setSelectedItem(categoryToEdit.getTransactionType());
        }
    }

    private void initComponents() {
        nameField = new JTextField(20);
        typeComboBox = new JComboBox<>(TransactionType.values());
        saveButton = new JButton("Salvar");
        cancelButton = new JButton("Cancelar");
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nome da Categoria:"), gbc);

        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Tipo de Transação:"), gbc);

        gbc.gridx = 1;
        panel.add(typeComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void attachListeners() {
        saveButton.addActionListener(e -> performSave());
        cancelButton.addActionListener(e -> dispose());
    }

    private void performSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome da categoria nao pode estar vazio.", "Erro de validacao", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TransactionType selectedType = (TransactionType) typeComboBox.getSelectedItem();

        boolean success;
        try {
            if (categoryToEdit == null) {
                success = categoryController.addCategory(name, selectedType, currentUser);
            } else {
                success = categoryController.updateCategory(categoryToEdit.getId(), name, selectedType, currentUser);
            }

            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this, "Categoria salva!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar. Verifique se já não existe uma categoria com este nome.", "Salvar falhou", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar a categoria: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }
}