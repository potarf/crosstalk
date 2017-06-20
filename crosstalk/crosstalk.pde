// Global Constants
static final int SIM_DIAM = 550;
static final int CELL_DIAM = 195;
PrintWriter output;

//////// NEW CODE ///////
Sipm chip;
Distribution pulseData[];
float[] current;
float[] mean;
float[] variance;

HScrollbar pulseSizeSlider, pulseCenterSlider, pulseSigmaSlider;

void setup(){
  
  // Initialize window
  size(1100, 700);
  background(255);
  noStroke();

  // Initialize output file
  output = createWriter("positions.txt"); 
       
  //Initialize data values
  Pulse p   = new Pulse(1000);
  chip      = new Sipm(CELL_DIAM, p);
  pulseData = new Distribution[PULSE_LEN * STEPS_PER_NS];
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i] = new Distribution();
  }

  current   = new float[PULSE_LEN * STEPS_PER_NS];
  mean      = new float[PULSE_LEN * STEPS_PER_NS];
  variance  = new float[PULSE_LEN * STEPS_PER_NS];
  
  pulseSizeSlider = new HScrollbar(0, SIM_DIAM + 150/2 - 36, SIM_DIAM, 16, 16);
  pulseCenterSlider = new HScrollbar(0, SIM_DIAM + 150/2, SIM_DIAM, 16, 16);
  pulseSigmaSlider = new HScrollbar(0, SIM_DIAM + 150/2 + 36, SIM_DIAM, 16, 16);

}

void draw(){
  // Update time and environment
  update();

  // Draw things
  chip.draw(g, 0, 0, 550);
  Plot.drawPlot(g, current, 550, 0, 550, 275, 255, 30, 0);
  Plot.drawPlot(g, mean, 550, 275, 550, 275, 0, 30, 255, true);
  drawBorders();
  
  pulseSizeSlider.update();
  pulseCenterSlider.update();
  pulseSigmaSlider.update();
  pulseSizeSlider.display();
  pulseCenterSlider.display();
  pulseSigmaSlider.display();
}

void keyPressed(){
  output.flush(); // Writes the remaining data to the file
  output.close(); // Finishes the file
  exit(); // Stops the program
}

void drawBorders(){
  stroke(0);
  line(550, 0, 550, 550);
  line(550, 550 / 2, 2 * 550, 550 / 2);
  line(0, 550, 2 * 550, 550);
}

void update(){
  e.increment();
  chip.update();
  
  double curCharge = chip.getCharge();
  int curStep = e.getStep() % (PULSE_LEN * STEPS_PER_NS);

  pulseData[curStep].add(curCharge);

  for(int i = 0; i < pulseData.length; i++){
    current[i]  = (float)pulseData[i].getCurrent();
    mean[i]     = (float)pulseData[i].getMean();
    variance[i] = (float)pulseData[i].getVariance();
  }
}