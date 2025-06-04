package unaerp.br.model.dto;

import java.math.BigDecimal;

public class FinancialSummary {
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
    private final BigDecimal balance;

    public FinancialSummary(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "FinancialSummary{" +
                "totalIncome=" + totalIncome +
                ", totalExpense=" + totalExpense +
                ", balance=" + balance +
                '}';
    }
}