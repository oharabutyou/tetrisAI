public class Individual implements Comparable<Individual>{
    private double[][] inputWeight;
    private double[] outputWeight;
    private long score;
    private long lines;

	@Override
	public int compareTo(Individual o) {
        if(lines==o.lines){
            if(score<o.score)return -1;
            else if(score==o.score)return 0;
            else return 1;
        }else{
            return (lines<o.lines)?-1:1;
        }
	}

    public Individual(double[][] inputWeight, double[] outputWeight) {
        this.inputWeight = inputWeight;
        this.outputWeight = outputWeight;
    }

    public Individual(Individual indivi,long score,long lines){
        this(indivi.inputWeight,indivi.outputWeight);
        this.score=score;
        this.lines=lines;
    }

    public Individual(Individual i){
        this(i.inputWeight,i.outputWeight,i.score,i.lines);
    }

    public Individual clone(){
        return new Individual(inputWeight.clone(),outputWeight.clone(),score,lines);
    }

    public double[][] getInputWeight(){
        return inputWeight;
    }
    public double[] getOutputWeight(){
        return outputWeight;
    }

    public long getLines(){
        return lines;
    }

    public long getScore(){
        return score;
    }

    public Individual(double[][] inputWeight, double[] outputWeight, long score, long lines) {
        this.inputWeight = inputWeight;
        this.outputWeight = outputWeight;
        this.score = score;
        this.lines = lines;
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

    public void setLines(long lines) {
        this.lines = lines;
    }

}