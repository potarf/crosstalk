package simulator;

import java.lang.Math;

/**
* The Pulse class acts on an array of Cells, and simmulates the photon pulse 
* which the SiPM recieves
*
* @author  John Lawrence, Jordan Potarf, Andrew Baas
* @version 1.0
* @since   05-07-2017
*/
class Pulse{
  
  // Pulse data
  int numPhotons;
  int startStep;
  NormExpression shape;
  Environment e;

  // Information for partial photons
  double remainder;

  /**
  * Constructor takes general pulse data
  *
  * @param numPhotons Number of photons
  * @param shape      Distribution of photons in the pulse
  * @param e          The environment of the simulation
  */ 
  public Pulse(int numPhotons, Environment e){
    this.e = e;
    this.numPhotons = numPhotons;
    startStep = e.getStep();
    this.shape = e.getLightPulse();
    remainder = 0;
  }

  /**
  * Applies one step of the pulse to an array of Cells 
  *
  * @param cells Array of cells to apply the pulse to
  */
  public void pulse(Cell[][] cells){

    // Current step inside the current pulse
    int curStep = (e.getStep() - startStep) % (e.timeToStep(e.getPulseLen()));

    // Current pulse data
    int diameter = cells.length;
    double actPhot = numPhotons * shape.get(curStep) + remainder;
    int photons = (int)actPhot;
    remainder = actPhot - photons;

    // Randomly activate valid pixels
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

  /**
  * Sets the number of photons in a pulse
  *
  * @param number Number of photons in a pulse
  */
  public void setNum(int number){
    this.numPhotons = number;
  }

  /**
  * Gets the number of photons in a pulse
  *
  * @return Number of photons in a pulse
  */
  public int getNum(){
    return numPhotons;
  }
}