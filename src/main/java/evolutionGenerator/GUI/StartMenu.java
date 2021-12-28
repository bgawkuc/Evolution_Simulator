package evolutionGenerator.GUI;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StartMenu {
    public final VBox vBox;
    private TextField animalNumber, width, height, startEnergy, moveEnergy, plantEnergy, ratio;
    private ToggleGroup gameChoice;
    private RadioButton magicGame;

    public StartMenu(VBox vBox) {
        this.vBox = vBox;
        this.addStartMenu();
    }

    public TextField getAnimalNumber() {return animalNumber;}

    public TextField getWidth() {return width;}

    public TextField getHeight() {return height;}

    public TextField getStartEnergy() {return startEnergy;}

    public TextField getMoveEnergy() {return moveEnergy;}

    public TextField getPlantEnergy() {return plantEnergy;}

    public TextField getRatio() {return ratio;}

    public ToggleGroup getGameChoice() {return gameChoice;}

    public RadioButton getMagicGame() {return magicGame;}

    public void addStartMenu() {
        animalNumber = addTextField("Animal number", "40");
        width = addTextField("Width","20");
        height = addTextField("Height","20");
        startEnergy = addTextField("Start energy","100");
        moveEnergy = addTextField("Move energy","3");
        plantEnergy = addTextField("Plant energy","20");
        ratio = addTextField("Ratio","10");

        gameChoice = addGameChoice();

        vBox.setSpacing(7);
        vBox.setLayoutX(50);
        vBox.setLayoutY(30);
    }

    private TextField addTextField(String textFieldName, String setValue) {
        Label label = new Label(textFieldName);
        TextField textField = new TextField();
        textField.setText(setValue);
        int MAX_WIDTH = 150;
        textField.setMaxWidth(MAX_WIDTH);
        vBox.getChildren().addAll(label,textField);
        return textField;
    }

    private ToggleGroup addGameChoice() {
        RadioButton normalGame = new RadioButton("Normal game");
        magicGame = new RadioButton("Magic game");
        ToggleGroup gameChoice= new ToggleGroup();
        normalGame.setToggleGroup(gameChoice);
        magicGame.setToggleGroup(gameChoice);
        normalGame.setSelected(true);
        HBox game = new HBox(normalGame,magicGame);
        vBox.getChildren().add(game);
        return gameChoice;
    }

}
