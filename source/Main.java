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
    int min         = Integer.valueOf(args[0]);
    int max         = Integer.valueOf(args[1]);
    int step        = Integer.valueOf(args[2]);
    int numRuns     = Integer.valueOf(args[3]);
    int granularity = Integer.valueOf(args[4]);
    int numCells    = Integer.valueOf(args[5]);
    double t1       = Double.valueOf(args[6]);
    double t2       = Double.valueOf(args[7]);
    double t3       = Double.valueOf(args[8]);
    double t4       = Double.valueOf(args[9]);
    double t5       = Double.valueOf(args[10]);
    
    // Get output directory
    String directory  = "";
    if(args.length > 11){
        directory = args[11];
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
      output.format("%d 5 time(ns) charge%n", i);

      output.format("%-12s%-12s%-12s%-12s%-12s%-12s%n",
                              "time",
                              "mean_output",
                              "variance",
                              "binning",
                              "in_pulse",
                              "pix_pulse");
      // Initialize the appropriate simulator for this data sample
      sim = new Simulator(granularity, i, numCells, t1, t2, t3, t4, t5);

      // Step the simulator through the appropriate number of iterations
      for(int times = 0; times < sim.getStepsPerPulse() * numRuns; times++){
        sim.update();
      }

      // Output data to the data file
      for(int j = 0; j < sim.getStepsPerPulse(); j++){
        output.format("%-12.4f%-12.4f%-12.4f%-12.4f%-12.4f%-12.4f%n",
                        sim.getTime()[j],
                        sim.getMean()[j],
                        sim.getVariance()[j],
                        sim.getBinning()[j],
                        sim.getPulseShape()[j],
                        sim.getPixelShape()[j]
                        );
      }

      // Close file and print status
      output.close();
      System.out.println(filename);
    }
  }
}
