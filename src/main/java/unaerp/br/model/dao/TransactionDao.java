package unaerp.br.model.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import unaerp.br.model.entity.Transaction;
import unaerp.br.model.entity.User;
import unaerp.br.model.entity.Category;
import unaerp.br.model.enums.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionDao {

    private final EntityManager entityManager;

    public TransactionDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(Transaction transaction) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(transaction);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao salvar transação: " + e.getMessage());
            throw e;
        }
    }

    public void update(Transaction transaction) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(transaction);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao atualizar transação: " + e.getMessage());
            throw e;
        }
    }

    public void delete(Transaction transaction) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(entityManager.contains(transaction) ? transaction : entityManager.merge(transaction));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao deletar transação: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Transaction> findById(Long id) {
        try {
            Transaction transaction = entityManager.find(Transaction.class, id);
            return Optional.ofNullable(transaction);
        } catch (Exception e) {
            System.err.println("Erro ao buscar transação por ID: " + e.getMessage());
            throw e;
        }
    }

    public List<Transaction> findByUser(User user) {
        try {
            TypedQuery<Transaction> query = entityManager.createQuery(
                    "SELECT t FROM Transaction t WHERE t.user = :user ORDER BY t.transactionDate DESC",
                    Transaction.class
            );
            query.setParameter("user", user);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar transações do usuário: " + e.getMessage());
            throw e;
        }
    }

    public List<Transaction> findByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate) {
        try {
            TypedQuery<Transaction> query = entityManager.createQuery(
                    "SELECT t FROM Transaction t WHERE t.user = :user AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC",
                    Transaction.class
            );
            query.setParameter("user", user);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar transações por período: " + e.getMessage());
            throw e;
        }
    }

    public List<Transaction> findByUserAndCategory(User user, Category category) {
        try {
            TypedQuery<Transaction> query = entityManager.createQuery(
                    "SELECT t FROM Transaction t WHERE t.user = :user AND t.category = :category ORDER BY t.transactionDate DESC",
                    Transaction.class
            );
            query.setParameter("user", user);
            query.setParameter("category", category);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar transações por categoria: " + e.getMessage());
            throw e;
        }
    }
}