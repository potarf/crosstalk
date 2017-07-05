package simulator;

import java.io.PrintWriter; 
import java.io.IOException;
import java.lang.Math;

public class Simulator{
	
	  // Global Constants
	static final int SIM_DIAM = 550;
	PrintWriter output = null;
	
	Sipm chip;
	StatDist pulseData[];
	double[] current, mean, variance, input, pulse, bin;
  int granularity;
	
	// Plotter interactive variables
	int numPhotons;
	int timeShift;
	
	NormExpression gauss, cellCharge, cellProb, cellRecharge;
	Pulse p;
	Environment e;

  public Simulator(int granularity){
    this.granularity = granularity;
  }
	
	public void initValues(int numPhotons){
	  this.numPhotons = numPhotons;
	  e = new Environment(); 
      e.STEPS_PER_NS = granularity;
	  timeShift       = 20;
	
	  //gauss = new GaussianIntNorm(e.STEPS_PER_NS, timeShift, 0, e.PULSE_LEN,e.PULSE_LEN * e.STEPS_PER_NS);
	  gauss = new LightPulse(0, e.PULSE_LEN,e.PULSE_LEN * e.STEPS_PER_NS);
	  cellCharge = new CellCharge(0, e.DEAD_TIME, e.DEAD_TIME * e.STEPS_PER_NS);
	  cellProb = new CellProbability(0, e.RISE_TIME, e.RISE_TIME * e.STEPS_PER_NS);
	  cellRecharge = new CellRecharge(0, e.DEAD_TIME, e.DEAD_TIME * e.STEPS_PER_NS);
	
	
	  //Initialize data values
	  p         = new Pulse((int)numPhotons, gauss, e);
	  chip      = new Sipm(e.CELL_DIAM, p, cellCharge, cellProb, cellRecharge, e);
	  pulseData = new StatDist[e.PULSE_LEN * e.STEPS_PER_NS];
	  for(int i = 0; i < pulseData.length; i++){
	    pulseData[i] = new StatDist();
	  }
	
	  current   = new double[e.PULSE_LEN * e.STEPS_PER_NS];
	  mean      = new double[e.PULSE_LEN * e.STEPS_PER_NS];
	  variance  = new double[e.PULSE_LEN * e.STEPS_PER_NS];
	  bin       = new double[e.PULSE_LEN * e.STEPS_PER_NS]; 
	  
	  input = new double[e.PULSE_LEN * e.STEPS_PER_NS];
	  pulse = new double[e.PULSE_LEN * e.STEPS_PER_NS];
	  
	  for(int i = 0; i < input.length; i++){
	    input[i] =gauss.get(i) * p.getNum();
	    if( i < e.DEAD_TIME * e.STEPS_PER_NS){
	      pulse[i] = cellCharge.get(i);
	    } else {
        pulse[i] = 0;
      }
	  }
	}
	
	public void update(){
	  e.increment();
	  chip.update();
	  
	  double curCharge = chip.getCharge();
	  int curStep = e.getStep() % (e.PULSE_LEN * e.STEPS_PER_NS);
	
	  pulseData[curStep].add(curCharge);
	
	  for(int i = 0; i < pulseData.length; i++){
	    current[(i + timeShift + pulseData.length) %pulseData.length]  = (double)pulseData[i].getCurrent();
	    mean[(i + timeShift + pulseData.length) %pulseData.length]     = (double)pulseData[i].getMean();
	    variance[(i + timeShift + pulseData.length) %pulseData.length] = (double)pulseData[i].getVariance();
	    input[(i + timeShift + pulseData.length) %pulseData.length] = gauss.get(i) * p.getNum();
	    if( i < e.DEAD_TIME * e.STEPS_PER_NS){
	      pulse[i] = cellCharge.get(i);
	    }
	    int binTime = 25 * e.STEPS_PER_NS;
	    if(i % binTime == 0){
	      double binTot = 0;
	      for(int t = 0; t < binTime; t++){
	        binTot += mean[(i - t + pulseData.length) % pulseData.length] / (double)binTime;
	      }
	      for(int t = 0; t < binTime; t++){
	        bin[(i - t + pulseData.length) % pulseData.length] = binTot;
	      }
	    }
	
	  }
	}
	
	public void clearStats(){
	  for(int i = 0; i < pulseData.length; i++){
	    pulseData[i].clear();
	  }
	}

  public int getNumPhotons(){
    return numPhotons;
  }

  public void setNumPhotons(int numPhotons){
    p.setNum(numPhotons);
    this.numPhotons = numPhotons;
  }

  public int getStepsPerPulse(){
    return e.PULSE_LEN * e.STEPS_PER_NS;
  }
  
	public double stepToTime(int step){
    return step / (float)e.STEPS_PER_NS;
  }

  public double[] getMean(){
    return mean;
  }

  public double[] getVariance(){
    return variance;
  }

  public double[] getBinning(){
    return bin;
  }

  public double[] getPulseShape(){
    return input;
  }

  public double getCrossProb(){
    return e.getCrossProb();
  }

