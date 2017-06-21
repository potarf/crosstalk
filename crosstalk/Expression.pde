public abstract class NormExpression{

  float[] values;
  int minimum;
  int maximum;
  int numSteps;

  public NormExpression(int minimum, int maximum, int numSteps){
    this.minimum = minimum;
    this.maximum = maximum;
    this.numSteps = numSteps;
    values = new float[numSteps];
  }

  protected void updateValues(){
    float total = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = operation(minimum + i * (float)(maximum - minimum) / numSteps); 
      total += values[i];
    }
    for(int i = 0; i < values.length; i++){
      values[i] /= total;
    }
  }

  public float get(int step){
    return values[step];
  }
  
  abstract float operation(float val);
}
public class GaussianIntNorm extends NormExpression{
  float sigma;
  float mean;
  
  public GaussianIntNorm(float sigma, float mean, int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    this.sigma = sigma;
    this.mean = mean;
    updateValues();
  }

  public float operation(float val){
    return 1/sqrt(2 * sigma * sigma * PI) * exp(-pow(val - mean, 2) / (2 * pow(sigma, 2)));
  }

  public void setSigma(float sigma){
    if(this.sigma != sigma){
      updateValues();
      this.sigma = sigma;
    }
  }

  public void setMean(float mean){
    if(this.mean != mean){
      updateValues();
      this.mean = mean;
    }
  }
}

