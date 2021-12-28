package evolutionGenerator.GUI;
import evolutionGenerator.*;
import evolutionGenerator.Animal;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.Objects;

public class App extends Application implements IAnimalMoveObserver{

    private final GridPane gridPane = new GridPane();
    private WorldMap map;
    private SimulationEngine simulationEngine;
    public static boolean active;
    private GameMenu groupElements;
    private Scene startScene, gameScene;
    private XYChart.Series<Number,Number> animalSeries, plantSeries;
    private int lastUpdate = -1;

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) {
        createGameScene();
        createStartScene(primaryStage);

        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    private void createGameScene() {
        Group group = new Group();

        groupElements = new GameMenu(group);
        active = GameMenu.isActive();
        animalSeries = groupElements.getAnimalSeries();
        plantSeries = groupElements.getPlantSeries();

        group.getChildren().add(gridPane);
        gridPane.setTranslateX(550);
        gridPane.setTranslateY(10);

        gameScene = new Scene(group, 1200, 650);
    }

    private void createStartScene(Stage primaryStage) {
        VBox vBox = new VBox();
        StartMenu vBoxElements = new StartMenu(vBox);

        Button startGame = new Button("Start Game");
        vBox.getChildren().add(startGame);

        startScene = new Scene(vBox,1200,650);

        startGame.setOnAction( event ->{

            int animalNumber = Integer.parseInt(vBoxElements.getAnimalNumber().getText());
            int width = Integer.parseInt(vBoxElements.getWidth().getText());
            int height = Integer.parseInt(vBoxElements.getHeight().getText());
            int startEnergy = Integer.parseInt(vBoxElements.getStartEnergy().getText());
            int moveEnergy = Integer.parseInt(vBoxElements.getMoveEnergy().getText());
            int plantEnergy = Integer.parseInt(vBoxElements.getPlantEnergy().getText());
            int ratio = Integer.parseInt(vBoxElements.getRatio().getText());

            inputValidation(animalNumber, width,height,startEnergy,moveEnergy,plantEnergy,ratio);

            boolean magicSelected = Objects.equals(vBoxElements.getGameChoice().getSelectedToggle(), vBoxElements.getMagicGame());

            map = new WorldMap(width,height,ratio);
            simulationEngine = new SimulationEngine(map,animalNumber,startEnergy,plantEnergy,moveEnergy,active,groupElements,
                    magicSelected);
            simulationEngine.addAnimalMoveObserver(this);

            primaryStage.setScene(gameScene);
            Thread simulationEngineThread = new Thread(this.simulationEngine);
            simulationEngineThread.start();
        });
    }


    private void inputValidation(int animalNumber,int width,int height, int startEnergy, int moveEnergy, int plantEnergy,int ratio) {
        if (animalNumber <= 0) throw new IllegalArgumentException("Animal number must be grater than 0");

        if (width <= 0 || width > 30) throw new IllegalArgumentException("Width must be grater than 0 and smaller than 31");

        if (height <= 0 || height > 20) throw new IllegalArgumentException("Height must be grater than 0 and smaller than 21");

        if (startEnergy < 0) throw new IllegalArgumentException("Start energy must be grater than 0");

        if (moveEnergy < 0 || moveEnergy > startEnergy) throw new IllegalArgumentException("Move energy must be grater than 0 and smaller than start energy");

        if (plantEnergy < 0) throw new IllegalArgumentException("Plant energy must be grater than 0");

        if (ratio >= Math.min(width,height) || ratio < 0) throw new IllegalArgumentException("Ratio must be smaller than width and height. Ratio can't be negative number");
    }

//    pola są numerowane współrzędnymi (x,y) w ten sam sposób co w 1 ćwiartce układu współrzędnych
    private void showMap() {
        Vector2d lowerCorner = map.getLowerCorner();
        Vector2d upperCorner = map.getUpperCorner();
        this.gridPane.setGridLinesVisible(true);

        int xSize = upperCorner.x - lowerCorner.x;
        int ySize = upperCorner.y - lowerCorner.y;

        addGrid(xSize, ySize);
        addStepAndJungle(xSize, ySize);
        addAnimalsAndPlants(xSize, ySize);
    }

    private void addGrid(int xSize,  int ySize) {
        for (int x = 0; x <= xSize; x++)
            this.gridPane.getColumnConstraints().add(new ColumnConstraints(20));

        for (int y = 0; y <= ySize; y++)
            this.gridPane.getRowConstraints().add(new RowConstraints(20));

        this.gridPane.setGridLinesVisible(false);
        this.gridPane.setGridLinesVisible(true);

    }

    private void addStepAndJungle(int xSize,  int ySize) {
        for (int x = 0; x <= xSize; x++) {
            for (int y = 0; y <= ySize; y++) {

                HBox cell = new HBox();
                Vector2d position = new Vector2d(x,y);

                if (map.isPositionInJungle(position)) {
                    cell.setBackground(new Background(new BackgroundFill(Color.rgb(188, 235, 127), null, null)));
                }
                else {
                    cell.setBackground(new Background(new BackgroundFill(Color.rgb(231, 235, 181), null, null)));
                }

                gridPane.add(cell,x,map.getUpperCorner().y - y,1,1);
            }
        }
    }

    private void plotUpdate() {
        int simulationDay = simulationEngine.getSimulationDay();
        if (simulationDay != lastUpdate) {
            animalSeries.getData().add(new XYChart.Data<Number,Number>(simulationDay, map.animals.size()));
            plantSeries.getData().add(new XYChart.Data<Number,Number>(simulationDay, map.plants.size()));
            lastUpdate = simulationDay;
        }

    }

    private void addAnimalsAndPlants(int xSize, int ySize) {
        for (int x = 0; x <= xSize; x++) {
            for (int y = 0; y <= ySize; y++) {

                Vector2d vector2d = new Vector2d(x,y);

                if (map.isOccupied(vector2d) ) {
                    IMapElement object = map.objectAt(vector2d);

//                    jeśli na polu jest więcej niż 1 zwierzę to wyświetla się to co ma najwięcej energii
                    if (object.isAnimal()) {
                        HBox cell = new HBox();
                        cell.setPrefSize(20,20);
                        int[] colors = ((Animal) object).getAnimalColor();
                        cell.setBackground(new Background(new BackgroundFill(Color.rgb(colors[0],colors[1],colors[2]), null, null)));
                        gridPane.add(cell,x,map.getUpperCorner().y - y,1,1);

//                        po kliknięciu na zwierzę wyświetli się jego genotyp
                        int takeX = x;
                        int takeY = y;

                        cell.setOnMouseClicked(event -> {
                            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                            dialog.setResizable(true);
                            dialog.getDialogPane().setPrefSize(500, 200);
                            String genotype = Arrays.toString(simulationEngine.findGenotype(takeX, takeY));
                            dialog.setContentText("You picked animal with genotype:\n" + genotype);
                            dialog.show();
                        });
                    }

                    else if (!object.isAnimal()) {
                        HBox cell = new HBox();
                        cell.setBackground(new Background(new BackgroundFill(Color.rgb(114, 196, 6), null, null)));
                        gridPane.add(cell,x,map.getUpperCorner().y - y,1,1);
                    }
                }
            }
        }
    }

    private void update() {
        plotUpdate();
        this.gridPane.getChildren().clear();
        this.gridPane.getRowConstraints().clear();
        this.gridPane.getColumnConstraints().clear();
        showMap();
    }

    @Override
    public void lastMapUpdate() {
        Platform.runLater(this::update);
    }

    @Override
    public void mapUpdate() {
        Platform.runLater(() -> {
            active = GameMenu.isActive();
            if (active) update();
        });
    }
}