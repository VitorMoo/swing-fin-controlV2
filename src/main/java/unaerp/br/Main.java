package unaerp.br;

import unaerp.br.config.HibernateUtil;
import unaerp.br.controller.CategoryController;
import unaerp.br.controller.TransactionController;
import unaerp.br.controller.UserController;
import unaerp.br.model.entity.Category;
import unaerp.br.model.entity.User;
import unaerp.br.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        UserController userController = null;
        CategoryController categoryController = null;
        TransactionController transactionController = null;

        try {
            System.out.println("Iniciando teste de persistência...");

            userController = new UserController();
            categoryController = new CategoryController();
            transactionController = new TransactionController();

            // 1. Registrar usuário de teste
            String testUsername = "testuser";
            String testPassword = "password123";
            System.out.println("Registrando usuário: " + testUsername);
            boolean registered = userController.registerUser(testUsername, testPassword);

            if (registered) {
                System.out.println("Usuário registrado com sucesso.");
            } else {
                // Tentar fazer login se já existir
                System.out.println("Usuário já existe ou falha no registro. Tentando login...");
            }

            // 2. Fazer login com o usuário de teste
            Optional<User> userOpt = userController.login(testUsername, testPassword);
            User currentUser = null;

            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                System.out.println("Login bem-sucedido para o usuário: " + currentUser.getUsername());

                // 3. Adicionar categorias para o usuário
                System.out.println("Adicionando categorias...");
                categoryController.addCategory("Salário", currentUser);
                categoryController.addCategory("Alimentação", currentUser);
                categoryController.addCategory("Transporte", currentUser);
                categoryController.addCategory("Lazer", currentUser);
                System.out.println("Categorias adicionadas.");

                // Obter categorias para usar nas transações
                List<Category> categories = categoryController.getCategoriesByUser(currentUser);
                Category salarioCat = categories.stream()
                                                .filter(c -> c.getName().equals("Salário"))
                                                .findFirst().orElse(null);
                Category alimentacaoCat = categories.stream()
                                                  .filter(c -> c.getName().equals("Alimentação"))
                                                  .findFirst().orElse(null);
                 Category transporteCat = categories.stream()
                                                  .filter(c -> c.getName().equals("Transporte"))
                                                  .findFirst().orElse(null);


                // 4. Adicionar transações de teste
                System.out.println("Adicionando transações...");
                if (salarioCat != null) {
                     transactionController.addTransaction(
                         "Salário Mensal", new BigDecimal("6000.00"), TransactionType.INCOME, LocalDate.now(), salarioCat, currentUser
                     );
                }
                 if (alimentacaoCat != null) {
                     transactionController.addTransaction(
                         "Compras Supermercado", new BigDecimal("350.00"), TransactionType.EXPENSE, LocalDate.now(), alimentacaoCat, currentUser
                     );
                 }
                 if (transporteCat != null) {
                      transactionController.addTransaction(
                          "Uber", new BigDecimal("45.50"), TransactionType.EXPENSE, LocalDate.now(), transporteCat, currentUser
                      );
                 }
                System.out.println("Transações adicionadas.");

            } else {
                System.err.println("Falha no login. Não foi possível adicionar categorias e transações.");
            }


        } catch (Exception e) {
            System.err.println("Ocorreu um erro durante o teste de persistência: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Fechar recursos
            if (userController != null) userController.close();
            if (categoryController != null) categoryController.close();
            if (transactionController != null) transactionController.close();
            HibernateUtil.close();
            System.out.println("Teste de persistência concluído. Recursos fechados.");
        }
    }
}