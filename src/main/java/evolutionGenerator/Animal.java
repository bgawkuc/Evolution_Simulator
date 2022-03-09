package evolutionGenerator;

import java.util.ArrayList;
import java.util.Random;

public class Animal implements IMapElement {
    private Vector2d position;
    private MapDirection direction = MapDirection.NORTH;
    private double currentEnergy;
    private final double initialEnergy;
    private final int[] genotype;
    private int lifeSpan = 0, childNumber = 0;
    private final WorldMap map;
    private final ArrayList<IPositionChangeObserver> observerArrayList;

    public Animal(Vector2d position, double currentEnergy, WorldMap map) {
        this.position = position;
        this.currentEnergy = currentEnergy;
        this.initialEnergy =  currentEnergy;
        this.genotype = Genotype.createRandomGenotype();
        this.direction = direction.randomDirection();
        this.observerArrayList = new ArrayList<>();
        this.map = map;
    }

    public Animal(Vector2d position, double currentEnergy, WorldMap map, int[] genotype) {
        this.position = position;
        this.currentEnergy = currentEnergy;
        this.initialEnergy = currentEnergy;
        this.genotype = genotype;
        this.direction = direction.randomDirection();
        this.observerArrayList = new ArrayList<>();
        this.map = map;
    }

    public int[] getGenotype() {return genotype;}

    public int getLifeSpan() {return lifeSpan;}

    public int getChildNumber() {return childNumber;}

    public double getCurrentEnergy() {return currentEnergy;}

    @Override
    public Vector2d getPosition() {return position;}

    private MapDirection idToDirection(int directionId) {
        return switch(directionId) {
            case 0 -> MapDirection.NORTH;
            case 1 -> MapDirection.NORTH_EAST;
            case 2 -> MapDirection.EAST;
            case 3 -> MapDirection.SOUTH_EAST;
            case 4 -> MapDirection.SOUTH;
            case 5 -> MapDirection.SOUTH_WEST;
            case 6 -> MapDirection.WEST;
            case 7 -> MapDirection.NORTH_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + directionId);
        };
    }

    private MapDirection randomMove() {
        Random random = new Random();
        int idx = random.nextInt(32);
        return idToDirection(this.genotype[idx]);
    }


//   selecting north(0) or south(4) cause a field change. Other directions cause rotation by 45,90,135... degrees
    public void move(double moveEnergy) {
        MapDirection rotation = randomMove();

        if (rotation == MapDirection.NORTH) {
            if (map.canMoveTo(this.position.add(this.direction.toUnitVector()))) {
                positionChanged(this.position,this.position.add(this.direction.toUnitVector()));
                this.position = this.position.add(this.direction.toUnitVector());
            }
        }
        else if (rotation == MapDirection.SOUTH) {
            if (map.canMoveTo(this.position.subtract(this.direction.toUnitVector()))) {
                positionChanged(this.position,this.position.subtract(this.direction.toUnitVector()));
                this.position = this.position.subtract(this.direction.toUnitVector());
            }
        }
        else {
            this.direction = changeDirection(rotation.directionToId());
        }
        this.lifeSpan++;
        addEnergy(-moveEnergy);
    }

    public void addEnergy(double moveEnergy) {currentEnergy += moveEnergy;}

    public void addChild() {childNumber++;}

//   change direction depending on directionID (from 0 to 7)
    private MapDirection changeDirection(int directionId) {
        while (directionId > 0) {
            this.direction = this.direction.next();
            directionId--;
        }
        return direction;
    }

    public void addObserver(IPositionChangeObserver observer){
        observerArrayList.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        observerArrayList.remove(observer);
    }

    public void positionChanged(Vector2d oldPosition,Vector2d newPosition) {
        for (IPositionChangeObserver observer: observerArrayList) {
            observer.positionChanged(oldPosition,newPosition,this);
        }
    }

    public int[] getAnimalColor() {
        double leftEnergy = (100 * this.currentEnergy) / this.initialEnergy;

        if (leftEnergy > 80) return new int[] {82, 20, 20};

        else if (leftEnergy > 60) return new int[] {135, 9, 9};

        else if (leftEnergy > 40) return new int[] {245, 39, 24};

        else if (leftEnergy > 20) return new int[] {255, 166, 0};

        else return new int[] {237, 233, 14};
    }

    @Override
    public boolean isAnimal() {return true;}
}
