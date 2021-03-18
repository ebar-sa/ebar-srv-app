package main.java.com.ebarapp.ebar.model;

public enum Categoria {
    ENTRANTE("Entrante"), BRASA("Brasa"), BEBIDA("Bebida"), POSTRE("Postre");

    private String displayName;

    private Categoria(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}