public enum Tier {
    S_PLUS, S, A_PLUS, A, B_PLUS, D;

    public static Tier fromString(String s) {
        return switch (s.toLowerCase()) {
            case "s+" -> S_PLUS;
            case "s" -> S;
            case "a+" -> A_PLUS;
            case "a" -> A;
            case "b+" -> B_PLUS;
            case "d" -> D;
            default -> null;
        };
    }

    public String toDisplay() {
        return switch (this) {
            case S_PLUS -> "S+";
            case S -> "S";
            case A_PLUS -> "A+";
            case A -> "A";
            case B_PLUS -> "B+";
            case D -> "D";
        };
    }
}
