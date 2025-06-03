package unaerp.br.config;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;

public class DatabaseSetup {

    public void executeInitialSetup(EntityManager entityManager) {
        if (entityManager == null) {
            System.err.println("EntityManager nulo fornecido para DatabaseSetup. Abortando setup.");
            return;
        }

        try {
            Session session = entityManager.unwrap(Session.class);
            if (session == null) {
                System.err.println("Não foi possível obter a Sessão Hibernate do EntityManager.");
                return;
            }
            
        } catch (Exception e) {
            System.err.println("Erro durante o setup do banco de dados: " + e.getMessage());
            throw new RuntimeException("Erro durante o setup do banco de dados", e);
        }
    }
}