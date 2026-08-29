import java.io.*;
import java.util.*;

public class VocabManager {

    private final List<Word> deck = new ArrayList<>();
    private final Map<String, Word> deckByText = new HashMap<>();

    public void addCards(List<String> lines, boolean debug, boolean allowPromotion) {
        for (String l : lines) {

            l = l.trim();
            if (l.isEmpty()) continue;

            String[] parts = l.split("/");

            if (parts.length != 3) {
                if (debug) System.out.println("Invalid entry: " + l);
                continue;
            }

            Tier tier = Tier.fromString(parts[1].trim());
            String cardsStr = parts[2].trim();

            if (tier == null || !cardsStr.matches("\\d+")) {
                if (debug) System.out.println("Invalid entry: " + l);
                continue;
            }

            String text = parts[0].trim().toLowerCase();
            int cards = Integer.parseInt(cardsStr);

            Word existing = deckByText.get(text);

            if (existing == null) {
                Word word = new Word(text, tier, cards);
                deck.add(word);
                deckByText.put(text, word);
            } else {
                if (debug) System.out.println(text + " already exists.");
                if (allowPromotion) {
                    existing.promote();
                    existing.setCards(cards);
                }
            }
        }
    }



    public void sortDeck() {
        List<Tier> order = List.of(
                Tier.S_PLUS, Tier.S, Tier.A_PLUS,
                Tier.A, Tier.B_PLUS, Tier.D
        );

        deck.sort(Comparator.comparingInt(w -> order.indexOf(w.getTier())));
    }

    public void fetch(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty()) {
                    addCards(List.of(line), false, false);
                }
            }
        } catch (IOException e) {
            System.out.println("File not found: " + fileName);
        }
    }

    public void save(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Word word : deck) {
                bw.write(word.getText() + "/" +
                        word.getTier().toDisplay() + "/" +
                        word.getCards());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Word> dispenseVocab(int target) {
        List<Word> best = new ArrayList<>();
        int bestDiff = Integer.MAX_VALUE;

        for (int start = 0; start < deck.size(); start++) {

            int total = 0;
            List<Word> current = new ArrayList<>();

            for (int i = start; i < deck.size(); i++) {
                Word word = deck.get(i);

                if (total + word.getCards() <= target) {
                    current.add(word);
                    total += word.getCards();
                }
            }

            int diff = target - total;

            if (diff < bestDiff) {
                bestDiff = diff;
                best = new ArrayList<>(current);
            }

            if (bestDiff == 0) break; // perfect match
        }

        if (bestDiff != 0) {
            System.out.println("No perfect combination of cards.");
        }

        for (Word w : best) {
            deck.remove(w);
            deckByText.remove(w.getText());
        }

        return best;
    }

    public List<Word> getDeck() {
        return deck;
    }

    public Word findWord(String text) {
        return deckByText.get(text.toLowerCase().trim());
    }

}