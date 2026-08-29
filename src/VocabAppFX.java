import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.converter.IntegerStringConverter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.paint.Color;

public class VocabAppFX extends Application {

    // ===================== COLOUR SCHEME =====================
    private static final String NAVY          = "#23235B";
    private static final String NAVY_MID      = "#2D2D7A";
    private static final String NAVY_LIGHT    = "#3D3DAA";
    private static final String PURPLE        = "#9B59B6";
    private static final String PURPLE_LIGHT  = "#D2A8E8";
    private static final String PURPLE_BRIGHT = "#EDD9F7";
    private static final String SILVER_BRIGHT = "#FFFFFF";
    private static final String ACCENT        = "#D17EF8";
    private static final String ACCENT_DARK   = "#A855D4";
    private static final String TEXT_LIGHT    = "#FFFFFF";
    private static final String TEXT_DIM      = "#C8CEFF";

    private String buttonStyle(boolean primary) {
        if (primary) {
            return "-fx-background-color: linear-gradient(to bottom, " + ACCENT + ", " + ACCENT_DARK + ");" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 13px;" +
                    "-fx-background-radius: 6px;" +
                    "-fx-cursor: hand;" +
                    "-fx-border-color: " + ACCENT + ";" +
                    "-fx-border-radius: 6px;" +
                    "-fx-border-width: 1px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(168,85,247,0.4), 6, 0, 0, 2);";
        } else {
            return "-fx-background-color: " + NAVY_LIGHT + ";" +
                    "-fx-text-fill: " + PURPLE_BRIGHT + ";" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 13px;" +
                    "-fx-background-radius: 6px;" +
                    "-fx-cursor: hand;" +
                    "-fx-border-color: " + PURPLE + ";" +
                    "-fx-border-radius: 6px;" +
                    "-fx-border-width: 1px;";
        }
    }

    private String textFieldStyle() {
        return "-fx-background-color: " + NAVY_MID + ";" +
                "-fx-text-fill: " + TEXT_LIGHT + ";" +
                "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
                "-fx-border-color: " + PURPLE + ";" +
                "-fx-border-radius: 6px;" +
                "-fx-background-radius: 6px;" +
                "-fx-font-size: 13px;";
    }

    private String labelStyle() {
        return "-fx-text-fill: " + PURPLE_BRIGHT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;";
    }

