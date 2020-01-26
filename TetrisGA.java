import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * GA GA for NN
 */
public class TetrisGA {

    private static final double mutation = 0.01;
    private static final double crossOver = 0.94;
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

    public static void main(String[] args) {
        TetrisGA ga = new TetrisGA(100, 50);
        ga.startSearch();
        TetrisAI ai = new TetrisAI(ga.inputWeight, ga.outputWeight);
        game.initAI(ai);
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
            System.out.println("top in " + gen + ":" + nextGen.get(0).getLines() + "," + nextGen.get(0).getScore());
        }
        inputWeight = nextGen.get(0).getInputWeight();
        outputWeight = nextGen.get(0).getOutputWeight();
        System.out.println(outputWeight);
        System.out.println(inputWeight);
    }

    private void firstGen() {
        currentGen = new ArrayList<>();
        int indivi = 0;
        while (indivi < individuals) {
            TetrisAI ai = new TetrisAI();
            game.initAI(ai);
            while (!game.isGameOver()) {
                game.AIPlay();
            }
            TetrisScore score = game.score();
            if (score.getLines() > 0) {
                currentGen.add(new Individual(ai.getWeight(), score.getScore(), score.getLines()));
                indivi++;
                System.out.println(indivi);
            }
        }
        Collections.sort(currentGen);
        Individual top = currentGen.get(0);
        System.out.println("top in firstGen:" + top.getLines() + "," + top.getScore());
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
            double random = rand.nextDouble();
            Individual i;
            if (random < crossOver) {
                i = crossOver(select(), select());
            } else if (random < (crossOver + mutation)) {
                i = mutation(select());
            } else
                i = select();
            TetrisAI ai = new TetrisAI(i.getInputWeight(), i.getOutputWeight());
            game.initAI(ai);
            while (!game.isGameOver()) {
                game.AIPlay();
            }
            TetrisScore score = game.score();
            if (score.getLines() > 0) {
                nextGen.add(new Individual(i, score.getScore(), score.getLines()));
                indivi++;
                System.out.println(indivi);
            }
        }
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
