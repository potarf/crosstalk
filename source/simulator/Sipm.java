package simulator;
import java.lang.Math;

/**
 * The SiPM class manages the collection of {@link simulator.Cell}s which make 
 * up a SiPM by facilitating the interactions between the photon 
 * {@link simulator.Pulse} and the {@link simulator.Cell}s and the interactions
 * between {@link simulator.Cell}s due to crosstalk.
 *
 * @author  John Lawrence, Jordan Potarf, Andrew Baas
 * @version 1.0
 * @since   05-07-2017
 */

class Sipm{
  
  // Simulation environmental data
  Cell cells[][];
  Pulse p;
  Environment e;

  // SiPM chip attributes
  private int diameter;

  // Cell attribute
  private NormExpression cellCharge;

  /**
   * Constructor generates and initializes a SiPM from the given arguments
   *
   * @param diameter      Diameter (cells) of the SiPM chip
   * @param p             Input photon pulse
   * @param cellCharge    Distribution of a {@link simulation.Cell}'s output 
   *                        charge over the time after last fired.
   * @param cellProb      Distribution of the crosstalk probability over a 
   *                        {@link simulation.Cell}'s risetime
   * @param cellRecharge  Distribution of a {@link simulation.Cell}'s charge
   *                        over the time after last fired.
   */
  public Sipm(Pulse p, Environment e){
    
    // Set necessary values
    this.cellCharge = e.getCellCharge();
    this.diameter = e.getCellDiam();
    this.p = p;
    this.e = e;
  
    // Allocate space for SiPM chip
    cells = new Cell[diameter][diameter];
  
    // Initialize cells so that any outside of the circle are invalid
    double center = (diameter - 1) / 2.0f;
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        boolean inCircle = ((x - center) * (x - center) + 
                            (y - center) * (y - center) - 
                            diameter * diameter /  4.0f)
                            <= 0;
        cells[x][y] = new Cell(inCircle, e);
      }
    } 
  }
  
  /**
  * Takes the {@link simulator.Cell} array through a single step, including 
  * pulse and crosstalk
  */
  public void update(){
    
    // Apply a photon pulse
    p.pulse(cells);
    
    if(e.getCrosstalk()){
      // Apply crosstalk to each cell
      for(int x = 0; x < diameter; x++){
        for(int y = 0; y < diameter; y++){
          if(cells[x][y].isValid()){
            cells[x][y].updateNeighbors(cells, x, y);
          }
        }
      }
    }
  }

  /**
  * Gets the total output charge from the chip
  *
  * @return Total output charge
  */
  public double getCharge(){

    // Sum the output charge from every cell
    double total = 0;
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        total += cells[x][y].getCharge();
      }
    }
    return total;
  }

  /**
  * Gets the normalized output charge from each cell
  *
  * @return Normalized charge from each cell
  */
  public double[][] getNormCellCharge(){

    double charges[][] = new double[diameter][diameter];

    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        charges[x][y] = cells[x][y].getCharge() / 
                        cellCharge.get(cells[x][y].getDeadSteps());
      }
    }
    return charges;
  }

  /**
  * Gets whether each cell is activated by a pulse
  *
  * @return Whether each cell is activated by a pulse
  */
  public boolean[][] getIsPulse(){

    boolean isPulses[][] = new boolean[diameter][diameter];

    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        isPulses[x][y] = cells[x][y].getIsPulse();
      }
    }
    return isPulses;
  }

  /**
  * Gets the diameter of the SiPM chip
  *
  * @return SiPM diameter
  */
  public int getDiameter(){
    return diameter;
  }
}