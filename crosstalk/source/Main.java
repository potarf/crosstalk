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
    Crosstalk test = new Crosstalk(granularity);
	  PrintWriter output = null;
	  
		for(int i = min; i <= max; i += step){
      int offset = (int)(Math.log10(max)) + 1;
      String filename = String.format("%0" + offset + "d_phot.dat", i);
      try{
	      output = new PrintWriter("data/" + filename, "UTF-8");
	    } catch (IOException e) {
	      System.out.println("File cannot open");
	    }
      output.println("3 time(ns) charge");
      test.initValues(i);
      
      for(int times = 0; times < test.e.STEPS_PER_NS * numRuns * test.e.PULSE_LEN; times++){
        test.update();
      }
      
      for(int j = 0; j < test.current.length; j++){
	      output.println(j / (float)test.e.STEPS_PER_NS + " " + test.mean[j] + " " + test.variance[j] + " " + test.bin[j]);
	    }
      output.close();
      System.out.println(filename);
	  }
	}

}
