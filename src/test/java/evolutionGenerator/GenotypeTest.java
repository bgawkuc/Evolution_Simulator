package evolutionGenerator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenotypeTest {

    @Test
    void testShouldKnowIfInheritedGenotypeIsCorrect() {
        WorldMap map = new WorldMap(10,10,5);
        int[] genotype1 = {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 7, 7, 7, 7};
        int[] genotype2 = {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 7};
        int[] childGenotype1 = {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 7};
        int[] childGenotype2 = {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 4, 5, 5, 5, 5, 6, 6, 7, 7, 7, 7};
        Animal animal1 = new Animal(new Vector2d(0,1),50,map,genotype1);
        Animal animal2 = new Animal(new Vector2d(1,1),150,map,genotype2);

//        w zależności od wylosowanej strony - lewa/prawa, to istnieją dwie możliwości genotypu dziecka
        int[] correctChildGenotype = Genotype.inheritGenotype(animal1,animal2);
        boolean correct1 = Arrays.equals(correctChildGenotype,childGenotype1);
        boolean correct2 = Arrays.equals(correctChildGenotype,childGenotype2);

        int number;
        if (correct1 || correct2) number = 0;
        else number = 1;
        assertEquals(number,0);
    }

}