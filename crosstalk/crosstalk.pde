
static final int SIM_DIAM = 550;
PrintWriter output;

float[] current, mean, variance, input, pulse, bin;
double[] cellCharges, time;
// Plotter interactive variables
float numPhotons;

HScrollbar pulseSizeSlider, timeShiftSlider, crossProbSlider;
ScrollbarLabel pulseSizeLabel, timeShiftLabel, crossProbLabel;
simulator.Simulator sim;

void setup(){
  size(1110, 700);
  sim = new simulator.Simulator(2, 1000, 37990, .0001, 3, .5, 5, 0);
  time = sim.getTime();
  cellCharges = new double[1000];
  initGraphics();
}


public void initGraphics(){
  // Initialize window
  background(255);
  noStroke();
  
  pulseSizeSlider   = new HScrollbar(0, SIM_DIAM + 32, SIM_DIAM, 16,
                            log(1), log(1000000), log(sim.getNumPhotons()));
  timeShiftSlider   = new HScrollbar(0, SIM_DIAM + 64 , SIM_DIAM, 16,
                            0, sim.getStepsPerPulse() - 1, sim.getTimeShift());
  crossProbSlider   = new HScrollbar(0, SIM_DIAM + 96, SIM_DIAM, 16,
                            log(.0001), log(1),
                            log((float)sim.getCrossProb()));
  pulseSizeLabel  = new ScrollbarLabel(0, SIM_DIAM + 30, SIM_DIAM, 16,
                            "Pulse Size", "photons",
                            exp(pulseSizeSlider.getValue()));
  timeShiftLabel  = new ScrollbarLabel(0, SIM_DIAM + 62, SIM_DIAM, 16,
                            "Time Shift", "nanoseconds",
                            (float)time[(int)timeShiftSlider.getValue()]);
  crossProbLabel  = new ScrollbarLabel(0, SIM_DIAM + 94, SIM_DIAM, 16, "Crosstalk Prob", "%", 100 * (float)sim.getCrossProb());
}

void draw(){
  background(255);
  pulseSizeSlider.update();
  timeShiftSlider.update();
  crossProbSlider.update();
  
  pulseSizeLabel.update(exp(pulseSizeSlider.getValue()));
  timeShiftLabel.update((float)time[(int)timeShiftSlider.getValue()]); 
  crossProbLabel.update(100 * (float)sim.getCrossProb());

  pulseSizeSlider.display();
  timeShiftSlider.display();
  crossProbSlider.display();
  
  pulseSizeLabel.display();
  timeShiftLabel.display();
  crossProbLabel.display();

  updateValues();
  // Update time and environment
  sim.update();

  // Draw things
  drawChip(g, 0, 0, 550);
  Plot.drawPlot(g, sim.getCurrent(), 550, 0, 550, 200, 255, 30, 0);
  float yScale = Plot.drawPlot(g, sim.getPulseShape(), 550, 200, 550, 200, 0, 255, 30);
  Plot.drawPlot(g, sim.getMean(), 550, 200, 550, 200, 0, 30, 255, false, true, yScale);
  Plot.drawPlot(g, sim.getBinning(), 550, 200, 550, 200, 255, 30, 0, false, true, yScale);
  Plot.drawPlot(g, sim.getBinning(), 550, 400, 550, 300, 0, 255, 30);
}

void keyPressed(){
  // Closes the program with Esc key
  if(key == 27){
    exit();         // Stops the program
  }
}

void updateValues(){ 

  if(sim.getTimeShift() != timeShiftSlider.getValue()){
    sim.setTimeShift((int)timeShiftSlider.getValue());
  }
  
  if(sim.getNumPhotons() != (int)exp(pulseSizeSlider.getValue())){
    sim.setNumPhotons((int)exp(pulseSizeSlider.getValue()));
    sim.clearStats();
  }

  if(sim.getCrossProb() != exp(crossProbSlider.getValue())){
    sim.setCrossProb(exp(crossProbSlider.getValue()));
    sim.clearStats();
  }
}

PGraphics drawChip(PGraphics g, int xOr, int yOr, int sideLen){
  int diameter = sim.getDiameter();
  float ratio = sideLen / (float) diameter;
  double[][] charges = sim.getNormCellCharge();
  boolean[][] isPulse = sim.getIsPulse();
  cellCharges[sim.getStep() % cellCharges.length] = charges[diameter / 2][diameter / 2];

  for(int x = 0; x < diameter; x++){
    for(int y = 0; y < diameter; y++){

      float p = (float)charges[x][y];
      if(p > 1){
        p = 1;
      }
      if(isPulse[x][y]){
        g.fill(255 - p*255, 255 - 30 * p, 255 - p * 255);
      }else{
        g.fill(255 - p * 255, 255 - 255 * p, 255 - p * 255);
      }
      g.noStroke();
      g.rect(xOr + x * ratio, yOr + y * ratio, ratio, ratio);
    }
  }

  return g;
}