  public void setCrossProb(double crossProb){
    e.setCrossProb(crossProb);
  }

  public int getTimeShift(){
    return timeShift;
  }

  public void setTimeShift(int timeShift){
    this.timeShift = timeShift;
  }

  public int getDiameter(){
    return e.CELL_DIAM;
  }

  public double[][] getNormCellCharge(){
    return chip.getNormCellCharge();
  }

  public boolean[][] getIsPulse(){
    return chip.getIsPulse();
  }

  public int getStep(){
    return e.getStep();
  }

}

class Cell{

  private int[] actStep;
  private double[] actCharge;
  private int actNum;
  private int deadSteps;
  private boolean valid;
  private boolean active;
  private boolean isPulse;
  private double up, right, down, left;
  private Environment e;
  private NormExpression charge;
  private NormExpression probability;
  private NormExpression recharge;

  public Cell(boolean valid, NormExpression charge, NormExpression probability, NormExpression recharge, Environment e){
    this.deadSteps = e.RISE_TIME * e.STEPS_PER_NS;
    this.valid = valid;
    this.charge = charge;
    this.recharge = recharge;
    this.probability = probability;
    this.e = e;

    actStep = new int[e.DEAD_TIME / e.RISE_TIME + 1];
    actCharge = new double[e.DEAD_TIME / e.RISE_TIME + 1];

    for(int i = 0; i < actStep.length; i++){
      actStep[i] = -1;
      actCharge[i] = 0;
    }
    actNum = 0;
  }

  public boolean activate(boolean isPulse){
    if(curStep() < deadSteps){
      return false;
    }
    up    = ((double)Math.random() * 1.0f);
    right = ((double)Math.random() * 1.0f);
    down  = ((double)Math.random() * 1.0f);
    left  = ((double)Math.random() * 1.0f);
    actStep[actNum % actStep.length] = e.getStep() + 1;
    actCharge[actNum % actStep.length] = (double)getRecharge(); 
    actNum = actNum + 1;
    this.isPulse = isPulse;
    return true;
  }

  public void updateNeighbors(Cell[][] cells, int x, int y){
    if(curStep() < deadSteps){
      double spreadProb = this.getProb(); 
      if(x - 1 > 0 && cells[x - 1][y].isValid()){ 
        if(up < spreadProb)
          cells[x - 1][y].activate(false);
      }
    
      if(x + 1 < cells.length && cells[x + 1][y].isValid()){ 
        if(down < spreadProb)
          cells[x + 1][y].activate(false);
      }
    
      if(y - 1 > 0 && cells[x][y - 1].isValid()){ 
        if(left < spreadProb)
          cells[x][y - 1].activate(false);
      }
    
      if(y + 1 < cells.length && cells[x][y + 1].isValid()){ 
        if(right < spreadProb)
          cells[x][y + 1].activate(false);
      }
    }
  }

  public int getDeadSteps(){
    return deadSteps;
  }

  public boolean getIsPulse(){
    return isPulse;
  }

  public boolean isActivated(){
    return curStep() < deadSteps;
  }

  public boolean isValid(){
    return valid;
  }

  public double getProb(){
    return probability.get(curStep()) * curCharge() * e.getCrossProb();
  }

  public double getCharge(){
    double total = 0;
    for(int i = 0; i < actStep.length; i++){
      if(actStep[i] != -1){
        total += charge.get(e.getStep() - actStep[i]) * actCharge[i];
      }
    }
    return total;
  }

  public double getRecharge(){
    return recharge.get(curStep());
  }

  public double getLife(){
    return (1 - curStep() / (double) (e.DEAD_TIME * e.STEPS_PER_NS));
  }

  public int curStep(){
    if(actNum == 0){
      return e.DEAD_TIME * e.STEPS_PER_NS + 1;
    }
    return e.getStep() - actStep[(actNum - 1) % actStep.length];
  }

  public double curCharge(){
    if(actNum == 0){
      return 0;
    }
    return actCharge[(actNum - 1) % actStep.length];
  }


/*  void reset(){
    actStep = -1 * deadSteps;;
  }*/


}
class Environment{
  private int step;
  
  //Simulation constants
  public int STEPS_PER_NS = 5;
  public final int PULSE_LEN = 150;
  public final int CELL_DIAM = 195;
  
  //Cell constants
  public final int RISE_TIME = 4;
  public final int DEAD_TIME = 40;
  private double crossProb = .046f;

  public void Evironment(){
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
    return step / (double)(STEPS_PER_NS);
  }

  public int pulseNum(){
    return (step / STEPS_PER_NS) / PULSE_LEN;
  }
  
  public double getCrossProb(){
    return crossProb;
  }

  public void setCrossProb(double crossProb){
    this.crossProb = crossProb;
  }
}
abstract class NormExpression{

  double[] values;
  int minimum;
  int maximum;
  int numSteps;

  public NormExpression(int minimum, int maximum, int numSteps){
    this.minimum = minimum;
    this.maximum = maximum;
    this.numSteps = numSteps;
    values = new double[numSteps];
  }

