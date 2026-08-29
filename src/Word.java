public class Word {
    private String text;
    private Tier tier;
    private int cards;

    public Word(String text, Tier tier, int cards) {
        this.text = text.toLowerCase();
        this.tier = tier;
        this.cards = cards;
    }

    public String getText() { return text; }
    public Tier getTier() { return tier; }
    public int getCards() { return cards; }

    public void setCards(int cards) { this.cards = cards; }

    public void setText(String text) {
        this.text = text;
    }

    public void promote() {
        switch (tier) {
            case D -> tier = Tier.B_PLUS;
            case B_PLUS -> tier = Tier.A;
            case A -> tier = Tier.A_PLUS;
            case A_PLUS -> tier = Tier.S;
            case S -> tier = Tier.S_PLUS;
            case S_PLUS -> {
                System.out.println(text + " is already S+.");
                return;
            }
        }
        System.out.println(text + " is now in " + tier.toDisplay());
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }
}