package main.java.com.ebarapp.ebar.model;

public enum Tipo {
    TAPA("Tapa"), MEDIA("Media"), RACION("Racion"), UNIDAD("Unidad");

    private String displayName;

    private Tipo(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}