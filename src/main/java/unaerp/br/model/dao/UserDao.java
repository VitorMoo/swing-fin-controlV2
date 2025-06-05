package unaerp.br.model.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import unaerp.br.model.entity.User;

import java.util.Optional;

public class UserDao {

    private final EntityManager entityManager;

    public UserDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(User user) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(user);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            throw e;
        }
    }

    public void update(User user) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(user);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            throw e;
        }
    }

    public void delete(User user) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
            throw e;
        }
    }

    public Optional<User> findById(Long id) {
        try {
            User user = entityManager.find(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
            throw e;
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :username",
                User.class
            );
            query.setParameter("username", username);
            return query.getResultList().stream().findFirst();
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário por nome: " + e.getMessage());
            throw e;
        }
    }
}