package evolutionGenerator;

import evolutionGenerator.GUI.GameMenu;
import evolutionGenerator.GUI.Statistics;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.FileNotFoundException;
import java.util.*;

public class SimulationEngine implements Runnable {
    private final ArrayList<Animal> animals, deadAnimals = new ArrayList<>();
    private final List<IAnimalMoveObserver> animalMoveObservers = new LinkedList<>();
    private final WorldMap map;
    private final int moveEnergy, plantEnergy;
    private final double startEnergy, half_of_start_energy;
    private final Vector2d lowerCorner,upperCorner;
    private boolean active;
    private final boolean magicSelected;
    private int cntAdded = 3, simulationDay = 0;
    private final Statistics statistics;
    private final Button displayAnimals, saveToFile;

    public SimulationEngine(WorldMap map, int animalNumber, double startEnergy, int plantEnergy, int moveEnergy, boolean active, GameMenu menu, boolean magicSelected) {
        this.animals = new ArrayList<>();
        this.map = map;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.startEnergy = startEnergy;
        this.half_of_start_energy = 0.5 * startEnergy;
        this.active = active;
        this.magicSelected = magicSelected;
        this.statistics = new Statistics(menu,magicSelected);
        this.lowerCorner = map.getLowerCorner();
        this.upperCorner = map.getUpperCorner();
        this.displayAnimals = menu.getDisplayAnimals();
        this.saveToFile = menu.getSaveToFile();

        displayAnimalsClicked();

        saveToFileClicked();

        addAnimalsToJungle(animalNumber);
    }

    public int getSimulationDay() {return simulationDay;}

