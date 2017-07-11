import java.util.Formatter;
import simulator.Simulator;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.Math;

/**
* The Main class automates collection of data from the SiPM simulation and
* stores it in well formatted text files
*
* @author  John Lawrence, Jordan Potarf, Andrew Baas
* @version 1.0
* @since   05-07-2017
*/
public class Main{

  /**
  * Main simulates the SiPM and facilitates data collection
  *
  * @param min  Minimum number of photons per pulse in data collection
  * @param max  Maximum number of photons per pulse in data collection
  * @param step Difference of number of photons between each data collection
  * 
  * @param numRuns      Number of runs each data collection averages over
  * @param granularity  Steps per nanosecond of each data run
  */
	public static void main(String[] args){
    
    // Get values from terminal arguments
    int min           = Integer.valueOf(args[0]);
    int max           = Integer.valueOf(args[1]);
    int step          = Integer.valueOf(args[2]);
    int numRuns       = Integer.valueOf(args[3]);
    int granularity   = Integer.valueOf(args[4]);
    
    // Get output directory
    String directory  = "";
    if(args.length > 5){
        directory = args[5];
    }

    // Define the simulator and the output
    Simulator sim;
    Formatter output = null;
	  
    // Iterate through each data collection
		for(int i = min; i <= max; i += step){
      
      // Prepare respective output file
      int offset = (int)(Math.log10(max)) + 1;
      String filename = String.format("%0" + offset + "d_phot.dat", i);
      try{
        output = new Formatter( directory + filename, "UTF-8");
      } catch (IOException e) {
        System.out.println("File cannot open");
      }
      
      // Output first information line
      output.format("%d 3 time(ns) charge%n", i);

      // Initialize the appropriate simulator for this data sample
      sim = new Simulator(granularity, i);

      // Step the simulator through the appropriate number of iterations
      for(int times = 0; times < sim.getStepsPerPulse() * numRuns; times++){
        sim.update();
      }

      // Output data to the data file
      for(int j = 0; j < sim.getStepsPerPulse(); j++){
        output.format("%-12.4f%-12.4f%-12.4f%-12.4f%-12.4f%n",
                        sim.getTime()[j],
                        sim.getMean()[j],
                        sim.getVariance()[j],
                        sim.getBinning()[j],
                        sim.getPulseShape()[j]);
      }

      // Close file and print status
      output.close();
      System.out.println(filename);
    }
  }
}
