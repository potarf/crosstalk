  // Global Constants
static final int SIM_DIAM = 550;
PrintWriter output;

Sipm chip;
StatDist pulseData[];
float[] current, mean, variance, input, pulse, bin;

// Plotter interactive variables
float numPhotons;
int timeShift;

HScrollbar pulseSizeSlider, timeShiftSlider, crossProbSlider;
ScrollbarLabel pulseSizeLabel, timeShiftLabel, crossProbLabel;
NormExpression gauss, cellCharge, cellProb, cellRecharge;
Pulse p;
Environment e;

void setup(){
  size(1110, 700);
  initValues();
  initGraphics();
}

void initValues(){
   e = new Environment();
  

  // Initialize output file
  output = createWriter("positions.txt"); 
  
  timeShift       = 20;
  numPhotons  = log(4000);

  //gauss = new GaussianIntNorm(e.STEPS_PER_NS, timeShift, 0, e.PULSE_LEN,e.PULSE_LEN * e.STEPS_PER_NS);
  gauss = new LightPulse(0, e.PULSE_LEN,e.PULSE_LEN * e.STEPS_PER_NS);
  cellCharge = new CellCharge(0, e.DEAD_TIME, e.DEAD_TIME * e.STEPS_PER_NS);
  cellProb = new CellProbability(0, e.RISE_TIME, e.RISE_TIME * e.STEPS_PER_NS);
  cellRecharge = new CellRecharge(0, e.DEAD_TIME, e.DEAD_TIME * e.STEPS_PER_NS);


  //Initialize data values
  p         = new Pulse((int)exp(numPhotons), gauss, e);
  chip      = new Sipm(e.CELL_DIAM, p, cellCharge, cellProb, cellRecharge, e);
  pulseData = new StatDist[e.PULSE_LEN * e.STEPS_PER_NS];
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i] = new StatDist();
  }

  current   = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  mean      = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  variance  = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  bin       = new float[e.PULSE_LEN * e.STEPS_PER_NS]; 
  
  input = new float[e.PULSE_LEN * e.STEPS_PER_NS];
  pulse = new float[e.DEAD_TIME * e.STEPS_PER_NS];
  
  for(int i = 0; i < input.length; i++){
    input[i] =gauss.get(i) * p.getNum();
    if( i < e.DEAD_TIME * e.STEPS_PER_NS){
      pulse[i] = cellCharge.get(i);
    }
  }
}

public void initGraphics(){
  // Initialize window
  background(255);
  noStroke();
  
  pulseSizeSlider   = new HScrollbar(0, SIM_DIAM + 32, SIM_DIAM, 16, log(1), log(100000), numPhotons);
  timeShiftSlider   = new HScrollbar(0, SIM_DIAM + 64 , SIM_DIAM, 16, 0, e.PULSE_LEN * e.STEPS_PER_NS, timeShift);
  crossProbSlider   = new HScrollbar(0, SIM_DIAM + 96, SIM_DIAM, 16, log(.0001), log(1), log(e.getCrossProb()));

  pulseSizeLabel  = new ScrollbarLabel(0, SIM_DIAM + 30, SIM_DIAM, 16, "Pulse Size", "photons", exp(pulseSizeSlider.getValue()));
  timeShiftLabel  = new ScrollbarLabel(0, SIM_DIAM + 62, SIM_DIAM, 16, "Time Shift", "nanoseconds", (int)timeShiftSlider.getValue() * 1/(float)e.STEPS_PER_NS);
  crossProbLabel  = new ScrollbarLabel(0, SIM_DIAM + 94, SIM_DIAM, 16, "Crosstalk Prob", "%", 100 * e.getCrossProb());
}

void draw(){
  background(255);
  pulseSizeSlider.update();
  timeShiftSlider.update();
  crossProbSlider.update();
  
  pulseSizeLabel.update(exp(pulseSizeSlider.getValue()));
  timeShiftLabel.update((int)timeShiftSlider.getValue() * 1/(float)e.STEPS_PER_NS); 
  crossProbLabel.update(100 * e.getCrossProb());

  pulseSizeSlider.display();
  timeShiftSlider.display();
  crossProbSlider.display();
  
  pulseSizeLabel.display();
  timeShiftLabel.display();
  crossProbLabel.display();

  updateValues();
  // Update time and environment
  update();

  // Draw things
  //chip.draw(g, 0, 0, 550);
  Plot.drawPlot(g, current, 550, 0, 550, 200, 255, 30, 0);
  float yScale = Plot.drawPlot(g, input, 550, 200, 550, 200, 0, 255, 30);
  Plot.drawPlot(g, mean, 550, 200, 550, 200, 0, 30, 255, false, true, yScale);
  Plot.drawPlot(g, bin, 550, 200, 550, 200, 255, 30, 0, false, true, yScale);
  Plot.drawPlot(g, bin, 550, 400, 550, 300, 0, 255, 30);
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
    current[(i + timeShift + pulseData.length) %pulseData.length]  = (float)pulseData[i].getCurrent();
    mean[(i + timeShift + pulseData.length) %pulseData.length]     = (float)pulseData[i].getMean();
    variance[(i + timeShift + pulseData.length) %pulseData.length] = (float)pulseData[i].getVariance();
    input[(i + timeShift + pulseData.length) %pulseData.length] = gauss.get(i) * p.getNum();
    if( i < e.DEAD_TIME * e.STEPS_PER_NS){
      pulse[i] = cellCharge.get(i);
    }
    int binTime = 25 * e.STEPS_PER_NS;
    if(i % binTime == 0){
      float binTot = 0;
      for(int t = 0; t < binTime; t++){
        binTot += mean[(i - t + pulseData.length) % pulseData.length] / (float)binTime;
      }
      for(int t = 0; t < binTime; t++){
        bin[(i - t + pulseData.length) % pulseData.length] = binTot;
      }
    }

  }
}

void updateValues(){ 

  if(timeShift != timeShiftSlider.getValue()){
    timeShift = (int)timeShiftSlider.getValue();
  }
  
  if(numPhotons != pulseSizeSlider.getValue()){
    numPhotons = pulseSizeSlider.getValue();
    p.setNum((int)exp(numPhotons));
    clearStats();
  }

  if(e.getCrossProb() != exp(crossProbSlider.getValue())){
    e.setCrossProb(exp(crossProbSlider.getValue()));
    clearStats();
  }
}

void clearStats(){
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i].clear();
  }
}
