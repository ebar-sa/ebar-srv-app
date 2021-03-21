package com.ebarapp.ebar.model.type;

public enum RationType {
	APPETIZER("Appetizer"), 
	HALF_RATION("Half Ration"), 
	RATION("Racion"), 
	UNIT("Unit");

    private String type;

    private RationType(final String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
    
	@Override
	public String toString() {
		return this.type;
	}
}