
import simulator.Simulator;
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

            for(int times = 0; times < sim.getStepsPerPulse() * numRuns; times++){
                sim.update();
            }

            for(int j = 0; j < sim.getStepsPerPulse(); j++){
                output.println(sim.stepToTime(j)
                                + " " + sim.getMean()[j]
                                + " " + sim.getVariance()[j]
                                + " " + sim.getBinning()[j]
                                + " " + sim.getPulseShape()[j]);
            }
            output.close();
            System.out.println(filename);
        }
    }
}
