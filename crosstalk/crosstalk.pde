  // Global Constants
static final int SIM_DIAM = 550;
PrintWriter output;

Sipm chip;
StatDist pulseData[];
float[] current, mean, variance, input, pulse, bin;
double[] cellCharges;
// Plotter interactive variables
float numPhotons;
int timeShift;

HScrollbar pulseSizeSlider, timeShiftSlider, crossProbSlider;
ScrollbarLabel pulseSizeLabel, timeShiftLabel, crossProbLabel;
Simulator sim;

void setup(){
  size(1110, 700);
  sim = new Simulator(2);
  sim.initValues(10000);
  cellCharges = new double[1000];
  initGraphics();
}


public void initGraphics(){
  // Initialize window
  background(255);
  noStroke();
  
  pulseSizeSlider   = new HScrollbar(0, SIM_DIAM + 32, SIM_DIAM, 16, log(1), log(100000), log(sim.numPhotons));
  timeShiftSlider   = new HScrollbar(0, SIM_DIAM + 64 , SIM_DIAM, 16, 0, sim.e.PULSE_LEN * sim.e.STEPS_PER_NS, timeShift);
  crossProbSlider   = new HScrollbar(0, SIM_DIAM + 96, SIM_DIAM, 16, log(.0001), log(1), log((float)sim.e.getCrossProb()));

  pulseSizeLabel  = new ScrollbarLabel(0, SIM_DIAM + 30, SIM_DIAM, 16, "Pulse Size", "photons", exp(pulseSizeSlider.getValue()));
  timeShiftLabel  = new ScrollbarLabel(0, SIM_DIAM + 62, SIM_DIAM, 16, "Time Shift", "nanoseconds", (int)timeShiftSlider.getValue() * 1/(float)sim.e.STEPS_PER_NS);
  crossProbLabel  = new ScrollbarLabel(0, SIM_DIAM + 94, SIM_DIAM, 16, "Crosstalk Prob", "%", 100 * (float)sim.e.getCrossProb());
}

void draw(){
  background(255);
  pulseSizeSlider.update();
  timeShiftSlider.update();
  crossProbSlider.update();
  
  pulseSizeLabel.update(exp(pulseSizeSlider.getValue()));
  timeShiftLabel.update((int)timeShiftSlider.getValue() * 1/(float)sim.e.STEPS_PER_NS); 
  crossProbLabel.update(100 * (float)sim.e.getCrossProb());

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
  Plot.drawPlot(g, cellCharges, 550, 0, 550, 200, 255, 30, 0);
  float yScale = Plot.drawPlot(g, sim.input, 550, 200, 550, 200, 0, 255, 30);
  Plot.drawPlot(g, sim.mean, 550, 200, 550, 200, 0, 30, 255, false, true, yScale);
  Plot.drawPlot(g, sim.bin, 550, 200, 550, 200, 255, 30, 0, false, true, yScale);
  Plot.drawPlot(g, sim.bin, 550, 400, 550, 300, 0, 255, 30);
}

void keyPressed(){
  // Closes the program with Esc key
  if(key == 27){
    exit();         // Stops the program
  }
}

void updateValues(){ 

  if(sim.timeShift != timeShiftSlider.getValue()){
    sim.timeShift = (int)timeShiftSlider.getValue();
  }
  
  if(sim.numPhotons != (int)exp(pulseSizeSlider.getValue())){
    sim.numPhotons = (int)exp(pulseSizeSlider.getValue());
    sim.p.setNum(sim.numPhotons);
    sim.clearStats();
  }

  if(sim.e.getCrossProb() != exp(crossProbSlider.getValue())){
    sim.e.setCrossProb(exp(crossProbSlider.getValue()));
    sim.clearStats();
  }
}

PGraphics drawChip(PGraphics g, int xOr, int yOr, int sideLen){
  int diameter = sim.chip.getDiameter();
  float ratio = sideLen / (float) diameter;
  double[][] charges = sim.chip.getNormCellCharge();
  boolean[][] isPulse = sim.chip.getIsPulse();
  cellCharges[sim.e.getStep() % cellCharges.length] = charges[diameter / 2][diameter / 2];

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
