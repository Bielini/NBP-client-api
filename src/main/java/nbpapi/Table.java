package nbpapi;

public enum Table {
    TABLE_A("A"),
    TABLE_B("B"),
    TABLE_C("C");

    private final String value;

    Table(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

