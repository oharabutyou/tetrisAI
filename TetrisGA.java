import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * GA GA for NN
 */
public class TetrisGA {

    private static final int copy = 5;
    private static final double mutationRate = 0.01;
    private int individuals;
    private int generations;
    private double[][] inputWeight;
    private double[] outputWeight;
    private ArrayList<Individual> currentGen;
    private ArrayList<Individual> nextGen;
    private static Random rand = new Random();
    private long genSum;
    private int inputUnit;
    private int midUnit;
    private int inputWeightSize;
    private long scores;

    public TetrisGA(int individuals, int generations) {
        this.individuals = individuals;
        this.generations = generations;
    }

    synchronized void addScores(long scores) {
        this.scores += scores;
    }

    public double[][] getInputWeight() {
        return inputWeight;
    }

    public double[] getOutputWeight() {
        return outputWeight;
    }

    public void startSearch() {
        firstGen();
        for (int gen = 1; gen < generations; gen++) {
            nextGen();
            Individual top = nextGen.get(0);
            System.out.println("top in " + gen + ":" + top.getScore());
            printWeights(top);
        }
        Individual top = nextGen.get(0);
        inputWeight = top.getInputWeight();
        outputWeight = top.getOutputWeight();
    }

    private void printWeights(Individual ind) {
        double[][] inputWeight = ind.getInputWeight();
        double[] outputWeight = ind.getOutputWeight();
        // print weights
        System.out.print("inputWeight:\n{");
        for (int i = 0; i < inputWeight.length; i++) {
            System.out.print("{");
            for (int j = 0; j < inputWeight[i].length; j++) {
                System.out.print(inputWeight[i][j] + ",");
            }
            System.out.print("},");
        }
        System.out.print("}\noutputWeight:\n{");
        for (int i = 0; i < outputWeight.length; i++) {
            System.out.print(outputWeight[i] + ",");
        }
        System.out.print("}\n");
    }

    private void firstGen() {
        currentGen = new ArrayList<>();
        int indivi = 0;
        while (indivi < individuals) {
            TetrisAI ai = new TetrisAI();
            scores = 0;
            Thread[] threads = new Thread[100];
            for (int repeat = 0; repeat < 100; repeat++) {
                threads[repeat] = new Thread() {
                    @Override
                    public void run() {
                        TetrisSimple game = new TetrisSimple();
                        game.initAI(ai);
                        while (!game.isGameOver()) {
                            game.AIPlay();
                        }
                        addScores(game.getScore());
                    }
                };
                threads[repeat].start();
            }
            for (int i = 0; i < threads.length; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (scores > 1000) {
                currentGen.add(new Individual(ai.getWeight(), scores));
                indivi++;
                if (indivi % 10 == 0)
                    System.out.print(indivi + " ");
            }
        }
        System.out.println();
        Collections.sort(currentGen);
        Individual top = currentGen.get(0);
        System.out.println("top in firstGen:" + top.getScore());
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
            genSum += i.getScore();
        }
        while (indivi < individuals) {
            Individual i;
            if (indivi < copy) {
                i = currentGen.get(indivi).clone();
            } else
                i = mutation(crossOver(select(), select()));
            TetrisAI ai = new TetrisAI(i.getInputWeight(), i.getOutputWeight());
            scores = 0;
            Thread[] threads = new Thread[100];
            for (int repeat = 0; repeat < 100; repeat++) {
                threads[repeat] = new Thread() {
                    @Override
                    public void run() {
                        TetrisSimple game = new TetrisSimple();
                        game.initAI(ai);
                        while (!game.isGameOver()) {
                            game.AIPlay();
                        }
                        addScores(game.getScore());
                    }
                };
                threads[repeat].start();
            }
            for (int j = 0; j < threads.length; j++) {
                try {
                    threads[j].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            nextGen.add(new Individual(ai.getWeight(), scores));
            indivi++;
            if (indivi % 10 == 0)
                System.out.print(indivi + " ");
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
            sum += currentGen.get(i).getScore();
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

        for (int i = 0; i < inputA.length; i++) {
            for (int j = 0; j < inputA[i].length; j++) {
                if (rand.nextBoolean())
                    inputA[i][j] = inputB[i][j];
            }
        }
        for (int i = 0; i < outputA.length; i++) {
            if (rand.nextBoolean())
                outputA[i] = outputB[i];
        }
        return new Individual(inputA, outputA);
    }

    private Individual mutation(Individual i) {
        Individual mutation = new Individual(i);
        double[][] input = mutation.getInputWeight();
        double[] output = mutation.getOutputWeight();
        for (int index = 0; index < input.length; index++) {
            for (int j = 0; j < input[index].length; j++) {
                if (rand.nextDouble() < mutationRate)
                    input[index][j] = 1.0 - rand.nextDouble() * 2.0;
            }
        }
        for (int index = 0; index < output.length; index++) {
            if (rand.nextDouble() < mutationRate)
                output[index] = 1.0 - rand.nextDouble() * 2.0;
        }
        mutation.setInputWeight(input);
        mutation.setOutputWeight(output);
        return mutation;
    }

    private int randIndex() {
        int indexSum = inputWeightSize;
        return rand.nextInt(indexSum);
    }
}
