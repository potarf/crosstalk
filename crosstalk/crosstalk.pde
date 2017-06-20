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
float[] input;

void setup(){
  
  // Initialize window
  size(1100, 550);
  background(255);
  noStroke();

  // Initialize output file
  output = createWriter("positions.txt"); 
       
  //Initialize data values
  

  /////////// NEW CODE //////////////////
  Pulse p   = new Pulse(PULSE_SIZE);
  chip      = new Sipm(CELL_DIAM, p);
  pulseData = new Distribution[PULSE_LEN * STEPS_PER_NS];
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i] = new Distribution();
  }

  current   = new float[PULSE_LEN * STEPS_PER_NS];
  mean      = new float[PULSE_LEN * STEPS_PER_NS];
  variance  = new float[PULSE_LEN * STEPS_PER_NS];
  input     = new float[PULSE_LEN * STEPS_PER_NS];
  for(int i = 0; i < input.length; i++){
    input[i] = PULSE_DIST[(i - 1 + input.length) % input.length] * PULSE_SIZE; 
  }
}

void draw(){
  // Update time and environment
  update();

  // Draw things
  chip.draw(g, 0, 0, 550);
  Plot.drawPlot(g, current, 550, 0, 550, 275, 255, 30, 0);
  Plot.drawPlot(g, input, 550, 275, 550, 275, 0, 255, 30, true);
  Plot.drawPlot(g, mean, 550, 275, 550, 275, 0, 30, 255, false);
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
