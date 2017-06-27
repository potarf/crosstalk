  // Global Constants
static final int SIM_DIAM = 550;
PrintWriter output;

Sipm chip;
StatDist pulseData[];
float[] current, mean, variance, input, pulse;

// Plotter interactive variables
float inSigma, inMean, numPhotons;

HScrollbar pulseSizeSlider, pulseCenterSlider, pulseSigmaSlider;
ScrollbarLabel pulseSizeLabel, pulseCenterLabel, pulseSigmaLabel;
NormExpression gauss, cellCharge, cellProb, cellRecharge;
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
  numPhotons  = log(4000);

  gauss = new GaussianIntNorm(inSigma, inMean, 0, e.PULSE_LEN,e.PULSE_LEN * e.STEPS_PER_NS);
  cellCharge = new CellCharge(0, e.DEAD_TIME, e.DEAD_TIME * e.STEPS_PER_NS);
  cellProb = new CellProbability(0, e.RISE_TIME, e.RISE_TIME * e.STEPS_PER_NS);
  cellRecharge = new CellRecharge(0, e.DEAD_TIME, e.DEAD_TIME * e.STEPS_PER_NS);


  //Initialize data values
  p   = new Pulse((int)exp(numPhotons), gauss, e);
  chip      = new Sipm(e.CELL_DIAM, p, cellCharge, cellProb, cellRecharge, e);
  pulseData = new StatDist[e.PULSE_LEN * e.STEPS_PER_NS];
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i] = new StatDist();
  }

  current   = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  mean      = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  variance  = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  
  pulseSizeSlider = new HScrollbar(0, SIM_DIAM + 32, SIM_DIAM, 16, log(1), log(100000), numPhotons);
  pulseCenterSlider = new HScrollbar(0, SIM_DIAM + 64 , SIM_DIAM, 16, 0, e.PULSE_LEN, inMean);
  pulseSigmaSlider = new HScrollbar(0, SIM_DIAM + 96, SIM_DIAM, 16, .1, 20, inSigma);
  
  pulseSizeLabel = new ScrollbarLabel(0, SIM_DIAM + 30, SIM_DIAM, 16, "Pulse Size", "photons", exp(pulseSizeSlider.getValue()));
  pulseCenterLabel = new ScrollbarLabel(0, SIM_DIAM + 62, SIM_DIAM, 16, "Pulse Center", "nanoseconds", pulseCenterSlider.getValue());
  pulseSigmaLabel = new ScrollbarLabel(0, SIM_DIAM + 94, SIM_DIAM, 16, "Pulse Sigma", "nanoseconds", pulseSigmaSlider.getValue());

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
  background(255);
  pulseSizeSlider.update();
  pulseCenterSlider.update();
  pulseSigmaSlider.update();
  
  pulseSizeLabel.update(exp(pulseSizeSlider.getValue()));
  pulseCenterLabel.update(pulseCenterSlider.getValue());
  pulseSigmaLabel.update(pulseSigmaSlider.getValue());
  
  pulseSizeSlider.display();
  pulseCenterSlider.display();
  pulseSigmaSlider.display();
  
  pulseSizeLabel.display();
  pulseCenterLabel.display();
  pulseSigmaLabel.display();

  updateValues();
  // Update time and environment
  update();

  // Draw things
  chip.draw(g, 0, 0, 550);
  Plot.drawPlot(g, current, 550, 0, 550, 200, 255, 30, 0);
  float yScale = Plot.drawPlot(g, input, 550, 200, 550, 200, 0, 255, 30);
  Plot.drawPlot(g, mean, 550, 200, 550, 200, 0, 30, 255, false, true, yScale);
  Plot.drawPlot(g, pulse, 550, 400, 550, 300, 0, 255, 30);
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
  
  if(numPhotons != pulseSizeSlider.getValue()){
    numPhotons = pulseSizeSlider.getValue();
    p.setNum((int)exp(numPhotons));
    clearStats();
  }
  
}

void clearStats(){
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i].clear();
  }
}