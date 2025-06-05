package unaerp.br.controller;

import unaerp.br.config.HibernateUtil;
import unaerp.br.model.dao.TransactionDao;
import unaerp.br.model.dto.FinancialSummary;
import unaerp.br.model.entity.Transaction;
import unaerp.br.model.entity.User;
import unaerp.br.model.enums.TransactionType;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FinancialSummaryController {

    private TransactionDao transactionDao;
    private EntityManager entityManager;

    public FinancialSummaryController() {
        this.entityManager = HibernateUtil.getEntityManager();
        this.transactionDao = new TransactionDao(this.entityManager);
        System.out.println("FinancialSummaryController instanciado.");
    }

    public FinancialSummary getFinancialSummary(User currentUser, LocalDate startDate, LocalDate endDate) {
        if (currentUser == null || startDate == null || endDate == null) {
            System.out.println("Não é possível gerar resumo financeiro: dados de entrada inválidos (usuário, data inicial ou data final).");
            return new FinancialSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        List<Transaction> transactions;
        try {
            transactions = transactionDao.findByUserAndDateRange(currentUser, startDate, endDate);
            System.out.println(transactions.size() + " transações encontradas para o resumo financeiro do usuário " + currentUser.getUsername() + " no período de " + startDate + " a " + endDate);
        } catch (Exception e) {
            System.err.println("Erro ao buscar transações para o resumo financeiro do usuário " + currentUser.getUsername() + ": " + e.getMessage());
            return new FinancialSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        BigDecimal balance = totalIncome.subtract(totalExpense);
        System.out.println("Resumo financeiro calculado para o usuário " + currentUser.getUsername() +
                " no período de " + startDate + " a " + endDate +
                ": Receitas=" + totalIncome + ", Despesas=" + totalExpense + ", Saldo=" + balance);

        return new FinancialSummary(totalIncome, totalExpense, balance);
    }

    public void close() {
        if (this.entityManager != null && this.entityManager.isOpen()) {
            this.entityManager.close();
            System.out.println("EntityManager fechado pelo FinancialSummaryController.");
        }
    }
}