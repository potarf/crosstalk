package simulator;
import java.lang.Math;

/**
* The NormExpression class manages a normalized function
*
* @author  John Lawrence, Jordan Potarf, Andrew Baas
* @version 1.0
* @since   05-07-2017
*/
abstract class NormExpression{

  // Cached function values
  protected double[] values;

  // Caching-related variables
  protected int minimum;
  protected int maximum;
  protected int numSteps;

  /**
  * Constructor takes necessary caching information
  *
  * @param minimum  The minimum input expected to be given to the function
  * @param maximum  The maximum input
  * @param numSteps The number of steps in caching between the maximum and input
  */
  public NormExpression(int minimum, int maximum, int numSteps){
    
    // Set caching values
    this.minimum = minimum;
    this.maximum = maximum;
    this.numSteps = numSteps;
    
    // Allocate space for caching
    values = new double[numSteps];
  }

  /**
  * Normalizes the function and then caches the values
  */
  protected void updateValues(){
    double total = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = 
          operation(minimum + i * (double)(maximum - minimum) / (double)numSteps);
      
      total += values[i];
    }
    for(int i = 0; i < values.length; i++){
      values[i] /= total;
    }
  }

  /**
  * Retrieves cached values
  *
  * @param step Cached step to retrieve (note, not in normal units)
  * @return The value at the given step
  */
  public double get(int step){
    if(step >= 0 && step < values.length)
      return values[step];
    return 0;
  }
 
  /**
  * Gets values directly from the stored function
  *
  * @param val Input for function
  * @return Value of the function at 'val'
  */ 
  protected abstract double operation(double val);
}

/**
* The CellCharge class is a NormExpression which determines the distribution of
* a Cell's output charge a certain time after activation
*
* @author  John Lawrence, Jordan Potarf, Andrew Baas
* @version 1.0
* @since   05-07-2017
*/
class CellCharge extends NormExpression{
 	private double t1 = .0001;
	private double t2 = .05;
 	private double t3 = .5;
 	private double t4 = 5;
 	private double t5 = .6;

  public CellCharge(int minimum, int maximum, int numSteps, double t1, 
                    double t2, double t3, double t4, double t5){
    super(minimum, maximum, numSteps);
    this.t1 = t1;
    this.t2 = t2;
    this.t3 = t3;
    this.t4 = t4;
    this.t5 = t5;

    updateValues();
  }

	protected double operation(double  val) {
 		if(val <= t2) {
   		return 1-Math.exp(-val/t1);
 		} else {
   		return ((t5) * Math.exp(-(val-t2)/t3) + 
              (1 - t5) * Math.exp(-(val - t2)/t4));
    }
	}
}

/**
* The CellProbability class is a NormExpression which determines the time
* distribution of the probability a cell activates a single neighbor
*
* @author  John Lawrence, Jordan Potarf, Andrew Baas
* @version 1.0
* @since   05-07-2017
*/
class CellProbability extends NormExpression{
  
  private double t1;
  private double t2;

  public CellProbability(int minimum, int maximum, int numSteps, double t1, 
                          double t2){
    super(minimum, maximum, numSteps);
    
    this.t1 = t1;
    this.t2 = t2;

    updateValues();
  }

  protected double operation(double val){
    if(val <= t2) {
   		return 1-Math.exp(-val/t1);
    }
    return 0;
  }
  
  protected void updateValues(){
    double maxima = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = operation(minimum + i * 
                            (double)(maximum - minimum) / numSteps);
      if(values[i] > maxima){
        maxima = values[i];
      }
    }
    if(maxima == 0){
      values[0] = 1;
    } else {
      for(int i = 0; i < values.length; i++){
        values[i] /= maxima;
      }
    }
  }
}

/**
* The CellRecharge class is a NormExpression which determines a Cell's charge a
* period of time after activation
*
* @author  John Lawrence, Jordan Potarf, Andrew Baas
* @version 1.0
* @since   05-07-2017
*/
class CellRecharge extends NormExpression{

  private double t1;

  public CellRecharge(int minimum, int maximum, int numSteps, double t1){
    super(minimum, maximum, numSteps);

    this.t1 = t1;
    
    updateValues();
  }

  
	protected double operation(double val){
 		double tRc = 9;
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
      values[i] = operation(minimum + i * 
                              (double)(maximum - minimum) / numSteps);
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

/**
* The LighPulse class is a NormExpression which determines the distribution of
* photons in a light pulse
*
* @author  John Lawrence, Jordan Potarf, Andrew Baas
* @version 1.0
* @since   05-07-2017
*/
class LightPulse extends NormExpression{
  public LightPulse(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

  
	protected double operation(double val){
    double t1 = 1.25;
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
