package unaerp.br.model;

import java.util.Objects;

class Category {
    private Long id;
    private String name;
    private User user;

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria não pode ser vazio.");
        }
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) ||
                (Objects.equals(name, category.name) && Objects.equals(user, category.user));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, user);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", nome='" + name + '\'' +
                (user != null ? ", usuario=" + user.getUsername() : "") +
                '}';
    }
}
