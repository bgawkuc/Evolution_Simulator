package evolutionGenerator;

import java.util.Objects;

public class Vector2d {
    public final int x, y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {return "(" + x + "," + y + ")";}

    public boolean precedes(Vector2d other) {return x <= other.x && y <= other.y;}

    public boolean follows(Vector2d other) {return x >= other.x && y >= other.y;}

    public Vector2d add(Vector2d other) {return new Vector2d(x + other.x, y + other.y);}

    public Vector2d subtract(Vector2d other) {return new Vector2d(x - other.x, y - other.y);}

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        return x == ((Vector2d) other).x && y == ((Vector2d) other).y;
    }

    @Override
    public int hashCode() {return Objects.hash(this.x, this.y);}
}

