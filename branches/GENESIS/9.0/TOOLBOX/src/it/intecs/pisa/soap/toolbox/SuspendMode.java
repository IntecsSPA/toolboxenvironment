package it.intecs.pisa.soap.toolbox;

public class SuspendMode {

    public static final String HARD = "hard";
    public static final String SOFT = "soft";
    public static final SuspendMode HARD_MODE = new SuspendMode(HARD);
    public static final SuspendMode SOFT_MODE = new SuspendMode(SOFT);

    public static SuspendMode getSuspendMode(String suspendString) {
        if (suspendString.equals(HARD_MODE.toString())) {
            return HARD_MODE;
        }
        if (suspendString.equals(SOFT_MODE.toString())) {
            return SOFT_MODE;
        }
        return null;
    }
    private final String suspendString;

    private SuspendMode(String suspendString) {
        super();
        this.suspendString = suspendString;
    }

    public String toString() {
        return suspendString;
    }

    @Override
    public boolean equals(Object arg0) {
        SuspendMode comparable;

        comparable=(SuspendMode)arg0;

        return comparable.toString().equals(suspendString);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.suspendString != null ? this.suspendString.hashCode() : 0);
        return hash;
    }


}
