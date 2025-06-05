package unaerp.br.controller;

import unaerp.br.config.HibernateUtil;
import unaerp.br.model.dao.CategoryDao;
import unaerp.br.model.dao.TransactionDao;
import unaerp.br.model.entity.Category;
import unaerp.br.model.entity.Transaction;
import unaerp.br.model.entity.User;
import unaerp.br.model.enums.TransactionType;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

public class TransactionController {

    private TransactionDao transactionDao;
    private CategoryDao categoryDao;
    private EntityManager entityManager;

    public TransactionController() {
        this.entityManager = HibernateUtil.getEntityManager();
        this.transactionDao = new TransactionDao(this.entityManager);
        this.categoryDao = new CategoryDao(this.entityManager);
    }

    public boolean addTransaction(String description, BigDecimal amount, TransactionType transactionType, LocalDate date, Category category, User currentUser) {
        if (description == null || description.trim().isEmpty() || amount == null || date == null || category == null || currentUser == null) {
            return false;
        }

        if (!category.getUser().getId().equals(currentUser.getId())) {
            return false;
        }

        if (category.getTransactionType() != transactionType) {
            return false;
        }

        Transaction newTransaction = new Transaction(description, amount, transactionType, date, category, currentUser);
        try {
            transactionDao.save(newTransaction);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao adicionar transação para o usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean updateTransaction(Long transactionId, String description, BigDecimal amount, TransactionType transactionType, LocalDate date, Category category, User currentUser) {
        if (transactionId == null || description == null || description.trim().isEmpty() || amount == null || date == null || category == null || currentUser == null) {
            return false;
        }

        Optional<Transaction> transactionOpt = transactionDao.findById(transactionId);
        if (!transactionOpt.isPresent() || !transactionOpt.get().getUser().getId().equals(currentUser.getId())) {
            return false;
        }

        if (!category.getUser().getId().equals(currentUser.getId()) || category.getTransactionType() != transactionType) {
            return false;
        }

        Transaction transaction = transactionOpt.get();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setTransactionDate(date);
        transaction.setCategory(category);
        transaction.setTransactionType(transactionType);

        try {
            transactionDao.update(transaction);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar transação ID " + transactionId + " para o usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTransaction(Long transactionId, User currentUser) {
        if (transactionId == null || currentUser == null) {
            return false;
        }

        Optional<Transaction> transactionOpt = transactionDao.findById(transactionId);
        if (!transactionOpt.isPresent() || !transactionOpt.get().getUser().getId().equals(currentUser.getId())) {
            return false;
        }

        try {
            transactionDao.delete(transactionOpt.get());
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao deletar transação ID " + transactionId + " para o usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    public List<Transaction> getTransactionsByUser(User currentUser) {
        if (currentUser == null) {
            return Collections.emptyList();
        }
        try {
            return transactionDao.findByUser(currentUser);
        } catch (Exception e) {
            System.err.println("Erro ao buscar transações para o usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Transaction> getTransactionsByUserAndDateRange(User currentUser, LocalDate startDate, LocalDate endDate) {
        if (currentUser == null || startDate == null || endDate == null) {
            return Collections.emptyList();
        }
        try {
            return transactionDao.findByUserAndDateRange(currentUser, startDate, endDate);
        } catch (Exception e) {
            System.err.println("Erro ao buscar transações por período para o usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<Transaction> getTransactionByIdAndUser(Long id, User currentUser) {
        if (id == null || currentUser == null) {
            return Optional.empty();
        }
        Optional<Transaction> transactionOpt = transactionDao.findById(id);
        if (transactionOpt.isPresent() && transactionOpt.get().getUser().getId().equals(currentUser.getId())) {
            return transactionOpt;
        }
        return Optional.empty();
    }

    public void close() {
        if (this.entityManager != null && this.entityManager.isOpen()) {
            this.entityManager.close();
        }
    }
}