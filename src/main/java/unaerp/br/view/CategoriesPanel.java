package unaerp.br.view;

import unaerp.br.controller.CategoryController;
import unaerp.br.model.entity.Category;
import unaerp.br.model.entity.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CategoriesPanel extends JPanel {
    private User currentUser;
    private JFrame ownerFrame;

    private JList<CategoryWrapper> categoryList;
    private DefaultListModel<CategoryWrapper> listModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    private CategoryController categoryController;

    public CategoriesPanel(User user, JFrame owner) {
        this.currentUser = user;
        this.ownerFrame = owner;
        this.categoryController = new CategoryController();

        setLayout(new BorderLayout(10, 10));
        initComponents();
        layoutComponents();
        attachListeners();

        loadCategories();
    }

    private void initComponents() {
        listModel = new DefaultListModel<>();
        categoryList = new JList<>(listModel);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addButton = new JButton("Adicionar Categoria");
        editButton = new JButton("Editar Selecionada");
        deleteButton = new JButton("Excluir Selecionada");

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void layoutComponents() {
        JScrollPane scrollPane = new JScrollPane(categoryList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void attachListeners() {
        addButton.addActionListener(e -> openAddEditCategoryDialog(null));

        editButton.addActionListener(e -> {
            CategoryWrapper selectedWrapper = categoryList.getSelectedValue();
            if (selectedWrapper != null) {
                openAddEditCategoryDialog(selectedWrapper.getCategory());
            } else {
                JOptionPane.showMessageDialog(ownerFrame, "Por favor, selecione uma categoria para editar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> deleteSelectedCategory());

        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean isSelected = categoryList.getSelectedIndex() != -1;
                editButton.setEnabled(isSelected);
                deleteButton.setEnabled(isSelected);
            }
        });
    }

    public void loadCategories() {
        listModel.clear();
        try {
            List<Category> categories = categoryController.getCategoriesByUser(currentUser);
            for (Category cat : categories) {
                listModel.addElement(new CategoryWrapper(cat));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ownerFrame, "Erro ao carregar categorias: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openAddEditCategoryDialog(Category categoryToEdit) {
        AddEditCategoryDialog dialog = new AddEditCategoryDialog(ownerFrame, categoryController, currentUser, categoryToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadCategories();
        }
    }

    private void deleteSelectedCategory() {
        CategoryWrapper selectedWrapper = categoryList.getSelectedValue();
        if (selectedWrapper == null) {
            JOptionPane.showMessageDialog(ownerFrame, "Por favor, selecione uma categoria para excluir.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Category categoryToDelete = selectedWrapper.getCategory();
        int confirmation = JOptionPane.showConfirmDialog(ownerFrame,
                "Tem certeza que deseja excluir a categoria '" + categoryToDelete.getName() + "'?\nEsta ação não pode ser desfeita.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                boolean success = categoryController.deleteCategory(categoryToDelete.getId(), currentUser);
                if (success) {
                    JOptionPane.showMessageDialog(ownerFrame, "Categoria excluída com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    loadCategories();
                } else {
                    JOptionPane.showMessageDialog(ownerFrame, "Não foi possível excluir a categoria. Ela pode estar em uso ou ocorreu um erro.", "Falha na Exclusão", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ownerFrame, "Erro ao excluir categoria: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public void closeResources() {
        if (categoryController != null) {
            categoryController.close();
            System.out.println("CategoriesPanel fechado, recursos do CategoryController liberados.");
        }
    }

    private static class CategoryWrapper {
        private Category category;
        public CategoryWrapper(Category category) { this.category = category; }
        public Category getCategory() { return category; }
        @Override public String toString() { return category.getName(); }
    }
}