    @Override
    public void start(Stage stage) {
        System.out.println(System.getProperty("user.dir"));

        // ===================== DATA SETUP =====================

        Map<String, VocabManager> managers = new HashMap<>();

        Map<String, String> files = Map.of(
                "Korean",   "korean_vocab_java.txt",
                "Japanese", "japanese_vocab_java.txt",
                "Spanish",  "spanish_vocab_java.txt"
        );

        managers.put("Korean",   new VocabManager());
        managers.put("Japanese", new VocabManager());
        managers.put("Spanish",  new VocabManager());

        managers.get("Korean").fetch(files.get("Korean"));
        managers.get("Japanese").fetch(files.get("Japanese"));
        managers.get("Spanish").fetch(files.get("Spanish"));


        // ===================== TOP BAR =====================

        ComboBox<String> languageBox = new ComboBox<>();
        languageBox.getItems().addAll("Korean", "Japanese", "Spanish");
        languageBox.setValue("Korean");
        languageBox.setStyle(
                "-fx-background-color: " + PURPLE_LIGHT + ";" +
                        "-fx-text-fill: " + PURPLE_LIGHT + ";" +
                        "-fx-border-color: " + PURPLE + ";" +
                        "-fx-border-radius: 6px;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        Label titleLabel = new Label("✦  Vocab Manager  ✦");
        titleLabel.setStyle(
                "-fx-text-fill: " + SILVER_BRIGHT + ";" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );
        DropShadow purpleGlow = new DropShadow(12, Color.web(ACCENT));
        titleLabel.setEffect(purpleGlow);

        Button saveButton = new Button("💾  Save");
        saveButton.setStyle(buttonStyle(true));
        saveButton.setOnMouseEntered(_ -> saveButton.setStyle(buttonStyle(true) + "-fx-background-color: " + ACCENT_DARK + ";"));
        saveButton.setOnMouseExited(_ -> saveButton.setStyle(buttonStyle(true)));
        saveButton.setOnAction(_ -> {
            String lang = languageBox.getValue();
            VocabManager vm = managers.get(lang);
            vm.sortDeck();
            vm.save(files.get(lang));
            System.out.println("Saved " + lang + " vocab.");
        });

        HBox topBar = new HBox(titleLabel, languageBox, saveButton);
        topBar.setStyle(
                "-fx-background-color: linear-gradient(to right, " + NAVY + ", " + NAVY_LIGHT + ");" +
                        "-fx-border-color: " + ACCENT + ";" +
                        "-fx-border-width: 0 0 2 0;"
        );
        topBar.setSpacing(12);
        topBar.setPadding(new Insets(12, 15, 12, 15));
        topBar.setAlignment(Pos.CENTER_LEFT);


        // ===================== TABLE =====================

        TableView<Word> table = new TableView<>();

        TableColumn<Word, String> wordCol = new TableColumn<>("Word");
        wordCol.setCellValueFactory(new PropertyValueFactory<>("text"));

        TableColumn<Word, Tier> tierCol = new TableColumn<>("Tier");
        tierCol.setCellValueFactory(new PropertyValueFactory<>("tier"));

        TableColumn<Word, Integer> cardsCol = new TableColumn<>("Cards");
        cardsCol.setCellValueFactory(new PropertyValueFactory<>("cards"));

        table.getColumns().addAll(wordCol, tierCol, cardsCol);

        ObservableList<Word> data = FXCollections.observableArrayList();
        table.setItems(data);

        Runnable refreshTable = () -> {
            String lang = languageBox.getValue();
            VocabManager vm = managers.get(lang);
            vm.sortDeck();
            data.setAll(vm.getDeck());
        };

        refreshTable.run();
        languageBox.setOnAction(_ -> refreshTable.run());


        // ===================== ADD TAB =====================

        TextArea inputArea = new TextArea();
        inputArea.setPromptText("Enter vocab like: word/tier/cards");
        inputArea.setStyle(textFieldStyle());
        inputArea.setPrefHeight(110);

        TextArea feedbackArea = new TextArea();
        feedbackArea.setEditable(false);
        feedbackArea.setPrefHeight(75);
        feedbackArea.setStyle(
                "-fx-background-color: #07071A;" +
                        "-fx-text-fill: #B39DDB;" +
                        "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
                        "-fx-border-color: " + PURPLE + ";" +
                        "-fx-border-radius: 6px;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-font-size: 12px;"
        );

        Label feedbackLabel = new Label("⚠  Feedback:");
        feedbackLabel.setStyle(labelStyle());

        Button addButton = new Button("＋  Add");
        addButton.setStyle(buttonStyle(true));
        addButton.setOnMouseEntered(_ -> addButton.setStyle(buttonStyle(true) + "-fx-background-color: " + ACCENT_DARK + ";"));
        addButton.setOnMouseExited(_ -> addButton.setStyle(buttonStyle(true)));

        addButton.setOnAction(_ -> {
            String text = inputArea.getText().trim();
            List<String> lines = Arrays.asList(text.split("\\R"));

            String lang = languageBox.getValue();
            VocabManager vm = managers.get(lang);

            StringBuilder feedback = new StringBuilder();
            int added = 0;
            int promoted = 0;

            for (String line : lines) {
                if (line.isBlank()) continue;

                String[] parts = line.split("/");

                if (parts.length != 3) {
                    feedback.append("❌ Invalid format (need word/tier/cards): ").append(line).append("\n");
                    continue;
                }

                Tier tier = Tier.fromString(parts[1]);
                if (tier == null) {
                    feedback.append("❌ Invalid tier '").append(parts[1]).append("' in: ").append(line).append("\n");
                    continue;
                }

                if (!parts[2].matches("\\d+")) {
                    feedback.append("❌ Cards must be a number in: ").append(line).append("\n");
                    continue;
                }

                String wordText = parts[0].toLowerCase();
                if (vm.findWord(wordText) != null) {
                    feedback.append("⬆ '").append(wordText).append("' already exists — promoted!\n");
                    promoted++;
                } else {
                    added++;
                }
            }

            vm.addCards(lines, true, true);
            inputArea.clear();
            refreshTable.run();

            if (added > 0)    feedback.insert(0, "✅ Added " + added + " word(s).\n");
            if (promoted > 0) feedback.insert(0, "⬆ Promoted " + promoted + " word(s).\n");
            if (feedback.isEmpty()) feedback.append("Nothing to add!");

            feedbackArea.setText(feedback.toString().trim());
        });

        inputArea.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.ENTER) {
                addButton.fire();
            }
        });

        Label addLabel = new Label("Enter vocab:");
        addLabel.setStyle(labelStyle());

        VBox addLayout = new VBox(addLabel, inputArea, addButton, feedbackLabel, feedbackArea);
        addLayout.setSpacing(10);
        addLayout.setPadding(new Insets(15));
        addLayout.setStyle("-fx-background-color: " + NAVY + ";");


        // ===================== TIER LIST TAB =====================

        TextField searchField = new TextField();
        searchField.setPromptText("Enter word to search...");
        searchField.setStyle(textFieldStyle());

        Button searchButton = new Button("🔍  Search");
        searchButton.setStyle(buttonStyle(false));
        searchButton.setOnMouseEntered(_ -> searchButton.setStyle(buttonStyle(false) + "-fx-border-color: " + PURPLE_LIGHT + ";"));
        searchButton.setOnMouseExited(_ -> searchButton.setStyle(buttonStyle(false)));

        Button deleteButton = new Button("🗑  Delete");
        deleteButton.setStyle(buttonStyle(false));
        deleteButton.setOnMouseEntered(_ -> deleteButton.setStyle(buttonStyle(false) + "-fx-border-color: " + PURPLE_LIGHT + ";"));
        deleteButton.setOnMouseExited(_ -> deleteButton.setStyle(buttonStyle(false)));

        searchButton.setOnAction(_ -> {
            String target = searchField.getText().trim().toLowerCase();
            for (Word w : table.getItems()) {
                if (w.getText().toLowerCase().equals(target)) {
                    table.getSelectionModel().select(w);
                    table.scrollTo(w);
                    return;
                }
            }
            System.out.println("Not found");
        });

        searchField.setOnAction(_ -> searchButton.fire());

        deleteButton.setOnAction(_ -> {
            Word selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String lang = languageBox.getValue();
                VocabManager vm = managers.get(lang);
                vm.getDeck().remove(selected);
                refreshTable.run();
            } else {
                System.out.println("No word selected.");
            }
        });

        HBox searchBar = new HBox(searchField, searchButton, deleteButton);
        searchBar.setSpacing(10);
        searchBar.setPadding(new Insets(10));
        searchBar.setStyle("-fx-background-color: " + NAVY_MID + ";");

        VBox printLayout = new VBox(searchBar, table);
        printLayout.setStyle("-fx-background-color: " + NAVY + ";");
        printLayout.setSpacing(5);


        // ===================== DISPENSE TAB =====================

        TextField numberField = new TextField();
        numberField.setPromptText("Enter number of cards");
        numberField.setStyle(textFieldStyle());

        Button dispenseButton = new Button("🃏  Dispense");
        dispenseButton.setStyle(buttonStyle(true));
        dispenseButton.setOnMouseEntered(_ -> dispenseButton.setStyle(buttonStyle(true) + "-fx-background-color: " + ACCENT_DARK + ";"));
        dispenseButton.setOnMouseExited(_ -> dispenseButton.setStyle(buttonStyle(true)));

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setStyle(textFieldStyle());
        outputArea.setPrefHeight(120);

        dispenseButton.setOnAction(_ -> {
            try {
                int num = Integer.parseInt(numberField.getText());
                String lang = languageBox.getValue();
                VocabManager vm = managers.get(lang);
                List<Word> result = vm.dispenseVocab(num);

                StringBuilder sb = new StringBuilder();
                for (Word w : result) {
                    sb.append(w.getText())
                            .append(" x ")
                            .append(w.getCards())
                            .append("\n");
                }

                outputArea.setText(sb.toString());
                refreshTable.run();

            } catch (NumberFormatException ex) {
                outputArea.setText("Enter a valid number.");
            }
        });

        numberField.setOnAction(_ -> dispenseButton.fire());

        Label dispenseLabel = new Label("Number of cards:");
        dispenseLabel.setStyle(labelStyle());
        Label resultsLabel = new Label("Results:");
        resultsLabel.setStyle(labelStyle());

        VBox dispenseLayout = new VBox(dispenseLabel, numberField, dispenseButton, resultsLabel, outputArea);
        dispenseLayout.setSpacing(10);
        dispenseLayout.setPadding(new Insets(15));
        dispenseLayout.setStyle("-fx-background-color: " + NAVY + ";");


        // ===================== EDITABLE TABLE =====================

        table.setEditable(true);

        wordCol.setCellFactory(TextFieldTableCell.forTableColumn());
        wordCol.setOnEditCommit(e -> e.getRowValue().setText(e.getNewValue()));

        cardsCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        cardsCol.setOnEditCommit(e -> e.getRowValue().setCards(e.getNewValue()));

        tierCol.setCellFactory(ComboBoxTableCell.forTableColumn(Tier.values()));
        tierCol.setOnEditCommit(e -> {
            e.getRowValue().setTier(e.getNewValue());
            refreshTable.run();
        });


        // ===================== TABS =====================

        TabPane tabPane = new TabPane();

        Tab addTab      = new Tab("＋  Add",       addLayout);
        Tab printTab    = new Tab("📋  Tier List",  printLayout);
        Tab dispenseTab = new Tab("🃏  Dispense",   dispenseLayout);

        tabPane.getTabs().addAll(addTab, printTab, dispenseTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);


        // ===================== SIZING =====================

        addButton.setPrefWidth(130);
        dispenseButton.setPrefWidth(130);
        deleteButton.setPrefWidth(120);
        searchButton.setPrefWidth(120);
        saveButton.setPrefWidth(100);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);


        // ===================== ROW FACTORY =====================

        table.setRowFactory(_ -> new TableRow<>() {

            private Timeline rainbow;

            @Override
            protected void updateItem(Word word, boolean empty) {
                super.updateItem(word, empty);

                if (rainbow != null) {
                    rainbow.stop();
                    rainbow = null;
                }

                setEffect(null);

                if (word == null || empty) {
                    setStyle("-fx-background-color: transparent;");
                    return;
                }

                switch (word.getTier()) {

                    case S_PLUS -> {
                        final double[] hue = {0};
                        rainbow = new Timeline(
                                new KeyFrame(Duration.millis(120), _ -> {
                                    hue[0] = (hue[0] + 3) % 360;
                                    Color color = Color.hsb(hue[0], 0.8, 1.0);
                                    String hex = String.format("#%02X%02X%02X",
                                            (int)(color.getRed()*255),
                                            (int)(color.getGreen()*255),
                                            (int)(color.getBlue()*255));
                                    setStyle("-fx-background-color: " + hex + ";" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 14px;" +
                                            "-fx-text-fill: black;");
                                })
                        );
                        rainbow.setCycleCount(Animation.INDEFINITE);
                        rainbow.play();
                        setOnMouseEntered(_ -> { if (rainbow != null) rainbow.setRate(2.0); });
                        setOnMouseExited(_ ->  { if (rainbow != null) rainbow.setRate(1.0); });
                    }

                    case S -> {
                        setStyle("-fx-background-color: #d8d027;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-text-fill: white;" +
                                "-fx-border-color: #E0A0FF;" +
                                "-fx-border-width: 1px;");
                        Glow glow = new Glow(0.5);
                        setEffect(glow);
                        setOnMouseEntered(_ -> glow.setLevel(0.9));
                        setOnMouseExited(_ ->  glow.setLevel(0.5));
                    }

                    case A_PLUS -> setStyle("-fx-background-color: #28f3b6; -fx-text-fill: white; -fx-font-weight: bold;");

                    case A -> setStyle("-fx-background-color: #669dcd; -fx-text-fill: #D6EAF8;");

                    case B_PLUS -> setStyle("-fx-background-color: #37a66a; -fx-text-fill: white;");

                    case D -> setStyle("-fx-background-color: #B8540A; -fx-text-fill: #FFE0C0;");

                    default -> setStyle("-fx-background-color: " + NAVY_MID + "; -fx-text-fill: " + TEXT_LIGHT + ";");
                }
            }
        });


        // ===================== ROOT =====================

        VBox root = new VBox(topBar, tabPane);
        root.setStyle("-fx-background-color: " + NAVY + ";");

        Scene scene = new Scene(root, 540, 460);

        scene.getStylesheets().add("data:text/css," +
                ".tab-pane .tab-header-area .tab-header-background {" +
                "    -fx-background-color: " + NAVY_MID + ";" +
                "}" +
                ".tab {" +
                "    -fx-background-color: " + NAVY + ";" +
                "}" +
                ".tab:selected {" +
                "    -fx-background-color: " + NAVY_LIGHT + ";" +
                "}" +
                ".tab .tab-label {" +
                "    -fx-text-fill: " + PURPLE_BRIGHT + ";" +
                "    -fx-font-weight: bold;" +
                "    -fx-font-size: 12px;" +
                "}" +
                ".tab:selected .tab-label {" +
                "    -fx-text-fill: white;" +
                "}" +
                ".table-view .column-header {" +
                "    -fx-background-color: " + NAVY_LIGHT + ";" +
                "}" +
                ".table-view .column-header .label {" +
                "    -fx-text-fill: " + PURPLE_BRIGHT + ";" +
                "    -fx-font-weight: bold;" +
                "}" +
                ".table-view .table-row-cell:selected {" +
                "    -fx-background-color: " + PURPLE + ";" +
                "}" +
                ".scroll-bar {" +
                "    -fx-background-color: " + NAVY + ";" +
                "}" +
                ".text-area .content {" +
                "    -fx-background-color: " + NAVY_MID + ";" +
                "}" +
                ".combo-box-popup .list-view {" +
                "    -fx-background-color: " + NAVY_LIGHT + ";" +
                "}" +
                ".combo-box-popup .list-cell {" +
                "    -fx-background-color: " + NAVY_LIGHT + ";" +
                "    -fx-text-fill: " + TEXT_LIGHT + ";" +
                "}" +
                ".combo-box-popup .list-cell:hover {" +
                "    -fx-background-color: " + PURPLE + ";" +
                "}"
        );

        stage.setScene(scene);
        stage.setTitle("✦ Vocab Manager");
        stage.show();
    }


    static void main() {
        launch();
    }
}