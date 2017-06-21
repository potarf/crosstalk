// Global Constants
static final int SIM_DIAM = 550;
static final int CELL_DIAM = 195;
PrintWriter output;

Sipm chip;
StatDist pulseData[];
float[] current;
float[] mean;
float[] variance;
float[] input;

// Plotter interactive variables
float sigma;
float center;
float num_photons;

HScrollbar pulseSizeSlider, pulseCenterSlider, pulseSigmaSlider;
NormExpression gauss, decayCharge, decayProb;
Pulse p;

void setup(){
  
  // Initialize window
  size(1100, 700);
  background(255);
  noStroke();

  // Initialize output file
  output = createWriter("positions.txt"); 
  
  gauss = new GaussianIntNorm(1, 20,0,50,50);

  //Initialize data values  
  p   = new Pulse(PULSE_SIZE, gauss);
  chip      = new Sipm(CELL_DIAM, p);
  pulseData = new StatDist[PULSE_LEN * STEPS_PER_NS];
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i] = new StatDist();
  }

  current   = new float[PULSE_LEN * STEPS_PER_NS];
  mean      = new float[PULSE_LEN * STEPS_PER_NS];
  variance  = new float[PULSE_LEN * STEPS_PER_NS];
  
  pulseSizeSlider = new HScrollbar(0, SIM_DIAM + 150/2 - 36, SIM_DIAM, 16, 16);
  pulseCenterSlider = new HScrollbar(0, SIM_DIAM + 150/2, SIM_DIAM, 16, 16);
  pulseSigmaSlider = new HScrollbar(0, SIM_DIAM + 150/2 + 36, SIM_DIAM, 16, 16);

  input = new float[PULSE_LEN * STEPS_PER_NS];
  for(int i = 0; i < input.length; i++){
    input[i] =gauss.get(i) * p.getNum();; 
  }
}

void draw(){

  pulseSizeSlider.update();
  pulseCenterSlider.update();
  pulseSigmaSlider.update();
  pulseSizeSlider.display();
  pulseCenterSlider.display();
  pulseSigmaSlider.display();

  ((GaussianIntNorm)gauss).setSigma(pulseSigmaSlider.getValue(1, 25)); 
  ((GaussianIntNorm)gauss).setMean(pulseCenterSlider.getValue(0, 50));
  p.setNum((int)pulseSizeSlider.getValue(0,100000));
  // Update time and environment
  update();

  // Draw things
  chip.draw(g, 0, 0, 550);
  Plot.drawPlot(g, current, 550, 0, 550, 275, 255, 30, 0);
  float yScale = Plot.drawPlot(g, input, 550, 275, 550, 275, 0, 255, 30);
  Plot.drawPlot(g, mean, 550, 275, 550, 275, 0, 30, 255, false, true, yScale);
  drawBorders();
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
    input[i] = gauss.get(i) * p.getNum(); 
  }
}
