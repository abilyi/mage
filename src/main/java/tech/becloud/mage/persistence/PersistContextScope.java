package tech.becloud.mage.persistence;

public enum PersistContextScope {

    NONE(0),
    EXECUTION(1),
    USER(2),
    ALL(3);

    private int mask;

    private PersistContextScope(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }
}
