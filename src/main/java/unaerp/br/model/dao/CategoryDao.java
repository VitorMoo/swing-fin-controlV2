package unaerp.br.model.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import unaerp.br.model.entity.Category;
import unaerp.br.model.entity.User;
import unaerp.br.model.enums.TransactionType;

import java.util.List;
import java.util.Optional;

public class CategoryDao {

    private final EntityManager entityManager;

    public CategoryDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(Category category) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(category);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao salvar categoria: " + e.getMessage());
            throw e;
        }
    }

    public void update(Category category) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(category);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao atualizar categoria: " + e.getMessage());
            throw e;
        }
    }

    public void delete(Category category) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(entityManager.contains(category) ? category : entityManager.merge(category));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao deletar categoria: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Category> findById(Long id) {
        try {
            Category category = entityManager.find(Category.class, id);
            return Optional.ofNullable(category);
        } catch (Exception e) {
            System.err.println("Erro ao buscar categoria por ID: " + e.getMessage());
            throw e;
        }
    }

    public List<Category> findByUser(User user) {
        try {
            TypedQuery<Category> query = entityManager.createQuery(
                    "SELECT c FROM Category c WHERE c.user = :user ORDER BY c.name",
                    Category.class
            );
            query.setParameter("user", user);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar categorias do usuário: " + e.getMessage());
            throw e;
        }
    }

    public List<Category> findByUserAndType(User user, TransactionType type) {
        try {
            TypedQuery<Category> query = entityManager.createQuery(
                    "SELECT c FROM Category c WHERE c.user = :user AND c.transactionType = :type ORDER BY c.name",
                    Category.class
            );
            query.setParameter("user", user);
            query.setParameter("type", type);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar categorias do usuário por tipo: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Category> findByNameAndUser(String name, User user) {
        try {
            TypedQuery<Category> query = entityManager.createQuery(
                    "SELECT c FROM Category c WHERE c.name = :name AND c.user = :user",
                    Category.class
            );
            query.setParameter("name", name);
            query.setParameter("user", user);
            return query.getResultList().stream().findFirst();
        } catch (Exception e) {
            System.err.println("Erro ao buscar categoria por nome e usuário: " + e.getMessage());
            throw e;
        }
    }
}