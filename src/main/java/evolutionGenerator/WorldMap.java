package evolutionGenerator;

import java.util.*;

public class WorldMap implements IPositionChangeObserver{
    private final int width;
    private final int height;
    private final int ratio;
    private final Vector2d lowerCorner;
    private final Vector2d upperCorner;
    private final Vector2d jungleLowerCorner;
    private final Vector2d jungleUpperCorner;

    public Map<Vector2d, AnimalCell> mapAnimals = new LinkedHashMap<>();
    public Map<Vector2d, Plant> mapPlants = new LinkedHashMap<>();
    public Map<int[],Integer> mapGenotypes = new LinkedHashMap<>();
    public ArrayList<Animal> animals = new ArrayList<>();
    public ArrayList<Plant> plants = new ArrayList<>();


    public WorldMap(int width, int height, int ratio) {
        this.width = width;
        this.height = height;
        this.ratio = ratio;
        this.lowerCorner = new Vector2d(0,0);
        this.upperCorner = new Vector2d(width-1,height-1);
        this.jungleLowerCorner = getLowerJungleCorner();
        this.jungleUpperCorner = getUpperJungleCorner();
    }

    public Vector2d getLowerCorner() {return lowerCorner;}

    public Vector2d getUpperCorner() {return upperCorner;}

    public Vector2d getLowerJungleCorner() {
        int spaceX = (width - ratio) / 2;
        int spaceY = (height - ratio) / 2;
        return new Vector2d(spaceX,spaceY);
    }

    public Vector2d getUpperJungleCorner() {
        Vector2d lowerJungleCorner = getLowerJungleCorner();
        return new Vector2d(lowerJungleCorner.x + ratio-1, lowerJungleCorner.y + ratio-1);
    }

    public boolean isPositionInJungle(Vector2d position) {return getLowerJungleCorner().precedes(position) && getUpperJungleCorner().follows(position);}

    public boolean canMoveTo(Vector2d position) {return position.follows(lowerCorner) && position.precedes(upperCorner);}

    public void place(Animal animal) {
        addGenotype(animal.getGenotype());
        animals.add(animal);
        addAnimalToMap(animal,animal.getPosition());
        animal.addObserver(this);
    }

//    przy dodaniu zwierzęcia do mapy dodaje jego genotyp hash mapy
    private void addGenotype(int[] newGenotype) {
        for (Map.Entry<int[],Integer> entry: mapGenotypes.entrySet()) {
            int[] key = entry.getKey();
            Integer value = entry.getValue();

            if (Arrays.equals(key, newGenotype)) {
                mapGenotypes.put(key,value+1);
                mapGenotypes.remove(key,value);
                return;
            }
        }
        mapGenotypes.put(newGenotype,1);
    }

    public boolean isOccupied(Vector2d position) {return objectAt(position) != null;}

    public IMapElement objectAt(Vector2d position) {
        for (Animal animal: animals) {
            if (animal.getPosition().equals(position)) {
//              zwracam najsilniejsze(pod wzgledem energii) zwierze
                ArrayList<Animal> strongestAnimals = mapAnimals.get(animal.getPosition()).findStrongestAnimals();
                Random random = new Random();
                int randomIdx = random.nextInt(strongestAnimals.size());
                return strongestAnimals.get(randomIdx);
            }
        }

        for (Plant plant: plants) {
            if (plant.getPosition().equals(position)) {
                return plant;
            }
        }
        return null;
    }

    public void addAnimalToMap(Animal animal, Vector2d position) {
        if (mapAnimals.get(position) == null) {
            AnimalCell animalCell = new AnimalCell(position,animal,this);
            mapAnimals.put(position, animalCell);
        }
        else {
            mapAnimals.get(position).addAnimal(animal);
        }
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        if (!oldPosition.equals(newPosition)) {
            if (mapAnimals.get(oldPosition).getAnimals().size() == 1) {
                mapAnimals.remove(oldPosition);
            }
            else {
                mapAnimals.get(oldPosition).removeAnimal(animal);
            }
            addAnimalToMap(animal,newPosition);
        }
    }

    public ArrayList<Integer> freePositions(Vector2d start, Vector2d end) {
        ArrayList<Integer> free = new ArrayList<>();
        for (int i = start.x; i <= end.x; i++) {
            for (int j = start.y; j <= end.y; j++) {
                if (objectAt(new Vector2d(i, j)) == null) {
                    free.add(i);
                    free.add(j);
                }
            }
        }
        return free;
    }

    public ArrayList<Integer> freePositionsAtStep() {
        ArrayList<Integer> free = new ArrayList<>();
        for (int i = lowerCorner.x; i <= upperCorner.x; i++) {
            for (int j = lowerCorner.y; j <= upperCorner.y; j++) {
                if (objectAt(new Vector2d(i, j)) == null && !isPositionInJungle(new Vector2d(i,j))) {
                    free.add(i);
                    free.add(j);
                }
            }
        }
        return free;
    }

    public int generateRandomInt(int max) {return (int) ((Math.random() * max));}

    private void addPlant(ArrayList<Integer> free) {
        if (free.size() > 0) {
            int randomIdx = this.generateRandomInt(free.size()/2);
            Vector2d vector2d = new Vector2d(free.get(randomIdx*2), free.get(randomIdx*2+1));
            free.remove(randomIdx*2+1);
            free.remove(randomIdx*2);

            Plant plant = new Plant(vector2d);
            mapPlants.put(vector2d,plant);
            plants.add(plant);
        }
    }

    public void addPlantToJungle() {
        ArrayList<Integer> free = freePositions(jungleLowerCorner,jungleUpperCorner);
        addPlant(free);
    }

    public void addPlantToStep() {
        ArrayList<Integer> free = freePositionsAtStep();
        addPlant(free);
    }

    public boolean isWholeMapFilledWithPlants() {return plants.size() == width * height;}
}
