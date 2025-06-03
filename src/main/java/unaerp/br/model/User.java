package unaerp.br.model;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

class User {
    private Long id;
    private String username;
    private String passwordHash;
    private List<Category> categories;
    private List<Transaction> transactions;

    public User(String username, String password) {
        this.username = username;
        this.passwordHash = password;
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean checkPassword(String password) {
        return this.passwordHash.equals(password);
    }

    public void setPassword(String password) {
        this.passwordHash = password;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
        category.setUser(this);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setUser(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + username + '\'' +
                '}';
    }
}