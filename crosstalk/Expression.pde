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
    if(step >= 0 && step < values.length)
      return values[step];
    return 0;
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

public class CellCharge extends NormExpression{

  public CellCharge(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

	public float operation(float  val) {
 		float t1 = 1;
		float t2 = 4;
 		float t3 = 5;
 		float t4 = 15;
 		float t5 = 9;
 		if(val < t2) {
   		return 1-exp(-val/t1);
 		} else if(val < t4) {
   		return (1-exp(-t2/t1))*exp(-(val-t2)/t3);
 		} else {
   		return (1-exp(-t2/t1))*exp(-(t4-t2)/t3)*exp(-(val-t4)/t5);
 		}
	}
}

public class CellProbability extends NormExpression{

  public CellProbability(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

  public float operation(float val){
 		float t1 = 1;
		float t2 = 4;

 		if(val < t2) {
   		return 1-exp(-val/t1);
    }
    return 0;
  }
  
  protected void updateValues(){
    float maxima = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = operation(minimum + i * (float)(maximum - minimum) / numSteps);
      if(values[i] > maxima){
        maxima = values[i];
      }
    }
    for(int i = 0; i < values.length; i++){
      values[i] /= maxima;
    }
  }
}
