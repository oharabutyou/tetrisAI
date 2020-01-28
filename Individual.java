public class Individual implements Comparable<Individual> {
    private double[][] inputWeight;
    private double[] outputWeight;
    private long score;

    @Override
    public int compareTo(Individual o) {
        if (score > o.score)
            return -1;
        else if (score == o.score)
            return 0;
        else
            return 1;
    }

    public Individual(double[][] inputWeight, double[] outputWeight) {
        this.inputWeight = inputWeight;
        this.outputWeight = outputWeight;
    }

    public Individual(double[][] inputWeight, double[] outputWeight, long score) {
        this.inputWeight = inputWeight;
        this.outputWeight = outputWeight;
        this.score = score;
    }

    public Individual(Individual indivi, long score) {
        this(indivi.inputWeight, indivi.outputWeight, score);
    }

    public Individual(Individual i) {
        this(i.inputWeight, i.outputWeight);
        this.score = i.score;
    }

    public Individual clone() {
        return new Individual(inputWeight.clone(), outputWeight.clone(), score);
    }

    public double[][] getInputWeight() {
        double[][] clone = inputWeight.clone();
        for (int i = 0; i < clone.length; i++) {
            clone[i] = inputWeight[i].clone();
        }
        return clone;
    }

    public double[] getOutputWeight() {
        return outputWeight.clone();
    }

    public long getScore() {
        return score;
    }

    public Individual(double[][] inputWeight, double[] outputWeight, long score, long lines) {
        this.inputWeight = inputWeight;
        this.outputWeight = outputWeight;
        this.score = score;
    }

    public void setInputWeight(double[][] inputWeight) {
        this.inputWeight = inputWeight;
    }

    public void setOutputWeight(double[] outputWeight) {
        this.outputWeight = outputWeight;
    }

    public void setScore(long score) {
        this.score = score;
    }

}