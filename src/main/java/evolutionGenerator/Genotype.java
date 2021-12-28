package evolutionGenerator;

import java.util.Arrays;
import java.util.Random;

public class Genotype {
    private static final int GENOTYPE_LENGTH = 32;
    private static final int GENES_TYPE = 8;

    public static int[] createRandomGenotype() {
        int[] genotype = new int[GENOTYPE_LENGTH];
        for (int i = 0; i < GENOTYPE_LENGTH; i++) {
            genotype[i] = (int) ((Math.random() * GENES_TYPE));
        }
        Arrays.sort(genotype);
        return genotype;
    }


    public static int[] inheritGenotype(Animal animal1, Animal animal2) {
        double moreEnergy, lessEnergy;
        int border;
        int[] moreEnergyGen, lessEnergyGen, childGenotype = new int[GENOTYPE_LENGTH];

//       czy genotyp rodzica, który ma więcej energii ma zajmować lewą część
        boolean moreLeft = new Random().nextBoolean();

        if (animal1.getCurrentEnergy() > animal2.getCurrentEnergy()) {
            moreEnergy = animal1.getCurrentEnergy();
            lessEnergy = animal2.getCurrentEnergy();
            moreEnergyGen = animal1.getGenotype();
            lessEnergyGen = animal2.getGenotype();
        }
        else {
            lessEnergy = animal1.getCurrentEnergy();
            moreEnergy = animal2.getCurrentEnergy();
            lessEnergyGen = animal1.getGenotype();
            moreEnergyGen = animal2.getGenotype();
        }

        border = (int) ((moreEnergy * GENOTYPE_LENGTH ) / (moreEnergy+lessEnergy));


        if (moreLeft) {
            if (border >= 0) System.arraycopy(moreEnergyGen, 0, childGenotype, 0, border);

            if (GENOTYPE_LENGTH - border >= 0)
                System.arraycopy(lessEnergyGen, border, childGenotype, border, GENOTYPE_LENGTH - border);
        }
        else {
            if (GENOTYPE_LENGTH - border >= 0) System.arraycopy(lessEnergyGen, 0, childGenotype, 0, GENOTYPE_LENGTH - border);

            if (GENOTYPE_LENGTH - (GENOTYPE_LENGTH - border) >= 0)
                System.arraycopy(moreEnergyGen, GENOTYPE_LENGTH - border, childGenotype, GENOTYPE_LENGTH - border, GENOTYPE_LENGTH - (GENOTYPE_LENGTH - border));
        }
        Arrays.sort(childGenotype);

        return childGenotype;
    }
}