    private void updateAnimals() {
        for (IAnimalMoveObserver animalMoveObserver : animalMoveObservers)
            animalMoveObserver.mapUpdate();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("End of simulation");
        }
    }

    private void updateAnimalsLast() {
        for (IAnimalMoveObserver animalMoveObserver : animalMoveObservers)
            animalMoveObserver.lastMapUpdate();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.out.println("End of simulation");
        }
    }


    private void updateIsActive() {
        active = GameMenu.isActive();
    }

    @Override
    public void run() {
        updateAnimals();

        statistics.addStatistics(animals,deadAnimals,map.plants,map.mapGenotypes);

        while (!map.isWholeMapFilledWithPlants()) {

            updateIsActive();

            if (active) {
                updateAnimals();
                removeDeadAnimals();
                moveAnimals();
                eatPlants();
                reproduceAnimals();
                addPlants();
                Statistics.updateDailyStatistics(animals, deadAnimals, map.plants);
                simulationDay++;
            }
//          when game is paused
            else {
                updateAnimalsLast();
                while (!active) {
                    updateIsActive();
                    updateAnimals();
                }
            }
        }
        updateAnimals();

//      it'll wait a little bit before ending
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private void addAnimalsToJungle(int animalNumber) {
        ArrayList<Integer> free = map.freePositions(map.getLowerJungleCorner(), map.getUpperJungleCorner());

        for (int i=0; i < animalNumber; i++) {
            if (!free.isEmpty()) {
                int randomIdx = map.generateRandomInt(free.size()/2);
                Vector2d vector2d = new Vector2d(free.get(randomIdx*2), free.get(randomIdx*2+1));
                free.remove(randomIdx*2+1);
                free.remove(randomIdx*2);
                Animal animal = new Animal(vector2d,startEnergy,map);
                map.place(animal);
                animals.add(animal);
            }
        }
    }

    public void addAnimalMoveObserver(IAnimalMoveObserver animalMoveObserver) {animalMoveObservers.add(animalMoveObserver);}

    private void moveAnimals() {
        for (Animal animal: animals) {
            animal.move(moveEnergy);
        }
        updateAnimals();
        statistics.updateAverageCurrentEnergy(animals);
    }

//  check which plants are eaten
    private void eatPlants() {
        if (animals.size() > 0) {
            ArrayList<Plant> removePlants = new ArrayList<>();

            for (Plant plant: map.mapPlants.values()) {
                Vector2d plantPosition = plant.getPosition();
                AnimalCell animalCell = map.mapAnimals.get(plantPosition);

                if (animalCell != null) {

                    ArrayList<Animal> eatingAnimals = map.mapAnimals.get(plantPosition).findStrongestAnimals();

                    if (eatingAnimals.size() > 0) {
                        double earnEnergy = (double) plantEnergy / eatingAnimals.size();

                        for (Animal animal: eatingAnimals) {
                            animal.addEnergy(earnEnergy);
                        }
                        removePlants.add(plant);
                    }
                }
            }

            for (Plant plant: removePlants) {
                map.mapPlants.remove(plant.getPosition());
                map.plants.remove(plant);
            }
        }
        statistics.updateAverageCurrentEnergy(animals);
        updateAnimals();
    }


//  delete dead animals, during magic game adds additional 5 animals
    private void removeDeadAnimals() {
        ArrayList<Animal> animalsToCopy = new ArrayList<>();

        if (animals.size() == 5 && cntAdded > 0 && magicSelected) {
            animalsToCopy = new ArrayList<>(animals);
        }

        ArrayList<Animal> removeAnimals = new ArrayList<>();
        for (Animal animal: animals) {
            if (animal.getCurrentEnergy() <= 0) {
                removeAnimals.add(animal);
                deadAnimals.add(animal);
            }
        }

        for (Animal animal: removeAnimals) {
            AnimalCell animalCell = map.mapAnimals.get(animal.getPosition());
            animalCell.removeAnimal(animal);
            animals.remove(animal);
            map.animals.remove(animal);

            if (animals.size() == 5 && cntAdded > 0 && magicSelected) {
                animalsToCopy = new ArrayList<>(animals);
            }
        }

        if (animalsToCopy.size() == 5) {
            addNewFive(animalsToCopy);
        }
        statistics.updateAnimalCounter(animals);
        statistics.updateAverageLifeSpan(deadAnimals);
        statistics.updateAverageCurrentEnergy(animals);
        updateAnimals();
    }

    private void reproduceAnimals() {
        for (Vector2d position: map.mapAnimals.keySet()) {
            List<Animal> animalsAtPosition = map.mapAnimals.get(position).getAnimals();

            if (animalsAtPosition.size() >= 2) {
                ArrayList<Animal> twoStrongestAnimals = map.mapAnimals.get(position).findTwoStrongestAnimals();
                Animal animal1 = twoStrongestAnimals.get(0);
                Animal animal2 = twoStrongestAnimals.get(1);

                if (animal1.getCurrentEnergy() >= half_of_start_energy && animal2.getCurrentEnergy() >= half_of_start_energy) {
                    int[] childGenotype = Genotype.inheritGenotype(animal1,animal2);

                    double energy1 =  animal1.getCurrentEnergy() / 4;
                    double energy2 =  animal2.getCurrentEnergy() / 4;

                    animal1.addEnergy(-energy1);
                    animal2.addEnergy(-energy2);

                    animal1.addChild();
                    animal2.addChild();

                    Animal childAnimal = new Animal(position,energy1 + energy2,this.map,childGenotype);
                    map.place(childAnimal);
                    animals.add(childAnimal);
                }
            }
        }
        statistics.updateAnimalCounter(animals);
        statistics.updateAverageChildNumber(animals);
        statistics.updateDominantGenotype(map.mapGenotypes);
        statistics.updateAverageCurrentEnergy(animals);
        updateAnimals();
    }

    private void addNewFive(ArrayList<Animal> animalsToCopy) {
        ArrayList<Animal> newAnimals = new ArrayList<>();
        ArrayList<Integer> free = map.freePositions(lowerCorner,upperCorner);

        for (Animal animal: animalsToCopy) {
            if (!free.isEmpty()) {
                int randomIdx = map.generateRandomInt(free.size()/2);
                Vector2d vector2d = new Vector2d(free.get(randomIdx*2), free.get(randomIdx*2+1));
                free.remove(randomIdx*2+1);
                free.remove(randomIdx*2);

                Animal newAnimal = new Animal(vector2d,startEnergy,map,animal.getGenotype());
                newAnimals.add(newAnimal);
            }
        }

        for (Animal newAnimal : newAnimals) {
            map.place(newAnimal);
            animals.add(newAnimal);
        }
        cntAdded--;

        statistics.updateMagicGame(3-cntAdded);
        statistics.updateAverageCurrentEnergy(animals);
        updateAnimals();
    }

        private void addPlants() {
            map.addPlantToJungle();
            map.addPlantToStep();
            statistics.updatePlantCounter(map.plants);
        }

        public int[] findGenotype(int x, int y) {
            updateAnimals();
            Vector2d position = new Vector2d(x,y);

            if (map.mapAnimals.get(position) != null) {
                Animal animal = map.mapAnimals.get(position).getAnimals().get(0);
                return animal.getGenotype();
            }
            else {
                return new int[0];
            }
        }

        private ArrayList<Vector2d> animalsWithDominantGenotype() {
            ArrayList<Vector2d> dominantPositions = new ArrayList<>();
            int[] dominant = Statistics.getDominant();
            for (Animal animal: animals) {
                if (Arrays.equals(dominant,animal.getGenotype())) {
                    dominantPositions.add(animal.getPosition());
                }
            }
            return dominantPositions;
        }

//    display positions of all alive animals with dominant genotype
        private void displayAnimalsClicked() {
            displayAnimals.setOnAction( event ->  {
                if (!active) {
                    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setResizable(true);
                    dialog.getDialogPane().setPrefSize(500, 200);
                    ArrayList<Vector2d> dominantPositions = animalsWithDominantGenotype();
                    if (dominantPositions.isEmpty()) {
                        dialog.setContentText("All animals with dominant position are dead\n" );
                    }
                    else {
                        dialog.setContentText("Alive animals with dominant genotype :\n" + dominantPositions);
                    }
                    dialog.show();
                }

            });
        }

        private void saveToFileClicked() {
                saveToFile.setOnAction( event -> {
                    try {
                        statistics.saveToFile();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });
        }
}