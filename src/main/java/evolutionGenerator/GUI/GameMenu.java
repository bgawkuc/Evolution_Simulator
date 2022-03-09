package evolutionGenerator.GUI;

import javafx.scene.Group;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GameMenu {
    private final Group group;
    private Text gameType, animalCounter, plantCounter, dominantGenotype, averageLifeSpan, averageChildNumber, averageCurrentEnergy;
    private Button displayAnimals, saveToFile;
    private XYChart.Series<Number, Number> animalSeries, plantSeries;

    private static boolean active = true;
    private final int TRANSLATE_X = 20;

    public GameMenu(Group group) {
        this.group = group;
        this.addGameMenu();
    }

    public Text getGameType() {return gameType;}

    public Text getAnimalCounter() {return animalCounter;}

    public Text getPlantCounter() {return plantCounter;}

    public Text getDominantGenotype() {return dominantGenotype;}

    public Text getAverageLifeSpan() {return averageLifeSpan;}

    public Text getAverageChildNumber() {return averageChildNumber;}

    public Text getAverageCurrentEnergy() {return averageCurrentEnergy;}

    public Button getDisplayAnimals() {return displayAnimals;}

    public Button getSaveToFile() {return saveToFile;}

    public XYChart.Series<Number,Number> getAnimalSeries() {return animalSeries;}

    public XYChart.Series<Number,Number> getPlantSeries() {return plantSeries;}

    public static boolean isActive() {return active;}

    public void addGameMenu() {

        Button stopButton = createButton("Stop", 10);
        Button endButton = createButton("End", 50);

        stopButton.setOnAction( event ->  {
            active = !active;
            if (active) {
                stopButton.setText("Stop");
            }
            else {
                stopButton.setText("Start");
            }
        });

        endButton.setOnAction( event ->
                System.exit(0));

        gameType = createText(120);
        animalCounter = createText(140);
        plantCounter = createText(160);
        dominantGenotype = createText(180);
        averageLifeSpan = createText(200);
        averageChildNumber = createText(220);
        averageCurrentEnergy = createText(240);

        createLegendElement("Step",new int[] {231, 235, 181}, 280);
        createLegendElement("Jungle",new int[] {188, 235, 127}, 310);
        createLegendElement("Plant",new int[] {114, 196, 6}, 340);
        createLegendElement("Animal with energy from 80% to 100%",new int[] {82, 20, 20}, 370);
        createLegendElement("Animal with energy from 60% to 80%",new int[] {135, 9, 9}, 400);
        createLegendElement("Animal with energy from 40% to 60%",new int[] {245, 39, 24}, 430);
        createLegendElement("Animal with energy from 20% to 40%",new int[] {255, 166, 0}, 460);
        createLegendElement("Animal with energy from 0% to 20%",new int[] {237, 233, 14}, 490);

       displayAnimals = createButton("Animals with dominant genotype",540);
       saveToFile = createButton("Save to CSV file",580);
       createLineChart();
    }

    private void createLegendElement(String text, int[] colors, int translateY) {
        HBox hBox = new HBox();
        Text text1 = new Text("  " + text);
        Rectangle rectangle = new Rectangle(0,0, 20, 20);
        rectangle.setFill(Color.rgb(colors[0],colors[1],colors[2]));
        hBox.setTranslateX(TRANSLATE_X);
        hBox.setTranslateY(translateY);
        hBox.getChildren().addAll(rectangle,text1);
        group.getChildren().add(hBox);
    }

    private Button createButton(String text, int translateY) {
        Button button = new Button(text);
        button.setTranslateX(TRANSLATE_X);
        button.setTranslateY(translateY);
        button.setMinSize(100, 20);
        group.getChildren().add(button);
        return button;
    }

    private Text createText(int translateY) {
        Text text = new Text();
        text.setTranslateX(TRANSLATE_X);
        text.setTranslateY(translateY);
        group.getChildren().add(text);
        return text;
    }

//  based on https://docs.oracle.com/javafx/2/charts/line-chart.htm#CIHGBCFI
    private void createLineChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Days");
        yAxis.setLabel("Number");

        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTranslateX(550);
        lineChart.setTranslateY(420);
        lineChart.setPrefSize(400,200);
        group.getChildren().add(lineChart);

        animalSeries = new XYChart.Series<Number,Number>();
        plantSeries = new XYChart.Series<Number,Number>();
        animalSeries.setName("Animals number");
        plantSeries.setName("Plants number");
        lineChart.getData().add(animalSeries);
        lineChart.getData().add(plantSeries);
    }
}
