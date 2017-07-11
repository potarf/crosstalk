package simulator;
import java.lang.Math;

class Environment{
  private int step;
  
  //Simulation constants
  private int stepsPerNs;
  private int pulseLen;
  private int cellDiam;
  
  //Cell constants
  private int riseTime;
  private int cellPulseTime;
  private double crossProb;

  public Environment(int stepsPerNs, int pulseLen, int cellDiam, int riseTime, int cellPulseTime, double crossProb){
    this.stepsPerNs = stepsPerNs;
    this.pulseLen   = pulseLen;
    this.cellDiam   = cellDiam;
    this.riseTime   = riseTime;
    this.cellPulseTime   = cellPulseTime;
    this.crossProb  = crossProb;

    step = 0;
  }

  public int increment(){
    step++;
    return step;
  }

  public int getStep(){
    return step;
  }

  public double getTime(){
    return step / (double)(stepsPerNs);
  }

  public int pulseNum(){
    return (step / stepsPerNs) / pulseLen;
  }
  
  public double getCrossProb(){
    return crossProb;
  }

  public void setCrossProb(double crossProb){
    this.crossProb = crossProb;
  }

  public int getStepsPerNs(){
    return stepsPerNs;
  }

  public int getPulseLen(){
    return pulseLen;
  }

  public int getCellDiam(){
    return cellDiam;
  }

  public int getRiseTime(){
    return riseTime;
  }

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
}

