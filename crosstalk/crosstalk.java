import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class crosstalk extends PApplet {

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

public void setup(){
  
  initValues();
  initGraphics();
}

public void initValues(){
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
  crossProbSlider   = new HScrollbar(0, SIM_DIAM + 96, SIM_DIAM, 16, log(.0001f), log(1), log(e.getCrossProb()));

  pulseSizeLabel  = new ScrollbarLabel(0, SIM_DIAM + 30, SIM_DIAM, 16, "Pulse Size", "photons", exp(pulseSizeSlider.getValue()));
  timeShiftLabel  = new ScrollbarLabel(0, SIM_DIAM + 62, SIM_DIAM, 16, "Time Shift", "nanoseconds", (int)timeShiftSlider.getValue() * 1/(float)e.STEPS_PER_NS);
  crossProbLabel  = new ScrollbarLabel(0, SIM_DIAM + 94, SIM_DIAM, 16, "Crosstalk Prob", "%", 100 * e.getCrossProb());
}

public void draw(){
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

public void keyPressed(){
  // Closes the program with Esc key
  if(key == 27){
    output.flush(); // Writes the remaining data to the file
    output.close(); // Finishes the file
    exit();         // Stops the program
  }
}

public void update(){
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

public void updateValues(){ 

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

public void clearStats(){
  for(int i = 0; i < pulseData.length; i++){
    pulseData[i].clear();
  }
}
class Cell{

  private int[] actStep;
  private float[] actCharge;
  private int actNum;
  private int deadSteps;
  private boolean valid;
  private boolean active;
  private boolean isPulse;
  private float up, right, down, left;
  private Environment e;
  private NormExpression charge;
  private NormExpression probability;
  private NormExpression recharge;

  public Cell(boolean valid, NormExpression charge, NormExpression probability, NormExpression recharge, Environment e){
    this.deadSteps = e.RISE_TIME * e.STEPS_PER_NS;
    this.valid = valid;
    this.charge = charge;
    this.recharge = recharge;
    this.probability = probability;
    this.e = e;

    actStep = new int[e.DEAD_TIME / e.RISE_TIME + 1];
    actCharge = new float[e.DEAD_TIME / e.RISE_TIME + 1];

    for(int i = 0; i < actStep.length; i++){
      actStep[i] = -1;
      actCharge[i] = 0;
    }
    actNum = 0;
  }

  public boolean activate(boolean isPulse){
    if(curStep() < deadSteps){
      return false;
    }
    up    = random(1.0f);
    right = random(1.0f);
    down  = random(1.0f);
    left  = random(1.0f);
    actStep[actNum % actStep.length] = e.getStep() + 1;
    actCharge[actNum % actStep.length] = (float)getRecharge(); 
    actNum = actNum % actStep.length + 1;
    this.isPulse = isPulse;
    return true;
  }

  public void updateNeighbors(Cell[][] cells, int x, int y){
    if(curStep() < deadSteps){
      double spreadProb = this.getProb(); 
      if(x - 1 > 0 && cells[x - 1][y].isValid()){ 
        if(up < spreadProb)
          cells[x - 1][y].activate(false);
      }
    
      if(x + 1 < cells.length && cells[x + 1][y].isValid()){ 
        if(down < spreadProb)
          cells[x + 1][y].activate(false);
      }
    
      if(y - 1 > 0 && cells[x][y - 1].isValid()){ 
        if(left < spreadProb)
          cells[x][y - 1].activate(false);
      }
    
      if(y + 1 < cells.length && cells[x][y + 1].isValid()){ 
        if(right < spreadProb)
          cells[x][y + 1].activate(false);
      }
    }
  }

  public boolean isActivated(){
    return curStep() < deadSteps;
  }

  public boolean isValid(){
    return valid;
  }

  public double getProb(){
    return probability.get(curStep()) * curCharge() * e.getCrossProb();
  }

  public double getCharge(){
    float total = 0;
    for(int i = 0; i < actStep.length; i++){
      if(actStep[i] != -1){
        total += charge.get(e.getStep() - actStep[i]) * actCharge[i];
      }
    }
    return charge.get(curStep());
  }

  public double getRecharge(){
    return recharge.get(curStep());
  }

  public double getLife(){
    return (1 - curStep() / (double) (e.DEAD_TIME * e.STEPS_PER_NS));
  }

  public int curStep(){
    if(actNum == 0){
      return e.DEAD_TIME * e.STEPS_PER_NS + 1;
    }
    return e.getStep() - actStep[actNum - 1];
  }

  public float curCharge(){
    if(actNum == 0){
      return 0;
    }
    return actCharge[actNum - 1];
  }

/*  void reset(){
    actStep = -1 * deadSteps;;
  }*/

  public PGraphics draw(PGraphics g, float x, float y, float size){
   float p = (float)getLife();

   if(isPulse){
      g.fill(255 - p*255, 255 - 30 * p, 255 - p * 255);
    
    }else{
      g.fill(255 - p * 255, 255 - 255 * p, 255 - p * 255);
    
    }
    g.noStroke();
    g.rect(x, y, size, size);
    return g;
  }
}
class Environment{
  private int step;
  
  //Simulation constants
  public final int STEPS_PER_NS = 2;
  public final int PULSE_LEN = 125;
  public final int CELL_DIAM = 195;
  
  //Cell constants
  public final int RISE_TIME = 4;
  public final int DEAD_TIME = 40;
  private float crossProb = .046f;

  public void Evironment(){
    step = 0;
  }

  public int increment(){
    step++;
    return step;
  }

  public int getStep(){
    return step;
  }

  public double getTime(){
    return step / (double)(STEPS_PER_NS);
  }

  public int pulseNum(){
    return (step / STEPS_PER_NS) / PULSE_LEN;
  }
  
  public float getCrossProb(){
    return crossProb;
  }

  public void setCrossProb(float crossProb){
    this.crossProb = crossProb;
  }
}
public abstract class NormExpression{

  float[] values;
  int minimum;
  int maximum;
  int numSteps;

  public NormExpression(int minimum, int maximum, int numSteps){
    this.minimum = minimum;
    this.maximum = maximum;
    this.numSteps = numSteps;
    values = new float[numSteps];
  }

  protected void updateValues(){
    float total = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = operation(minimum + i * (float)(maximum - minimum) / numSteps); 
      total += values[i];
    }
    for(int i = 0; i < values.length; i++){
      values[i] /= total;
    }
  }

  public float get(int step){
    if(step >= 0 && step < values.length)
      return values[step];
    return 0;
  }
  
  public abstract float operation(float val);
}

public class GaussianIntNorm extends NormExpression{
  float sigma;
  float mean;
  
  public GaussianIntNorm(float sigma, float mean, int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    this.sigma = sigma;
    this.mean = mean;
    updateValues();
  }

  public float operation(float val){
    return 1/sqrt(2 * sigma * sigma * PI) * exp(-pow(val - mean, 2) / (2 * pow(sigma, 2)));
  }

  public void setSigma(float sigma){
    if(this.sigma != sigma){
      updateValues();
      this.sigma = sigma;
    }
  }

  public void setMean(float mean){
    if(this.mean != mean){
      updateValues();
      this.mean = mean;
    }
  }
}

public class CellCharge extends NormExpression{

  public CellCharge(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

	public float operation(float  val) {
 		float t1 = 1;
		float t2 = e.RISE_TIME;
 		float t3 = 5;
 		float t4 = 15;
 		float t5 = 9;
 		if(val < t2) {
   		return 1-exp(-val/t1);
 		} else if(val < t4) {
   		return (1-exp(-t2/t1))*exp(-(val-t2)/t3);
 		} else {
   		return (1-exp(-t2/t1))*exp(-(t4-t2)/t3)*exp(-(val-t4)/t5);
 		}
	}
}

public class CellProbability extends NormExpression{

  public CellProbability(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

  public float operation(float val){
 		float t1 = 1;
		float t2 = e.RISE_TIME;

 		if(val < t2) {
   		return 1-exp(-val/t1);
    }
    return 0;
  }
  
  protected void updateValues(){
    float maxima = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = operation(minimum + i * (float)(maximum - minimum) / numSteps);
      if(values[i] > maxima){
        maxima = values[i];
      }
    }
    for(int i = 0; i < values.length; i++){
      values[i] /= maxima;
    }
  }
}

public class CellRecharge extends NormExpression{

  public CellRecharge(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

  
	public float operation(float val){
    float t1 = e.RISE_TIME;
 		float tRc = 5;
		float t2 = e.DEAD_TIME;
  
    if(val < t1){
      return 0; 
    }

 		if(val < t2) {
   		return 1-exp(-(val - t1)/tRc);
    }
    return 1;
  }

  protected void updateValues(){
    float maxima = 0;
    for(int i = 0; i < values.length; i++){
      values[i] = operation(minimum + i * (float)(maximum - minimum) / numSteps);
      if(values[i] > maxima){
        maxima = values[i];
      }
    }
    for(int i = 0; i < values.length; i++){
      values[i] /= maxima;
    }
  }

  public float get(int step){
    if(step >= 0 && step < values.length)
      return values[step];
    return 1;
  }

}

public class LightPulse extends NormExpression{
  public LightPulse(int minimum, int maximum, int numSteps){
    super(minimum, maximum, numSteps);
    updateValues();
  }

  
	public float operation(float val){
    float t1 = 0.8f;
 		float t2 = 4;
		float t3 = 14;
    float t4 = 8;
    float t5 = 6;
    float t6 = 20;
    float t7 = 14;
  
    if(val < t2){
      return 1 - exp(-val/t1); 
    }
 		if(val < t4){
   		return (1 - exp(-t2/t1))*(exp(-(val - t2)/t3));
    }
    if(val < t6){
   		return (1 - exp(-t2/t1))*(exp(-(t4 - t2)/t3)) * (exp(-(val - t4)/t5));
    }
    return (1 - exp(-t2/t1))*(exp(-(t4 - t2)/t3)) * (exp(-(t6 - t4)/t5)) * exp(-(val - t6)/t7);
  }
}
static class Plot{
  static public float drawPlot(PGraphics g, float[] data, int xOr, int yOr, int w, int h, int r, int green, int b, boolean clear, boolean setScale, float yGivenScale){
    int size = data.length;
    float xScale = w / (float)size;
    float yScale;
    if(!setScale){
      float maxY = 0;
      for(int i = 0; i < size; i++){
        if(data[i] > maxY)
          maxY = data[i];
      }
      yScale = (h * 0.85f) / maxY;
    } else {
      yScale = yGivenScale;
    }

    if(clear){
      g.fill(255);
      g.stroke(255);
      g.rect(xOr, yOr, w, h);
    }
    
    g.stroke(r, green, b);
    for(int i = 0; i < size; i++){
      g.line(xOr + i * xScale, yOr + h - data[i] * yScale, xOr + (i + 1) * xScale, yOr + h - data[(i + 1) % size] * yScale);
    }
    
    g.stroke(0);
    g.line(xOr, yOr, xOr + w, yOr);
    g.line(xOr, yOr + h, xOr + w, yOr + h);
    g.line(xOr, yOr, xOr, yOr + h);
    g.line(xOr + w, yOr, xOr + w, yOr + h);

    return yScale;

  }
  static public float drawPlot(PGraphics g, float[] data, int xStr, int yStr, int w, int h, int r, int green, int b){
    return drawPlot(g, data, xStr, yStr, w, h, r, green, b, true, false, 0); 
  }
}
class Pulse{
  int numPhotons;
  int startStep;
  double remainder;
  NormExpression shape;
  Environment e;

  public Pulse(int numPhotons, NormExpression shape, Environment e){
    this.e = e;
    this.numPhotons = numPhotons;
    startStep = e.getStep();
    this.shape = shape;
    remainder = 0;
  }

  public void pulse(Cell[][] cells){
    int diameter = cells.length;
    int curStep = (e.getStep() - startStep) % (e.PULSE_LEN * e.STEPS_PER_NS);
    double actPhot = numPhotons * shape.get(curStep) + remainder;
    int photons = (int)actPhot;
    remainder = actPhot - photons;

    for(int i = 0; i < photons; i++){
      int x = (int)random(diameter);
      int y = (int)random(diameter);
      if(!cells[x][y].isValid()){
        i--;
      } else {
        cells[x][y].activate(true);
      }
    }

  }

  public void setNum(int number){
    this.numPhotons = number;
  }

  public int getNum(){
    return numPhotons;
  }

}
class HScrollbar {
  float barWidth, barHeight;    // width and height of bar
  float xPos, yPos;       // x and y position of bar
  
  float sliderWidth;
  float sliderPos;    // x position of slider
  float maxVal, minVal;
  
  boolean over;           // is the mouse over the slider?
  boolean locked;

  HScrollbar (float xPos, float yPos, float barWidth, float barHeight, float minVal, float maxVal, float curVal) {
    this.xPos = xPos;
    this.yPos = yPos;
    this.barWidth = barWidth;
    this.barHeight = this.sliderWidth = barHeight;
    this.maxVal = maxVal;
    this.minVal = minVal;
    this.sliderPos = (curVal - minVal) / (maxVal - minVal) * (barWidth - sliderWidth);
  }

  public void update() {
    if (mousePressed && over) {
      locked = true;
    }
    if (!mousePressed) {
      locked = false;
    }
    if (overEvent() && !mousePressed) {
      over = true;
    } else {
      over = false;
    }
    if (locked) {
      sliderPos = constrain(mouseX - sliderWidth / 2, 0, barWidth - sliderWidth);
    }
  }

  public float constrain(float val, float minv, float maxv) {
    return min(max(val, minv), maxv);
  }

  public boolean overEvent() {
    if (mouseX > xPos && mouseX < xPos+barWidth &&
       mouseY > yPos && mouseY < yPos+barHeight) {
      return true;
    } else {
      return false;
    }
  }

  public void display() {
    noStroke();
    fill(204);
    rect(xPos, yPos, barWidth, barHeight);
    if (over || locked) {
      fill(0, 0, 0);
    } else {
      fill(102, 102, 102);
    }
    rect(sliderPos, yPos, sliderWidth, sliderWidth);
  }

  public float getPos() {
    // Convert sliderPos to be values between
    // 0 and the total width of the scrollbar
    return sliderPos;
  }
  
  public float getValue(){
    //Interpolate a value on the scrollbar to a value between given min and max
    return (sliderPos - xPos) / (barWidth - sliderWidth) * (maxVal - minVal) + minVal;
  }
}

class ScrollbarLabel{
  int xPosition, yPosition;
  int labelWidth, labelHeight;
  PFont f;
  String title;
  String units;
  String value;
  
  ScrollbarLabel(int xPosition, int yPosition, int labelWidth, int labelHeight, String title, String units, float value){
    this.xPosition = xPosition;
    this.yPosition = yPosition;
    this.labelWidth = labelWidth;
    this.labelHeight = labelHeight;
    this.title = title;
    this.units = units;
    this.value = str(value);
    
    f = createFont("Arial", 16,true); // Arial, 16 point, anti-aliasing on    
  }
  
  public void display(){
    textFont(f,labelHeight);
    fill(0);
    
    int separation = labelWidth / 5;
    text(title, separation + xPosition, yPosition);
    text(value, 2* separation + xPosition, yPosition);
    text(units, 3 * separation + xPosition, yPosition);
  }
  
  public void update(float value){
    this.value = str(value);
  }
  
}
class Sipm{
  Cell cells[][];
  Pulse p;
  Environment e;

  private int diameter;

  private int numActive;
  private double curCharge;

  public Sipm(int diameter, Pulse p, NormExpression cellCharge, NormExpression cellProb, NormExpression cellRecharge, Environment e){
    this.diameter = diameter;
    this.p = p;
    this.e = e;

    cells = new Cell[diameter][diameter];
    
    double center = (diameter - 1) / 2.0f;

    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        boolean inCircle = (x - center) * (x - center) + (y - center) * (y - center) - diameter * diameter /  4.0f <= 0;
        cells[x][y] = new Cell(inCircle, cellCharge, cellProb, cellRecharge, e);
      }
    }

  }

  public void update(){
    p.pulse(cells);
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        if(cells[x][y].isValid()){
          cells[x][y].updateNeighbors(cells, x, y);
        }
      }
    }
  }

/*  void clear(){
    
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        cells[x][y].reset();
      }
    }
  }*/

  public PGraphics draw(PGraphics g, int xOr, int yOr, int sideLen){

    float ratio = sideLen / (float) diameter;

    for(int x = 0; x < diameter; x++){
    
      for(int y = 0; y < diameter; y++){
        cells[x][y].draw(g, xOr + x * ratio, yOr + y * ratio, ratio);
      }
    }

    return g;
  }

  public double getCharge(){
    double total = 0;
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        total += cells[x][y].getCharge();
      }
    }
    return total;
  }
}
class StatDist{
  private double current;
  private double mean;
  private double sum;
  private double sqSum;
  private double variance;
  private int size;

  public StatDist(){
    current = mean = sum = sqSum = variance = size = 0;
  }

  public void add(double value){
    size++;
    current = value;

    sum += current;
    mean = sum / size;
    
    sqSum += current * current;

    variance = (sqSum - 2 * mean * sum + size * mean * mean) / size;
  }

  public double getCurrent(){
    return current;
  }

  public double getMean(){
    return mean;
  }

  public double getVariance(){
    return variance;
  }

  public void clear(){
    current = mean = sum = sqSum = variance = size = 0;
  }
}
  public void settings() {  size(1110, 700); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "crosstalk" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