  protected void updateValues(){
    double total = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = operation(minimum + i * (double)(maximum - minimum) / numSteps); 
      total += values[i];
    }
    for(int i = 0; i < values.length; i++){
      values[i] /= total;
    }
  }

  public double get(int step){
    if(step >= 0 && step < values.length)
      return values[step];
    return 0;
  }
  
  public abstract double operation(double val);
}

class GaussianIntNorm extends NormExpression{
  double sigma;
  double mean;
  
  public GaussianIntNorm(double sigma, double mean, int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    this.sigma = sigma;
    this.mean = mean;
    updateValues();
  }

  public double operation(double val){
    return 1/Math.sqrt(2 * sigma * sigma * Math.PI) * Math.exp(-Math.pow(val - mean, 2) / (2 * Math.pow(sigma, 2)));
  }

  public void setSigma(double sigma){
    if(this.sigma != sigma){
      updateValues();
      this.sigma = sigma;
    }
  }

  public void setMean(double mean){
    if(this.mean != mean){
      updateValues();
      this.mean = mean;
    }
  }
}

class CellCharge extends NormExpression{

  public CellCharge(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

	public double operation(double  val) {
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

  public double operation(double val){
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

  
	public double operation(double val){
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

  
	public double operation(double val){
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
   		return (1 - Math.exp(-t2/t1))*(Math.exp(-(t4 - t2)/t3)) * (Math.exp(-(val - t4)/t5));
    }
    return (1 - Math.exp(-t2/t1))*(Math.exp(-(t4 - t2)/t3)) * (Math.exp(-(t6 - t4)/t5)) * Math.exp(-(val - t6)/t7);
  }
}

class Pulse{
  int numPhotons;
  int startStep;
  double remainder;
  NormExpression shape;
  Environment e;

  public Pulse(int numPhotons, NormExpression shape, Environment e){
    this.e = e;
    this.numPhotons = numPhotons;
    startStep = e.getStep();
    this.shape = shape;
    remainder = 0;
  }

  public void pulse(Cell[][] cells){
    int diameter = cells.length;
    int curStep = (e.getStep() - startStep) % (e.PULSE_LEN * e.STEPS_PER_NS);
    double actPhot = numPhotons * shape.get(curStep) + remainder;
    int photons = (int)actPhot;
    remainder = actPhot - photons;

    for(int i = 0; i < photons; i++){
      int x = (int)((double)Math.random() * diameter);
      int y = (int)((double)Math.random() * diameter);
      if(!cells[x][y].isValid()){
        i--;
      } else {
        cells[x][y].activate(true);
      }
    }

  }

  public void setNum(int number){
    this.numPhotons = number;
  }

  public int getNum(){
    return numPhotons;
  }

}

class Sipm{
  Cell cells[][];
  Pulse p;
  Environment e;

  private int diameter;

  private int numActive;
  private double curCharge;
  private NormExpression cellCharge;

  public Sipm(int diameter, Pulse p, NormExpression cellCharge, NormExpression cellProb, NormExpression cellRecharge, Environment e){
    this.cellCharge = cellCharge;
    this.diameter = diameter;
    this.p = p;
    this.e = e;

    cells = new Cell[diameter][diameter];
    
    double center = (diameter - 1) / 2.0f;

    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        boolean inCircle = (x - center) * (x - center) + (y - center) * (y - center) - diameter * diameter /  4.0f <= 0;
        cells[x][y] = new Cell(inCircle, cellCharge, cellProb, cellRecharge, e);
      }
    }

  }

  public void update(){
    p.pulse(cells);
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        if(cells[x][y].isValid()){
          cells[x][y].updateNeighbors(cells, x, y);
        }
      }
    }
  }

  public double getCharge(){
    double total = 0;
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        total += cells[x][y].getCharge();
      }
    }
    return total;
  }

  public double[][] getNormCellCharge(){

    double charges[][] = new double[diameter][diameter];

    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        charges[x][y] = cells[x][y].getCharge() / cellCharge.get(cells[x][y].getDeadSteps());
      }
    }
    return charges;
  }

  public boolean[][] getIsPulse(){

    boolean isPulses[][] = new boolean[diameter][diameter];

    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        isPulses[x][y] = cells[x][y].getIsPulse();
      }
    }
    return isPulses;
  }

  public int getDiameter(){
    return diameter;
  }
}

class StatDist{
  private double current;
  private double mean;
  private double sum;
  private double sqSum;
  private double variance;
  private int size;

  public StatDist(){
    current = mean = sum = sqSum = variance = size = 0;
  }

  public void add(double value){
    size++;
    current = value;

    sum += current;
    mean = sum / size;
    
    sqSum += current * current;

    variance = (sqSum - 2 * mean * sum + size * mean * mean) / size;
  }

  public double getCurrent(){
    return current;
  }

  public double getMean(){
    return mean;
  }

  public double getVariance(){
    return variance;
  }

  public void clear(){
    current = mean = sum = sqSum = variance = size = 0;
  }
}
