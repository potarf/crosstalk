class StatDist{
  private double current;
  private double mean;
  private double sum;
  private double sqSum;
  private double variance;
  private int size;

  public StatDist(){
    current = 0;
    mean = 0;
    variance = 0;
    size = 0;
  }

  public void add(double value){
    size++;
    current = value;

    sum += current;
    mean = sum / size;
    
    sqSum += current * current;

    variance = (sqSum - 2 * mean * sum + size * mean * mean) / size;
  }

  public double getCurrent(){
    return current;
  }

  public double getMean(){
    return mean;
  }

  public double getVariance(){
    return variance;
  }
}
