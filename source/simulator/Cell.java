package simulator;

import java.lang.Math;

/**
* The Cell class manages the charge and response of each individual SIPM cell 
* at each step of the simulation.
*
* @author  John Lawrence, Jordan Potarf, Andrew Baas
* @version 1.0
* @since   05-07-2017
*/
class Cell{

  // Parent environment reference
  private Environment e;
  private boolean valid;    

  // Distributions for cell operations
  private NormExpression charge;
  private NormExpression probability;
  private NormExpression recharge;

  // Storage for historical cell pulses
  private int actNum;
  private int[] actStep;
  private double[] actCharge;

  // Status variables for current activation
  private boolean isPulse;  // Whether the activation was due to a photon pulse
  private double up, right, down, left; // Probability for activating each
                                        //   neighbor
  /**
   * Constructor takes environment data used for the simulation
   *
   * @param valid       Whether the cell should be evaluated as a cell
   * @param charge      Distribution of the output charge over time
   * @param probability Distribution of the probability of neighbor activation
   * @param recharge    Distribution of the contained charge after a pulse
   * @param e           Environment in which the cell exists 
   */
  public Cell(boolean valid, NormExpression charge, NormExpression probability,
                NormExpression recharge, Environment e){

    // Update class data
    this.valid = valid;
    this.charge = charge;
    this.recharge = recharge;
    this.probability = probability;
    this.e = e;

    // Define and initialize pulse history data
    actNum = 0;
      // Note: historical pulses have no effect after their pulse time is up,
      //         so storing them is redundant.
    actStep   = new int   [e.getCellPulseTime() / e.getRiseTime() + 1];
    actCharge = new double[e.getCellPulseTime() / e.getRiseTime() + 1];

    for(int i = 0; i < actStep.length; i++){
      actStep[i] = -1;
      actCharge[i] = 0;
    }
  }
  
  /**
   * Attempts to activate the cell, fails if the cell is invalid or in its
   * deadtime
   *
   * @param isPulse Whether the activation comes from a photon pulse
   * @return Whether the cell was successfully activated
   */
  public boolean activate(boolean isPulse){
    
    // Check if cell is currently in deadtime
    if(curStep() < getDeadSteps()){
      return false;
    }

    // Update whether activation is from pulse
    this.isPulse = isPulse;

    // Set probabilities for neighbor activation
    up    = ((double)Math.random() * 1.0f);
    right = ((double)Math.random() * 1.0f);
    down  = ((double)Math.random() * 1.0f);
    left  = ((double)Math.random() * 1.0f);

    // Add this activation to the histories (overwrite unnecessary histories)
    actStep[actNum % actStep.length] = e.getStep() + 1;
    actCharge[actNum % actStep.length] = (double)getRecharge(); 
    actNum = actNum + 1;
    return true;
  }

  /**
   * Updates neighbor of cell based on probabilities
   *
   * @param isPulse Whether the activation comes from a photon pulse
   * @return Whether the cell was successfully activated
   */ 
  public void updateNeighbors(Cell[][] cells, int x, int y){
    
    // The probability of a neighbor being active is determined at the
    // activation of the Cell. This checks to see if the neighbor will be
    // activated at the current time-step

    if(curStep() < getDeadSteps()){
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

  /**
   * Gets the number of steps during which a cell is unable to be activated
   *
   * @return The number of steps during which a cell cannot be activated
   */
  public int getDeadSteps(){
    return e.timeToStep(e.getRiseTime());
  }

  /**
   * Gets whether the cell was activated by the light pulse
   *
   * @return Whether the cell was activated by a light pulse
   */
  public boolean getIsPulse(){
    return isPulse;
  }

  /**
   * Gets whether the cell is able to be activated
   *
   * @return Whether the cell is able to be activated
   */
  public boolean isActivated(){
    return curStep() < getDeadSteps();
  }

  /**
   * Gets whether the cell should be evaluated as a cell 
   *
   * @return Whether the cell should be evaluated as a cell
   */
  public boolean isValid(){
    return valid;
  }

  /**
   * Gets the probability of the cell being activated by the current time
   *
   * @return The probability of a neighbor being activated by this time
   */
  public double getProb(){
    return probability.get(curStep()) * curCharge() * e.getCrossProb();
  }

  /**
   * Gets the total ouput charge from this cell
   *
   * @return Total output charge from this cell
   */
  public double getCharge(){
    double total = 0;

    // Sum over all contributing activations and their respective charges
    for(int i = 0; i < actStep.length; i++){
      if(actStep[i] != -1){
        total += charge.get(e.getStep() - actStep[i]) * actCharge[i];
      }
    }
    return total;
  }

  /**
   * Gets the current charge the cell contains
   *
   * @return Gets the current charge contained by the cell
   */
  public double getRecharge(){
    return recharge.get(curStep());
  } 

  /**
   * Gets the number of steps since the last activation
   *
   * @return Number of steps since the last activation
   */
  public int curStep(){
    if(actNum == 0){
      return e.timeToStep(e.getCellPulseTime()) + 1;
    }
    return e.getStep() - actStep[(actNum - 1) % actStep.length];
  }

  /**
   * Charge which relates to the latest cell activation
   *
   * @return Charge of the cell at the moment of the last activation
   */
  public double curCharge(){
    if(actNum == 0){
      return 0;
    }
    return actCharge[(actNum - 1) % actStep.length];
  }
}

