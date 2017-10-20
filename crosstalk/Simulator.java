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
  
  public double Landau(double x, double mu, double sigma) {
    if (sigma <= 0) return 0; 
    Double den = anotherLandau((x-mu)/sigma); 
    return den;
  }
  
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