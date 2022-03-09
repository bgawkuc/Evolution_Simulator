package evolutionGenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

// contain animals that are in the field with coordinates (x,y)
public class AnimalCell {
    private final Vector2d position;
    private final List<Animal> animals = new LinkedList<>();
    private final WorldMap map;

    public AnimalCell(Vector2d position, Animal animal, WorldMap map) {
        this.position = position;
        this.map = map;
        animals.add(animal);
    }

    public List<Animal> getAnimals() {return animals;}

    public void addAnimal(Animal animal) {animals.add(animal);}

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
        if (animals.isEmpty()) map.mapAnimals.remove(position);
    }

//   returns 2 strongest animals (with the biggest amount of energy)
    public ArrayList<Animal> findTwoStrongestAnimals() {
        ArrayList<Animal> twoStrongestAnimals = new ArrayList<>();
        ArrayList<Animal> strongestAnimals = findStrongestAnimals();

        if (animals.size() < 2) {
            return twoStrongestAnimals;
        }

        else if (strongestAnimals.size() >= 2) {
            Random random = new Random();
            int randomIdx1, randomIdx2;
            randomIdx1 = random.nextInt(strongestAnimals.size());
            randomIdx2 = random.nextInt(strongestAnimals.size());

            while (randomIdx1 != randomIdx2) {
                randomIdx2 = random.nextInt(strongestAnimals.size());
            }

            twoStrongestAnimals.add(strongestAnimals.get(randomIdx1));
            twoStrongestAnimals.add(strongestAnimals.get(randomIdx2));
            return twoStrongestAnimals;
        }

        else {
            twoStrongestAnimals.add(strongestAnimals.get(0));
            Animal secondAnimal = strongestAnimals.get(0);
            double secondAnimalEnergy = 0;

            for (Animal animal: animals) {
                if (animal.getCurrentEnergy() > secondAnimalEnergy &&
                        animal.getCurrentEnergy() < strongestAnimals.get(0).getCurrentEnergy()) {
                    secondAnimal = animal;
                    secondAnimalEnergy = animal.getCurrentEnergy();
                }
            }
            twoStrongestAnimals.add(secondAnimal);
            return twoStrongestAnimals;
        }
    }

//  returns all strongest animals
    public ArrayList<Animal> findStrongestAnimals() {
        ArrayList<Animal> strongestAnimals = new ArrayList<>();
        double mostEnergy = 0;

        for (Animal animal: animals) {
            if (strongestAnimals.isEmpty() || animal.getCurrentEnergy() == mostEnergy) {
                strongestAnimals.add(animal);
            }
            else if (animal.getCurrentEnergy() > mostEnergy) {
                strongestAnimals.clear();
                strongestAnimals.add(animal);
                mostEnergy = animal.getCurrentEnergy();
            }
        }

        return strongestAnimals;
    }
}
