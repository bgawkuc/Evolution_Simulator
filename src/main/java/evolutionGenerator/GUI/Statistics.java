package evolutionGenerator.GUI;

import evolutionGenerator.Animal;
import evolutionGenerator.Plant;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Statistics {
    private final boolean magicSelected;
    private final Text gameType, animalCounter, plantCounter, averageLifeSpan, averageChildNumber, dominantGenotype, averageCurrentEnergy;
    private static int[] dominant;
    private static final ArrayList<ArrayList<Number>> dailyStatistics = new ArrayList<>();

    public Statistics(GameMenu menu, boolean magicSelected) {
        this.magicSelected = magicSelected;
        this.gameType = menu.getGameType();
        this.animalCounter = menu.getAnimalCounter();
        this.plantCounter = menu.getPlantCounter();
        this.averageLifeSpan = menu.getAverageLifeSpan();
        this.averageChildNumber = menu.getAverageChildNumber();
        this.dominantGenotype = menu.getDominantGenotype();
        this.averageCurrentEnergy = menu.getAverageCurrentEnergy();
    }

    public static int[] getDominant() {
        return dominant;
    }

    public void addStatistics(ArrayList<Animal> animals, ArrayList<Animal> deadAnimals, ArrayList<Plant> plants, Map<int[], Integer> mapGenotypes) {
        addGameType();
        updateAnimalCounter(animals);
        updatePlantCounter(plants);
        updateAverageLifeSpan(deadAnimals);
        updateAverageChildNumber(animals);
        updateDominantGenotype(mapGenotypes);
        updateAverageCurrentEnergy(animals);
    }


    private void addGameType() {
        if (magicSelected) {
            gameType.setText("Magic Game!");
        }
        else {
            gameType.setText("Normal Game");
        }
    }

    public void updateMagicGame(int number) {gameType.setText("Added 5 new animals for the " + number + " time");}

    public void updateAnimalCounter(ArrayList<Animal> animals) {animalCounter.setText("Animal number: " + animals.size());}

    public void updatePlantCounter(ArrayList<Plant> plants) {plantCounter.setText("Plants number: " + plants.size());}

    public void updateAverageLifeSpan(ArrayList<Animal> deadAnimals) {averageLifeSpan.setText("Average life span: " +  countAverageLifeSpan(deadAnimals));}

    public void updateAverageChildNumber(ArrayList<Animal> animals) {averageChildNumber.setText("Average child number " +  countAverageChildNumber(animals));}

    public void updateAverageCurrentEnergy(ArrayList<Animal> animals) {averageCurrentEnergy.setText("Average current energy " + countAverageCurrentEnergy(animals));}

//  finds dominant genotype among live and dead animals
    public void updateDominantGenotype(Map<int[],Integer> mapGenotypes) {
        int[] genotype = new int[32];
        int cnt = 0;

        for (Map.Entry<int[],Integer> entry: mapGenotypes.entrySet()) {
            int[] key = entry.getKey();
            Integer value = entry.getValue();
            if (value > cnt) {
                cnt = value;
                genotype = key;
            }
        }
        dominant = genotype;
        dominantGenotype.setText("Dominant genotype: " + Arrays.toString(genotype));
    }

//  average life span calculated on the basis of live and dead animals
    private static double countAverageLifeSpan(ArrayList<Animal> deadAnimals) {
        double result = 0;

        if (deadAnimals.size() != 0) {
            for (Animal animal: deadAnimals) {
                result += animal.getLifeSpan();
            }
            result /= deadAnimals.size();
        }
        return result;
    }

//  average child number calculated on the basis of live parents
    private static double countAverageChildNumber(ArrayList<Animal> animals) {
        double result = 0;

        if (animals.size() > 0) {
            for (Animal animal : animals) {
                result += animal.getChildNumber();
            }
            result /= animals.size();
        }
        return result;
    }

//    average energy calculated on the basis of live parents
    private static double countAverageCurrentEnergy(ArrayList<Animal> animals) {
        double result = 0;

        if (animals.size() > 0) {
            for (Animal animal : animals) {
                result += animal.getCurrentEnergy();
            }
            result /= animals.size();
        }
        return result;
    }


    public static void updateDailyStatistics(ArrayList<Animal> animals, ArrayList<Animal> deadAnimals, ArrayList<Plant> plants) {
        ArrayList<Number> newStatistics = new ArrayList<>();
        newStatistics.add(animals.size());
        newStatistics.add(plants.size());
        newStatistics.add(countAverageLifeSpan(deadAnimals));
        newStatistics.add(countAverageChildNumber(animals));
        newStatistics.add(countAverageCurrentEnergy(animals));
        dailyStatistics.add(newStatistics);
    }

//  save all statistics to file and at the end calculate the average values
    public void saveToFile() throws FileNotFoundException {
        File file = new File("statistics.csv");
        PrintWriter printWriter = new PrintWriter(file);
        Double avgAnimals = 0.0, avgPlants = 0.0, avgLifeSpan = 0.0, avgChildNumber = 0.0, avgEnergy = 0.0;

        for (ArrayList<Number> oneStatistic : dailyStatistics) {
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                string.append(oneStatistic.get(i));
                string.append("    ");

                switch (i) {
                    case 0 -> avgAnimals += (Integer) oneStatistic.get(0);
                    case 1 -> avgPlants += (Integer) oneStatistic.get(1);
                    case 2 -> avgLifeSpan += (Double) oneStatistic.get(2);
                    case 3 -> avgChildNumber += (Double) oneStatistic.get(3);
                    case 4 -> avgEnergy += (Double) oneStatistic.get(4);
                }
            }

            printWriter.printf(String.valueOf(string));
            printWriter.printf("\n");
        }
        printWriter.printf("Average values\n");
        int size = dailyStatistics.size();
        printWriter.printf(avgAnimals / size + "    " + avgPlants / size + "    " +
                avgLifeSpan / size + "    " + avgChildNumber / size + "    " + avgEnergy / size);

        printWriter.close();
    }
}