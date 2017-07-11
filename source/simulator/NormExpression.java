package simulator;
import java.lang.Math;

abstract class NormExpression{

  protected double[] values;
  protected int minimum;
  protected int maximum;
  protected int numSteps;

  public NormExpression(int minimum, int maximum, int numSteps){
    this.minimum = minimum;
    this.maximum = maximum;
    this.numSteps = numSteps;
    values = new double[numSteps];
  }

  protected void updateValues(){
    double total = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = 
          operation(minimum + i * (double)(maximum - minimum) / numSteps);
      
      total += values[i];
    }
    for(int i = 0; i < values.length; i++){
      values[i] /= total;
    }
    System.out.println(total);
  }

  public double get(int step){
    if(step >= 0 && step < values.length)
      return values[step];
    return 0;
  }
  
  protected abstract double operation(double val);
}

class CellCharge extends NormExpression{

  public CellCharge(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

	protected double operation(double  val) {
 		double t1 = 1;
		double t2 = 4;
 		double t3 = 5;
 		double t4 = 15;
 		double t5 = 9;
 		if(val <= t2) {
   		return 1-Math.exp(-val/t1);
 		} else if(val <= t4) {
   		return (1-Math.exp(-t2/t1))*Math.exp(-(val-t2)/t3);
 		} else {
   		return (1-Math.exp(-t2/t1))*Math.exp(-(t4-t2)/t3)*Math.exp(-(val-t4)/t5);
 		}
	}
}

class CellProbability extends NormExpression{

  public CellProbability(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

  protected double operation(double val){
 		double t1 = 1;
		double t2 = 4;

 		if(val < t2) {
   		return 1-Math.exp(-val/t1);
    }
    return 0;
  }
  
  protected void updateValues(){
    double maxima = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = operation(minimum + i * (double)(maximum - minimum) / numSteps);
      if(values[i] > maxima){
        maxima = values[i];
      }
    }
    for(int i = 0; i < values.length; i++){
      values[i] /= maxima;
    }
  }
}

class CellRecharge extends NormExpression{

  public CellRecharge(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

  
	protected double operation(double val){
    double t1 = 4;
 		double tRc = 5;
		double t2 = 40;
  
    if(val < t1){
      return 0; 
    }

 		if(val < t2) {
   		return 1-Math.exp(-(val - t1)/tRc);
    }
    return 1;
  }

  protected void updateValues(){
    double maxima = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = operation(minimum + i * (double)(maximum - minimum) / numSteps);
      if(values[i] > maxima){
        maxima = values[i];
      }
    }
    for(int i = 0; i < values.length; i++){
      values[i] /= maxima;
    }
  }

  public double get(int step){
    if(step >= 0 && step < values.length)
      return values[step];
    return 1;
  }

}

class LightPulse extends NormExpression{
  public LightPulse(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

  
	protected double operation(double val){
    double t1 = 0.8f;
   	double t2 = 4;
  	double t3 = 14;
    double t4 = 8;
    double t5 = 6;
    double t6 = 20;
    double t7 = 14;
  
    if(val < t2){
      return 1 - Math.exp(-val/t1); 
    }
 		if(val < t4){
   		return (1 - Math.exp(-t2/t1))*(Math.exp(-(val - t2)/t3));
    }
    if(val < t6){
   		return (1 - Math.exp(-t2/t1))*(Math.exp(-(t4 - t2)/t3)) * 
             (Math.exp(-(val - t4)/t5));
    }
    return (1 - Math.exp(-t2/t1))*(Math.exp(-(t4 - t2)/t3)) * 
           (Math.exp(-(t6 - t4)/t5)) * Math.exp(-(val - t6)/t7);
  }
}
