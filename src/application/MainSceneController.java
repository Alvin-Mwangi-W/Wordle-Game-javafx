package application;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainSceneController {
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField guessInput;
    @FXML
    private Label welcomeText;
    @FXML
    private Button exitButton;
    @FXML
    private Label playerNameLabel;
    @FXML
    private Label scoreLabel;

    private ArrayList<String> wordList = new ArrayList<>();
    private String word;
    private char[][] shadowData;

    private static final String FILE_PATH = "D:\\Coding\\Java\\WordleGame\\src\\application\\words.txt";
    private static final String LOG_FILE_PATH = "D:\\Coding\\Java\\WordleGame\\src\\application\\game_logs.txt";

    private String playerName;
    private int roundCount = 0;
    private int score = 0; 

    public void initialize() {
        promptForName();
        loadWordsFromFile();
        selectRandomWord();
        initializeShadowData();
        gridPane.setGridLinesVisible(true);
    }
    

    private void promptForName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Welcome to Wordle!");
        dialog.setHeaderText("Please enter your name:");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> playerName = name);
        playerNameLabel.setText(playerName);
    }

    private void loadWordsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectRandomWord() {
        Random random = new Random();
        int index = random.nextInt(wordList.size());
        word = wordList.get(index);
        wordList.remove(index);
    }

    private void initializeShadowData() {
        shadowData = new char[5][5];
    }

    @FXML
    protected void checkGuess() {
        String guess = guessInput.getText();
        if (isValidWord(guess)) {
            updateShadowData(guess);
            saveGameLog("Guess: " + guess);

            for (int i = 0; i < guess.length(); i++) {
                char letter = guess.charAt(i);
                Color color;
                if (word.charAt(i) == letter) {
                    color = Color.web("#50C878");
                } else if (word.indexOf(letter) > -1) {
                    color = Color.web("#007FFF");
                } else {
                    color = Color.web("#fd5c63");
                }
                Rectangle rectangle = new Rectangle(58, 58, color);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(1);

                Label letterLabel = new Label(String.valueOf(letter));
                letterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                letterLabel.setAlignment(Pos.CENTER);

                StackPane stackPane = new StackPane();
                stackPane.getChildren().addAll(rectangle, letterLabel);

                gridPane.add(stackPane, i, roundCount);
            }

            roundCount++;
            if (guess.equals(word)) {
            	calculateScore();
                handleRoundEnd(true);
            } else if (roundCount == 5) {
                handleRoundEnd(false);
            }
        } else {
            welcomeText.setText("Please enter a 5-letter word.");
            guessInput.clear();
        }
    }

    private void handleRoundEnd(boolean won) {
        if (won) {
            calculateAndDisplayScore();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("You ran out of attempts. The word was: " + word);
            alert.showAndWait();
        }
        resetGame();
    }

    private boolean isValidWord(String word) {
        return word.length() == 5;
    }

    private void updateShadowData(String guess) {
        for (int i = 0; i < guess.length(); i++) {
            shadowData[roundCount][i] = guess.charAt(i);
        }
    }

    @FXML
    protected void saveGameLogOnClick() {
        String guess = guessInput.getText();
        saveGameLog("Guess: " + guess);
    }
    @FXML
    private void calculateScore() {
        score += 10; 
        scoreLabel.setText(" " + score);
    }

    private void saveGameLog(String log) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            bw.write(playerName + " -> " + log + "SCORE: -> " + score + "\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void calculateAndDisplayScore() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText(null);
        alert.setContentText("You guessed the word correctly: " + word);
        alert.showAndWait();
        resetGame();
    }

    @FXML
    protected void exitApplication() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void resetGame() {
        guessInput.clear();
        welcomeText.setText("Welcome to Wordle!");
        gridPane.getChildren().clear();
        roundCount = 0;
        selectRandomWord();
        initializeShadowData();
    }

}