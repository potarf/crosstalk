import java.io.PrintWriter;
import java.io.IOException;

import java.lang.Math;

public class Main{
	public static void main(String[] args){
	  int min = Integer.valueOf(args[0]);
	  int max = Integer.valueOf(args[1]);
	  int step = Integer.valueOf(args[2]);
      int numRuns = Integer.valueOf(args[3]);
      int granularity = Integer.valueOf(args[4]);
      String directory = "";
      if(args.length > 5){
        directory = args[5];
      }
      Simulator sim = new Simulator(granularity);
	  PrintWriter output = null;
	  
		for(int i = min; i <= max; i += step){
      int offset = (int)(Math.log10(max)) + 1;
      String filename = String.format("%0" + offset + "d_phot.dat", i);
      try{
	      output = new PrintWriter( directory + filename, "UTF-8");
	    } catch (IOException e) {
	      System.out.println("File cannot open");
	    }
      output.println("3 time(ns) charge");
      sim.initValues(i);
      
      for(int times = 0; times < sim.e.STEPS_PER_NS * numRuns * sim.e.PULSE_LEN; times++){
        sim.update();
      }
      
      for(int j = 0; j < sim.current.length; j++){
	      output.println(j / (float)sim.e.STEPS_PER_NS
                        + " " + sim.mean[j] 
                        + " " + sim.variance[j] 
                        + " " + sim.bin[j] 
                        + " " + sim.input[j] 
                        + " " + sim.pulse[j]);
	    }
      output.close();
      System.out.println(filename);
	  }
	}

}
