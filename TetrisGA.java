import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * GA GA for NN
 */
public class TetrisGA {

    private static final int mutation = 1;
    private static final int copy = 5;
    private int individuals;
    private int generations;
    private double[][] inputWeight;
    private double[] outputWeight;
    private ArrayList<Individual> currentGen;
    private ArrayList<Individual> nextGen;
    private static Tetris game;
    private static Random rand = new Random();
    private long genSum;
    private int inputUnit;
    private int midUnit;
    private int inputWeightSize;

    public TetrisGA(int individuals, int generations) {
        this.individuals = individuals;
        this.generations = generations;
        game = new Tetris();
    }

    public double[][] getInputWeight() {
        return inputWeight;
    }

    public double[] getOutputWeight() {
        return outputWeight;
    }

    public void startSearch() {
        game = new Tetris();
        firstGen();
        for (int gen = 1; gen < generations; gen++) {
            nextGen();
            Individual top=nextGen.get(0);
            System.out.println("top in " + gen + ":" + top.getLines() + "," + top.getScore());
            printWeights(top);
        }
        Individual top = nextGen.get(0);
        inputWeight=top.getInputWeight();
        outputWeight = top.getOutputWeight();
    }

    private void printWeights(Individual ind) {
        double[][] inputWeight=ind.getInputWeight();
        double[] outputWeight=ind.getOutputWeight();
        // print weights
        System.out.println("inputWeight:");
        for(int i=0;i<inputWeight.length;i++){
            for(int j=0;j<inputWeight[i].length;j++){
                System.out.print(inputWeight[i][j]+",");
            }
            System.out.println();
        }
        System.out.println("outputWeight:");
        for(int i=0;i<outputWeight.length;i++){
            System.out.print(outputWeight[i]+",");
        }
        System.out.println();
    }

    private void firstGen() {
        currentGen = new ArrayList<>();
        int indivi = 0;
        while (indivi < individuals) {
            TetrisAI ai = new TetrisAI();
            long lines = 0;
            long scores = 0;
            for (int i = 0; i < 100; i++) {
                game.initAI(ai);
                while (!game.isGameOver()) {
                    game.AIPlay();
                }
                TetrisScore score = game.score();
                lines += score.getLines();
                scores += score.getScore();
            }
            if (lines > 0) {
                currentGen.add(new Individual(ai.getWeight(), scores, lines));
                indivi++;
                System.out.print(indivi+" ");
            }
        }
        System.out.println();
        Collections.sort(currentGen);
        Individual top = currentGen.get(0);
        System.out.println("top in firstGen:" + top.getLines() + "," + top.getScore());
        printWeights(top);
        inputUnit = top.getInputWeight().length;
        midUnit = top.getOutputWeight().length;
        inputWeightSize = inputUnit * midUnit;
    }

    private void nextGen() {
        nextGen = new ArrayList<>();
        int indivi = 0;
        genSum = 0;
        for (Individual i : currentGen) {
            genSum += i.getLines();
        }
        while (indivi < individuals) {
            Individual i;
            if (indivi < copy) {
                i = currentGen.get(indivi);
            } else if (indivi < copy+mutation) {
                i = mutation(select());
            } else
                i = crossOver(select(), select());
            TetrisAI ai = new TetrisAI(i.getInputWeight(), i.getOutputWeight());
            long lines = 0;
            long scores = 0;
            for (int repeat = 0; repeat < 100; repeat++) {
                game.initAI(ai);
                while (!game.isGameOver()) {
                    game.AIPlay();
                }
                TetrisScore score = game.score();
                lines += score.getLines();
                scores += score.getScore();
            }
            nextGen.add(new Individual(ai.getWeight(), scores, lines));
            indivi++;
            System.out.print(indivi+" ");
        }
        System.out.println();
        Collections.sort(nextGen);
        currentGen = nextGen;
    }

    private Individual select() {
        double random = rand.nextDouble();
        long sum = 0;
        int i;
        for (i = 0; i < currentGen.size(); i++) {
            sum += currentGen.get(i).getLines();
            if (random <= (sum / genSum))
                break;
        }
        return currentGen.get(i).clone();
    }

    private Individual crossOver(Individual parentA, Individual parentB) {
        int start = randIndex();
        int end = randIndex();
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        double[][] inputA = parentA.getInputWeight();
        double[] outputA = parentA.getOutputWeight();
        double[][] inputB = parentB.getInputWeight();
        double[] outputB = parentB.getOutputWeight();

        int index = start;
        for (; index < end && index < inputWeightSize; index++) {
            inputA[index / midUnit][index % midUnit] = inputB[index / midUnit][index % midUnit];
        }
        for (; index < end; index++) {
            outputA[index - inputWeightSize] = outputB[index - inputWeightSize];
        }
        return new Individual(inputA, outputA);
    }

    private Individual mutation(Individual i) {
        int index = randIndex();
        Individual mutation = new Individual(i);
        if (index < inputUnit * midUnit) {
            double[][] input = mutation.getInputWeight();
            input[index / midUnit][index % midUnit] = 1.0 - rand.nextDouble() * 2.0;
            mutation.setInputWeight(input);
        } else {
            double[] output = mutation.getOutputWeight();
            output[index - inputWeightSize] = 1.0 - rand.nextDouble() * 2.0;
            mutation.setOutputWeight(output);
        }
        return mutation;
    }

    private int randIndex() {
        int indexSum = inputWeightSize;
        return rand.nextInt(indexSum);
    }
}
