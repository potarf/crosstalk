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
  float t1;
  float t2;
  float t3;

  public CellCharge(float t1, float t2, float t3, int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    this.t1 = t1;
    this.t2 = t2;
    this.t3 = t3;
    updateValues();
  }

  public float operation(float val){
    if(val < t2){
      return 1 - exp(-val/t1);
    } else {
      return (1 - exp(-t2/t1)) * exp(-(val - t2) / t3);
    }
  }

  public void setT1(float t1){
    if(this.t1 != t1){
      this.t1 = t1;
      updateValues();
    }
  }

  public void setT2(float t2){
    if(this.t2 != t2){
      this.t2 = t2;
      updateValues();
    }
  }

  public void setT3(float t3){
    if(this.t3 != t3){
      this.t3 = t3;
      updateValues();
    }

  }
}

public class CellProbability extends NormExpression{
  float t1;
  float t2;

  public CellProbability(float t1, float t2, int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    this.t1 = t1;
    this.t2 = t2;
    updateValues();
  }

  public float operation(float val){
    return 1 - exp(-val/t1);
  }

  public void setT1(float t1){
    if(this.t1 != t1){
      this.t1 = t1;
      updateValues();
    }
  }

  public void setT2(float t2){
    if(this.t2 != t2){
      this.t2 = t2;
      this.numSteps = (int)(t2 * e.STEPS_PER_NS);
      updateValues();
    }
  }
}
