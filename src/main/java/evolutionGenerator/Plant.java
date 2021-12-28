package evolutionGenerator;

public class Plant implements IMapElement{
    private final Vector2d position;

    public Plant(Vector2d position) {this.position = position;}

    @Override
    public Vector2d getPosition() {return position;}

    @Override
    public boolean isAnimal() {return false;}
}

