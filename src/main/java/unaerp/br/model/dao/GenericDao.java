package unaerp.br.model.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

public abstract class GenericDao<T> {

    protected final EntityManager entityManager;
    protected final Class<T> entityClass;

    public GenericDao(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao salvar entidade: " + e.getMessage());
            throw e;
        }
    }

    public void update(T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao atualizar entidade: " + e.getMessage());
            throw e;
        }
    }

    public void delete(T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao deletar entidade: " + e.getMessage());
            throw e;
        }
    }

    public void deleteById(Long id) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            T entity = entityManager.find(entityClass, id);
            if (entity != null) {
                entityManager.remove(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Erro ao deletar " + entityClass.getSimpleName() + " por ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Erro ao deletar entidade por ID: " + entityClass.getSimpleName(), e);
        }
    }

    public Optional<T> findById(Long id) {
        try {
            T entity = entityManager.find(entityClass, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            System.err.println("Erro ao buscar entidade por ID: " + e.getMessage());
            throw e;
        }
    }

    public List<T> findAll() {
        try {
            TypedQuery<T> query = entityManager.createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e",
                entityClass
            );
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar todas as entidades: " + e.getMessage());
            throw e;
        }
    }
}