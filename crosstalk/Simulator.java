package simulator;

import java.io.PrintWriter; 
import java.io.IOException;
import java.lang.Math;

/**
 * The Simulator class manages the simulation of a SIPM
 * chip and collects data from the chip.
 *
 * @author  John Lawrence, Jordan Potarf, Andrew Baas
 * @version 1.0
 * @since   05-07-2017
 */

public class Simulator{

  // Arrays for reading out data from the chip
	private StatDist pulseData[];
	private double[] time, current, mean, variance, input, pulse, bin;

  // Simulation required variables
	private int timeShift;
	private Environment e;

  // Simulation objects
	private Pulse p;
	private Sipm chip;
	
  // Expressions for various necessary distributions
	private NormExpression lightPulse, cellCharge, cellProb, cellRecharge;


  /**
   * Constructor modifies the steps per nanosecond which the
   * simulation takes
   *
   * @param granularity Steps per nanosecond
   */
  public Simulator(int numPhotons, Environment e){
    this.e = e;
    
    timeShift = 20;
	
	  // Initialize simulation objects
	  p          = new Pulse((int)numPhotons, e);
	  chip       = new Sipm(p, e);

    // Initialize output data values
	  pulseData = new StatDist[getStepsPerPulse()];
    time      = new double[getStepsPerPulse()];
	  current   = new double[getStepsPerPulse()];
	  mean      = new double[getStepsPerPulse()];
	  variance  = new double[getStepsPerPulse()];
	  bin       = new double[getStepsPerPulse()]; 
	  input     = new double[getStepsPerPulse()];
	  pulse     = new double[getStepsPerPulse()];
	  
    // Initialize and place default values for output
	  for(int i = 0; i < input.length; i++){
      pulseData[i] = new StatDist();
      time[i]   = e.stepToTime(i);
	    input[i]  = e.getLightPulse().get(i) * p.getNum();
	    if( i < e.timeToStep(e.getCellPulseTime())){
	      pulse[i] = e.getCellCharge().get(i);
	    } else {
        pulse[i] = 0;
      }
	  }
	}
	
  /**
   * Takes the simulation through a single step and
   * collects data from the chip.
   */
	public void update(){

    // Steps through simulation time step
	  e.increment();
    chip.update();
	  
    // Collects relevant data
	  double curCharge = chip.getCharge();

    // Take data only for most recent pulse
	  int curStep = e.getStep() % (getStepsPerPulse());
	  
    // Add data to collective data stats object
	  pulseData[curStep].add(curCharge);
	
    // Update the output arrays with the statistical data at the end of each
    // pulse
	  if(!e.isBatchJob() || curStep == (getStepsPerPulse() - 1)){
      for(int i = 0; i < pulseData.length; i++){
        
        // Adjust the array index by the shift factor for the sake of binning
        int index = (i + timeShift + pulseData.length) %pulseData.length;
        
        // Update the output from the stats object
        current[index]  = (double)pulseData[i].getCurrent();
        mean[index]     = (double)pulseData[i].getMean();
        variance[index] = (double)pulseData[i].getVariance();
  	    if(!e.isBatchJob()){
          input[index]    = e.getLightPulse().get(i) * p.getNum();
        }
        // Bin the mean signal in 25 ns segments
        int binTime = e.timeToStep(25);
        if(i % binTime == 0){
          double binTot = 0;
          // Integrate over the last 25 ns
          for(int t = 0; t < binTime; t++){
            binTot += mean[(i - t + pulseData.length) % pulseData.length] / (double)binTime;
          }
          
          // Place the data in the bin array
          for(int t = 0; t < binTime; t++){
            bin[(i - t + pulseData.length) % pulseData.length] = binTot;
          }
        }
      }
    }
	}
	
  /**
   * Resets the statistical data
   */
	public void clearStats(){
	  for(int i = 0; i < pulseData.length; i++){
	    pulseData[i].clear();
	  }
	}

  /**
   * Gets the number of photons in a pulse
   *
   * @return Number of photons in a pulse
   */
  public int getNumPhotons(){
    return p.getNum();
  }

  /**
   * Sets the number of photons in a pulse
   *
   * @param numPhotons Number of photons per pulse
   */
  public void setNumPhotons(int numPhotons){
    p.setNum(numPhotons);
  }

  /**
   * Gets the length of a light pulse in nanoseconds
   *
   * @return Length of light pulse (ns)
   */
  public int getStepsPerPulse(){
    return e.getPulseLen() * e.getStepsPerNs();
  }

  /**
   * Gets the time for each index of the output
   *
   * @return Time for each index
   */
  public double[] getTime(){
    return time;
  }

  /**
   * Gets the current output pulse shape.
   *
   * @return Current pulse shape
   */
  public double[] getCurrent(){
    return current;
  }

  /**
   * Gets the mean output pulse shape, averaged over several runs
   *
   * @return Mean pulse shape
   */
  public double[] getMean(){
    return mean;
  }

  /**
   * Gets the variance of the output pulse shape, determined over several runs
   *
   * @return Variance of the pulse shape
   */
  public double[] getVariance(){
    return variance;
  }

  /**
   * Gets the 25ns binning of the mean output pulse shape, determined over
   * several runs
   *
   * @return Binning (25ns) of the pulse shape
   */
  public double[] getBinning(){
    return bin;
  }

  /**
   * Gets the shape of the input pulse
   *
   * @return Shape of the input pulse
   */
  public double[] getPulseShape(){
    return input;
  }

  /**
   * Gets the individual pixel response output shape
   *
   * @return Shape of the individual pixel response output
   */
  public double[] getPixelShape(){
    return pulse;
  }

  /**
   * Gets the probability of crosstalk between cells
   *
   * @return Probability of crosstalk
   */
  public double getCrossProb(){
    return e.getCrossProb();
  }

  /**
   * Sets the probability of crosstalk between cells
   *
   * @param crossProb Crosstalk probability
   */
  public void setCrossProb(double crossProb){
    e.setCrossProb(crossProb);
  }

  /**
   * Gets the timeshift of the output pulse represented in the binning
   *
   * @return The timeshift used in the binning
   */
  public int getTimeShift(){
    return timeShift;
  }

  /**
   * Sets the timeshift of the output pulse represented in the binning
   *
   * @param timeShift The timeshift to be used in the binning
   */
  public void setTimeShift(int timeShift){
    this.timeShift = timeShift;
  }

  /**
   * Gets the diameter of the SiPM chip (in cells)
   *
   * @return Diameter of the SiPM chip (cells)
   */
  public int getDiameter(){
    return e.getCellDiam();
  }

  /**
   * Gets the normalized charge data from each cell
   *
   * @return Array of normalized charges for each cell
   */
  public double[][] getNormCellCharge(){
    return chip.getNormCellCharge();
  }

  /**
   * Gets whether each cell was activated by the photon pulse 
   *
   * @return Array of boolean values representing whether the cell was
   *           activated by the photon pulse
   */
  public boolean[][] getIsPulse(){
    return chip.getIsPulse();
  }

  /**
   * Gets the current step in the simulation
   *
   * @return The current step number of the simulation
   */
  public int getStep(){
    return e.getStep();
  }

}



