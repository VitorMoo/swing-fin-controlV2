package unaerp.br.controller;

import unaerp.br.config.HibernateUtil;
import unaerp.br.model.dao.UserDao;
import unaerp.br.model.entity.User;
import unaerp.br.util.PasswordHashingUtil;
import jakarta.persistence.EntityManager;
import java.util.Optional;

public class UserController {

    private UserDao userDao;
    private EntityManager entityManager;

    public UserController() {
        this.entityManager = HibernateUtil.getEntityManager();
        this.userDao = new UserDao(this.entityManager);
    }

    public boolean registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        if (userDao.findByUsername(username).isPresent()) {
            return false;
        }

        String hashedPassword = PasswordHashingUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword);
        try {
            userDao.save(newUser);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao registrar usuário '" + username + "': " + e.getMessage());
            return false;
        }
    }

    public Optional<User> login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordHashingUtil.verifyPassword(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public void close() {
        if (this.entityManager != null && this.entityManager.isOpen()) {
            this.entityManager.close();
        }
    }
}