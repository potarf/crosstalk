// Global Constants
static final int SIM_DIAM = 550;
static final int CELL_DIAM = 195;
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

void setup(){
  
  // Initialize window
  size(1110, 700);
  background(255);
  noStroke();

  // Initialize output file
  output = createWriter("positions.txt"); 
  
  inSigma     = 1;
  inMean      = 20;
  numPhotons  = 1000;

  t1 = 1;
  t2 = 5;
  t3 = 10;

  gauss = new GaussianIntNorm(inSigma, inMean, 0, PULSE_LEN,PULSE_LEN * STEPS_PER_NS);
  cellCharge = new CellCharge(t1, t2, t3, 0, DEAD_TIME, DEAD_TIME * STEPS_PER_NS);
  cellProb = new CellProbability(t1, t2, 0, (int)t2, (int)(t2 * STEPS_PER_NS));


  //Initialize data values  
  p   = new Pulse(numPhotons, gauss);
  chip      = new Sipm(CELL_DIAM, p, cellCharge, cellProb);
  pulseData = new StatDist[PULSE_LEN * STEPS_PER_NS];
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i] = new StatDist();
  }

  current   = new float[PULSE_LEN * STEPS_PER_NS];
  mean      = new float[PULSE_LEN * STEPS_PER_NS];
  variance  = new float[PULSE_LEN * STEPS_PER_NS];
  
  pulseSizeSlider = new HScrollbar(0, SIM_DIAM + 150/2 - 36, SIM_DIAM, 16, 1);
  pulseCenterSlider = new HScrollbar(0, SIM_DIAM + 150/2, SIM_DIAM, 16, 1);
  pulseSigmaSlider = new HScrollbar(0, SIM_DIAM + 150/2 + 36, SIM_DIAM, 16, 1);
  t1Slider = new HScrollbar(SIM_DIAM, SIM_DIAM + 150/2 - 36, SIM_DIAM, 16, 1);
  t2Slider = new HScrollbar(SIM_DIAM, SIM_DIAM + 150/2, SIM_DIAM, 16, 1);
  t3Slider = new HScrollbar(SIM_DIAM, SIM_DIAM + 150/2 + 36, SIM_DIAM, 16, 1);

  input = new float[PULSE_LEN * STEPS_PER_NS];
  pulse = new float[DEAD_TIME * STEPS_PER_NS];
  for(int i = 0; i < input.length; i++){
    input[i] =gauss.get(i) * p.getNum();
    if( i < DEAD_TIME * STEPS_PER_NS){
      pulse[i] = cellCharge.get(i);
    }
  }
}

void draw(){

  pulseSizeSlider.update();
  pulseCenterSlider.update();
  pulseSigmaSlider.update();
  t1Slider.update();
  t2Slider.update();
  t3Slider.update();
  pulseSizeSlider.display();
  pulseCenterSlider.display();
  pulseSigmaSlider.display();
  t1Slider.display();
  t2Slider.display();
  t3Slider.display();

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
  output.flush(); // Writes the remaining data to the file
  output.close(); // Finishes the file
  exit(); // Stops the program
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
    if( i < DEAD_TIME * STEPS_PER_NS){
      pulse[i] = cellCharge.get(i);
    }
  }
}

void updateValues(){
  
  if(inSigma != pulseSigmaSlider.getValue(1, 25)){
    inSigma = pulseSigmaSlider.getValue(1, 25);
    ((GaussianIntNorm)gauss).setSigma(inSigma); 
    clearStats();
  }

  if(inMean != pulseCenterSlider.getValue(0, 50)){
    inMean = pulseCenterSlider.getValue(0, 50);
    ((GaussianIntNorm)gauss).setMean(inMean);
    clearStats();
  }
  
  if(numPhotons != (int)pulseSizeSlider.getValue(0,100000)){
    numPhotons = (int)pulseSizeSlider.getValue(0,100000);
    p.setNum(numPhotons);
    clearStats();
  }
  if(t1 != t1Slider.getValue(1, 3)){
    t1 = t1Slider.getValue(1, 3);
    ((CellCharge)cellCharge).setT1(t1);
    ((CellProbability)cellProb).setT1(t1);
    clearStats();
  }
  if(t2 != t2Slider.getValue(0, 10)){
    t2 = t2Slider.getValue(0, 10);
    ((CellCharge)cellCharge).setT2(t2);
    ((CellProbability)cellProb).setT2(t2);
    clearStats();
  }
  if(t3 != t3Slider.getValue(1, 10)){
    t3 = t3Slider.getValue(1, 10);
    ((CellCharge)cellCharge).setT3(t3);
    clearStats();
  }
  
}

void clearStats(){
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i].clear();
  }
}
