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
  
  /**
  * This is a function that is half of a landau function
  */
  public double anotherLandau(double x) {
    double xi = 1;
    double xj = 0;    
    final double p1[] = {0.4259894875,-0.1249762550, 0.03984243700, -0.006298287635,   0.001511162253};
    final double q1[] = {1.0         ,-0.3388260629, 0.09594393323, -0.01608042283,    0.003778942063};
   
    final double p2[] = {0.1788541609, 0.1173957403, 0.01488850518, -0.001394989411,   0.0001283617211};
    final double q2[] = {1.0         , 0.7428795082, 0.3153932961,   0.06694219548,    0.008790609714};
   
    final double p3[] = {0.1788544503, 0.09359161662,0.006325387654, 0.00006611667319,-0.000002031049101};
    final double q3[] = {1.0         , 0.6097809921, 0.2560616665,   0.04746722384,    0.006957301675};
   
    final double p4[] = {0.9874054407, 118.6723273,  849.2794360,   -743.7792444,      427.0262186};
    final double q4[] = {1.0         , 106.8615961,  337.6496214,    2016.712389,      1597.063511};
   
    final double p5[] = {1.003675074,  167.5702434,  4789.711289,    21217.86767,     -22324.94910};
    final double q5[] = {1.0         , 156.9424537,  3745.310488,    9834.698876,      66924.28357};
   
    final double p6[] = {1.000827619,  664.9143136,  62972.92665,    475554.6998,     -5743609.109};
    final double q6[] = {1.0         , 651.4101098,  56974.73333,    165917.4725,     -2815759.939};
  
    final double a1[] = {0.04166666667,-0.01996527778, 0.02709538966};
   
    final double a2[] = {-1.845568670,-4.284640743};
  
    if (xi <= 0) return 0;
    double v = (x - xj)/xi;
    double u, ue, us, denlan;
    if (v < -5.5) {
      u = Math.exp(v+1.0);
      if (u < 1e-10) return 0.0;
      ue = Math.exp(-1/u);
      us  = Math.sqrt(u);
      denlan = 0.3989422803*(ue/us)*(1+(a1[0]+(a1[1]+a1[2]*u)*u)*u);
    } else if(v < -1) {
      u = Math.exp(-v-1);
      denlan = Math.exp(-u)*Math.sqrt(u)*(p1[0]+(p1[1]+(p1[2]+(p1[3]+p1[4]*v)*v)*v)*v)/(q1[0]+(q1[1]+(q1[2]+(q1[3]+q1[4]*v)*v)*v)*v);
    } else if(v < 1) {
      denlan = (p2[0]+(p2[1]+(p2[2]+(p2[3]+p2[4]*v)*v)*v)*v)/(q2[0]+(q2[1]+(q2[2]+(q2[3]+q2[4]*v)*v)*v)*v);
    } else if(v < 5) {
      denlan = (p3[0]+(p3[1]+(p3[2]+(p3[3]+p3[4]*v)*v)*v)*v)/(q3[0]+(q3[1]+(q3[2]+(q3[3]+q3[4]*v)*v)*v)*v);
    } else if(v < 12) {
      u   = 1/v;
      denlan = u*u*(p4[0]+(p4[1]+(p4[2]+(p4[3]+p4[4]*u)*u)*u)*u)/(q4[0]+(q4[1]+(q4[2]+(q4[3]+q4[4]*u)*u)*u)*u);
    } else if(v < 50) {
      u   = 1/v;
      denlan = u*u*(p5[0]+(p5[1]+(p5[2]+(p5[3]+p5[4]*u)*u)*u)*u)/(q5[0]+(q5[1]+(q5[2]+(q5[3]+q5[4]*u)*u)*u)*u);
    } else if(v < 300) {
      u   = 1/v;
      denlan = u*u*(p6[0]+(p6[1]+(p6[2]+(p6[3]+p6[4]*u)*u)*u)*u)/(q6[0]+(q6[1]+(q6[2]+(q6[3]+q6[4]*u)*u)*u)*u);
    } else {
      u   = 1/(v-v*Math.log(v)/(v+1));
      denlan = u*u*(1+(a2[0]+a2[1]*u)*u);
    }
    return denlan/xi;
  }
  
  /**
  * This function is a landau function using the function above
  */
  public double Landau(double x, double mu, double sigma) {
    if (sigma <= 0) return 0; 
    Double den = anotherLandau((x-mu)/sigma); 
    return den;
  }
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

  /**
  * This is the mathmatical representation of the light pulse
  */
	protected double operation(double val){
   double A,n,t0,fit;
   //normalization constant
   A=0.104204;
   //first exponential constant
   n=0.44064;
   //second exponential constant
   t0=10.0186;
   //exponential constant that is dominate for the first part of the light pulse
   fit = A*(1-Math.exp(-val/n))*Math.exp(-val/t0);
   double norm,mpv,sigma,corTerm;
   //normalization contant
   norm=0.0806123; 
   //mean for landau function
   mpv=0; 
   //standard deviation for landau function
   sigma=20;
   //Landau function which is dominant for the tail part of the light pulse
   corTerm = norm*Landau(val,mpv,sigma);
   //fractional constant which normalizes overall function
   //and weights each individual function
   double frac = 0.13;
   double t = (1-frac)*fit + frac*corTerm;
   if(val >= 0) return t;
   else return 0.0;
  }
}