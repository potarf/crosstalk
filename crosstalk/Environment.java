package simulator;

import java.lang.Math;

/**
* The Environment class stores all values which need to be accessed by multiple
* independent objects in the simulation
*
* @author  John Lawrence, Jordan Potarf, Andrew Baas
* @version 1.0
* @since   05-07-2017
*/
public class Environment{
  private int step;
  
  //Simulation constants
  private int stepsPerNs;
  private int pulseLen;
  private int cellDiam;
  
  //Cell constants
  private double  riseTime;
  private int     cellPulseTime;
  private double  crossProb;

  //Distributions
  private NormExpression lightPulse;
  private NormExpression cellCharge;
  private NormExpression cellProb;
  private NormExpression cellRecharge;

  //Distribution constants
  private double t1;
  private double t2;
  private double t3;
  private double t4;
  private double t5;

  //Effect flags
  private boolean saturation;
  private boolean crosstalk;
  private boolean batchJob;
  

  /**
  * Constructor takes shared environment data
  *
  * @param stepsPerNs Number of simulation steps which happen in one nanosecond
  * @param pulseLen   Amount of time (ns) between repeated pulses  
  * @param cellDiam   Diameter (cells) of the chip
  * @param riseTime   Amount of time (ns) after an activation when a cell cannot
  *                     be activated again
  * @param cellPulseTime  Amount of time a cell's output pulse takes
  * @param crossProb      Probability of crosstalk between a single cell and a
  *                         single neighbor
  */
  public Environment(int stepsPerNs, int numCells,
                     int pulseLen, int cellPulseTime,
                     double crossProb,
                     double t1, double t2, double t3, double t4, double t5, double tRc,
                     boolean saturation, boolean crosstalk, boolean batchJob){
    
    // Initialize all environment variables
    this.stepsPerNs     = stepsPerNs;
    this.pulseLen       = pulseLen;
    this.cellDiam       = (int)Math.sqrt(numCells / Math.PI) * 2;
    this.riseTime       = (t2 > 1.0 / stepsPerNs)?t2:(1.0/stepsPerNs);
    this.cellPulseTime  = cellPulseTime;
    this.crossProb      = crossProb;
    this.saturation     = saturation;
    this.crosstalk      = crosstalk;
    this.batchJob       = batchJob;
 
    // Set up necessary distributions
	  this.lightPulse   = new LightPulse(     0, getPulseLen(),
                                        timeToStep(getPulseLen()));
	  this.cellCharge   = new CellCharge(     0, getCellPulseTime(),
                                        timeToStep(getCellPulseTime()), t1,
                                        t2, t3, t4, t5);
	  this.cellProb     = new CellProbability(0, (int)(getRiseTime() + 1),
                                        timeToStep(getRiseTime()), t1, t2);
	  this.cellRecharge = new CellRecharge(   0, getCellPulseTime(),
                                        timeToStep(getCellPulseTime()), t1, tRc);

    // Set the simulation time to 0
    step = 0;
  }

  /**
  * Increments the simulation by one step
  */
  public int increment(){
    step++;
    return step;
  }

  /**
  * Gets the current simulation step
  * 
  * @return Current simulation step
  */
  public int getStep(){
    return step;
  }

  /**
  * Gets the current simulation time (ns)
  * 
  * @return Current simulation time
  */
  public double getTime(){
    return step / (double)(stepsPerNs);
  }

  /**
  * Gets the number of pulses which have occured in the simulation
  * 
  * @return Number of pulses simulated
  */
  public int pulseNum(){
    return (step / stepsPerNs) / pulseLen;
  }

  /**
  * Gets the probability of crosstalk between neighboring cells
  * 
  * @return Probability of crosstalk
  */
  public double getCrossProb(){
    return crossProb;
  }

  /**
  * Sets the probability of crosstalk between neighboring cells
  * 
  * @param crossProb Probability of crosstalk
  */
  public void setCrossProb(double crossProb){
    this.crossProb = crossProb;
  }

  /**
  * Gets the number of simulation steps per nanosecond
  * 
  * @return Simulation steps per nanosecond
  */
  public int getStepsPerNs(){
    return stepsPerNs;
  }

  /**
  * Gets the time(ns) between light pulses
  * 
  * @return Time between light pulses
  */
  public int getPulseLen(){
    return pulseLen;
  }

  /**
  * Gets the diameter of the chip in units of cells
  * 
  * @return Diameter of the chip
  */
  public int getCellDiam(){
    return cellDiam;
  }

  /**
  * Gets the time after activation where a cell cannot be activated
  * 
  * @return Time after activation where a cell cannot be activated
  */
  public double getRiseTime(){
    return riseTime;
  }

  /**
  * Gets the time it takes for a single cell to completely fire
  * 
  * @return Time (ns) of a single cell firing
  */
  public int getCellPulseTime(){
    return cellPulseTime;
  }

  /**
   * Converts from the time to the corresponding step number
   *
   * @param time Time (ns) to convert
   * @return Corresponding step number 
   */  
  public int timeToStep(double time){
    return (int)(time * stepsPerNs);
  }
 
  /**
   * Converts from the step number to the appropriate time
   *
   * @param step Step number to convert
   * @return Time at which the step is executed (ns)
   */
  public double stepToTime(int step){
    return step / (float)stepsPerNs;
  }

  public NormExpression getLightPulse(){
    return lightPulse;
  }

  public NormExpression getCellCharge(){
    return cellCharge;
  }

  public NormExpression getCellProb(){
    return cellProb;
  }

  public NormExpression getCellRecharge(){
    return cellRecharge;
  }

  public boolean getCrosstalk(){
    return crosstalk;
  }

  public boolean getSaturation(){
    return saturation;
  }

  public boolean isBatchJob(){
    return batchJob;
  }
}