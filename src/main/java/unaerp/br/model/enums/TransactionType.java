package unaerp.br.model.enums;

public enum TransactionType {
    INCOME("Receita"),
    EXPENSE("Despesa");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}