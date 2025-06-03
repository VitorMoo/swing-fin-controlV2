package unaerp.br.model;

import java.util.Objects;
import java.time.LocalDate;
import java.math.BigDecimal;

class Transaction {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate date;
    private String description;
    private Category category;
    private User user;

    public Transaction(BigDecimal amount, TransactionType type, LocalDate date, String description, Category category, User user) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transação deve ser positivo.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Tipo da transação deve ser especificado.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Data da transação deve ser especificada.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Categoria da transação deve ser especificada.");
        }
        if (user == null) {
            throw new IllegalArgumentException("Usuário da transação deve ser especificado.");
        }

        this.amount = amount;
        this.type = type;
        this.date = date;
        this.description = description;
        this.category = category;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transação deve ser positivo.");
        }
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo da transação deve ser especificado.");
        }
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Data da transação deve ser especificada.");
        }
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Categoria da transação deve ser especificada.");
        }
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Usuário da transação deve ser especificado.");
        }
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", valor=" + amount +
                ", tipo=" + type +
                ", data=" + date +
                ", descricao='" + description + '\'' +
                ", categoria=" + (category != null ? category.getName() : "") +
                ", usuario=" + (user != null ? user.getUsername() : "") +
                '}';
    }
}