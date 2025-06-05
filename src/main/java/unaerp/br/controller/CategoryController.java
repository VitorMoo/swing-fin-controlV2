package unaerp.br.controller;

import unaerp.br.config.HibernateUtil;
import unaerp.br.model.dao.CategoryDao;
import unaerp.br.model.entity.Category;
import unaerp.br.model.entity.User;
import unaerp.br.model.enums.TransactionType;
import jakarta.persistence.EntityManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CategoryController {

    private CategoryDao categoryDao;
    private EntityManager entityManager;

    public CategoryController() {
        this.entityManager = HibernateUtil.getEntityManager();
        this.categoryDao = new CategoryDao(this.entityManager);
    }

    public boolean addCategory(String name, TransactionType transactionType, User currentUser) {
        if (name == null || name.trim().isEmpty() || transactionType == null || currentUser == null) {
            return false;
        }

        if (categoryDao.findByNameAndUser(name, currentUser).isPresent()) {
            return false;
        }

        Category newCategory = new Category(name, transactionType, currentUser);
        try {
            categoryDao.save(newCategory);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao adicionar categoria '" + name + "' para o usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean updateCategory(Long categoryId, String newName, TransactionType transactionType, User currentUser) {
        if (categoryId == null || newName == null || newName.trim().isEmpty() || transactionType == null || currentUser == null) {
            return false;
        }

        Optional<Category> categoryOpt = categoryDao.findById(categoryId);
        if (!categoryOpt.isPresent() || !categoryOpt.get().getUser().getId().equals(currentUser.getId())) {
            return false;
        }

        Category category = categoryOpt.get();

        if (!category.getName().equals(newName) && categoryDao.findByNameAndUser(newName, currentUser).isPresent()) {
            return false;
        }

        category.setName(newName);
        category.setTransactionType(transactionType);
        try {
            categoryDao.update(category);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar categoria ID " + categoryId + " para o usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCategory(Long categoryId, User currentUser) {
        if (categoryId == null || currentUser == null) {
            return false;
        }

        Optional<Category> categoryOpt = categoryDao.findById(categoryId);
        if (!categoryOpt.isPresent() || !categoryOpt.get().getUser().getId().equals(currentUser.getId())) {
            return false;
        }

        Category category = categoryOpt.get();
        if (!category.getTransactions().isEmpty()) {
            return false;
        }

        try {
            categoryDao.delete(category);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao deletar categoria ID " + categoryId + " para o usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    public List<Category> getCategoriesByUser(User currentUser) {
        if (currentUser == null) {
            return Collections.emptyList();
        }
        try {
            return categoryDao.findByUser(currentUser);
        } catch (Exception e) {
            System.err.println("Erro ao buscar categorias para o usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Category> getCategoriesByUserAndType(User currentUser, TransactionType type) {
        if (currentUser == null || type == null) {
            return Collections.emptyList();
        }
        try {
            return categoryDao.findByUserAndType(currentUser, type);
        } catch (Exception e) {
            System.err.println("Erro ao buscar categorias por tipo para o usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<Category> getCategoryByIdAndUser(Long id, User currentUser) {
        if (id == null || currentUser == null) {
            return Optional.empty();
        }
        Optional<Category> categoryOpt = categoryDao.findById(id);
        if (categoryOpt.isPresent() && categoryOpt.get().getUser().getId().equals(currentUser.getId())) {
            return categoryOpt;
        }
        return Optional.empty();
    }

    public void close() {
        if (this.entityManager != null && this.entityManager.isOpen()) {
            this.entityManager.close();
        }
    }
}