// Global Constants
static final int SIM_DIAM = 550;
PrintWriter output;

Sipm chip;
StatDist pulseData[];
float[] current, mean, variance, input, pulse;

// Plotter interactive variables
float inSigma, inMean;
int numPhotons;
float t1, t2, t3;

HScrollbar pulseSizeSlider, pulseCenterSlider, pulseSigmaSlider;
HScrollbar t1Slider, t2Slider, t3Slider;
NormExpression gauss, cellCharge, cellProb;
Pulse p;
Environment e;

void setup(){
  
  // Initialize window
  size(1110, 700);
  background(255);
  noStroke();
  e = new Environment();

  // Initialize output file
  output = createWriter("positions.txt"); 
  
  inSigma     = 5;
  inMean      = 20;
  numPhotons  = 10000;

  t1 = 1;
  t2 = 5;
  t3 = 10;

  gauss = new GaussianIntNorm(inSigma, inMean, 0, e.PULSE_LEN,e.PULSE_LEN * e.STEPS_PER_NS);
  cellCharge = new CellCharge(t1, t2, t3, 0, e.DEAD_TIME, e.DEAD_TIME * e.STEPS_PER_NS);
  cellProb = new CellProbability(t1, t2, 0, (int)t2, (int)(t2 * e.STEPS_PER_NS));


  //Initialize data values  
  p   = new Pulse(numPhotons, gauss, e);
  chip      = new Sipm(e.CELL_DIAM, p, cellCharge, cellProb, e);
  pulseData = new StatDist[e.PULSE_LEN * e.STEPS_PER_NS];
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i] = new StatDist();
  }

  current   = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  mean      = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  variance  = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  
  pulseSizeSlider = new HScrollbar(0, SIM_DIAM + 150/2 - 42, SIM_DIAM, 16, 0, 100000, numPhotons);
  pulseCenterSlider = new HScrollbar(0, SIM_DIAM + 150/2 - 8 , SIM_DIAM, 16, 0, e.PULSE_LEN, inMean);
  pulseSigmaSlider = new HScrollbar(0, SIM_DIAM + 150/2 + 28, SIM_DIAM, 16, .1, 20, inSigma);

  input = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  pulse = new float[e.DEAD_TIME * e.STEPS_PER_NS];
  for(int i = 0; i < input.length; i++){
    input[i] =gauss.get(i) * p.getNum();
    if( i < e.DEAD_TIME * e.STEPS_PER_NS){
      pulse[i] = cellCharge.get(i);
    }
  }
}

void draw(){

  pulseSizeSlider.update();
  pulseCenterSlider.update();
  pulseSigmaSlider.update();
  pulseSizeSlider.display();
  pulseCenterSlider.display();
  pulseSigmaSlider.display();

  updateValues();
  // Update time and environment
  update();

  // Draw things
  chip.draw(g, 0, 0, 550);
  Plot.drawPlot(g, current, 550, 0, 550, 150, 255, 30, 0);
  float yScale = Plot.drawPlot(g, input, 550, 150, 550, 150, 0, 255, 30);
  Plot.drawPlot(g, mean, 550, 150, 550, 150, 0, 30, 255, false, true, yScale);
  Plot.drawPlot(g, pulse, 550, 300, 550, 150, 0, 255, 30);
}

void keyPressed(){
  // Closes the program with Esc key
  if(key == 27){
    output.flush(); // Writes the remaining data to the file
    output.close(); // Finishes the file
    exit();         // Stops the program
  }
}

void update(){
  e.increment();
  chip.update();
  
  double curCharge = chip.getCharge();
  int curStep = e.getStep() % (e.PULSE_LEN * e.STEPS_PER_NS);

  pulseData[curStep].add(curCharge);

  for(int i = 0; i < pulseData.length; i++){
    current[i]  = (float)pulseData[i].getCurrent();
    mean[i]     = (float)pulseData[i].getMean();
    variance[i] = (float)pulseData[i].getVariance();
    input[i] = gauss.get(i) * p.getNum();
    if( i < e.DEAD_TIME * e.STEPS_PER_NS){
      pulse[i] = cellCharge.get(i);
    }
  }
}

void updateValues(){
  
  if(inSigma != pulseSigmaSlider.getValue()){
    inSigma = pulseSigmaSlider.getValue();
    ((GaussianIntNorm)gauss).setSigma(inSigma); 
    clearStats();
  }

  if(inMean != pulseCenterSlider.getValue()){
    inMean = pulseCenterSlider.getValue();
    ((GaussianIntNorm)gauss).setMean(inMean);
    clearStats();
  }
  
  if(numPhotons != (int)pulseSizeSlider.getValue()){
    numPhotons = (int)pulseSizeSlider.getValue();
    p.setNum(numPhotons);
    clearStats();
  }
  
}

void clearStats(){
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i].clear();
  }
}
