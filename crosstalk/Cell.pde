class Cell{

  private int actStep;
  private int deadSteps;
  private boolean valid;
  private boolean active;
  private boolean isPulse;
  private float up, right, down, left;
  private Environment e;
  private NormExpression charge;
  private NormExpression probability;

  public Cell(boolean valid, NormExpression charge, NormExpression probability, Environment e){
    deadSteps = e.DEAD_TIME * e.STEPS_PER_NS;
    actStep = deadSteps * -1;
    this.valid = valid;
    this.charge = charge;
    this.probability = probability;
    this.e = e;
  }
  
  public Cell(NormExpression charge, NormExpression probability, Environment e){
    deadSteps = e.DEAD_TIME * e.STEPS_PER_NS;
    actStep = deadSteps * -1;
    this.valid = true;
    this.charge = charge;
    this.probability = probability;
    this.e = e;
  }

  boolean activate(boolean isPulse){
    if(curStep() < deadSteps){
      return false;
    }
    up    = random(1.0);
    right = random(1.0);
    down  = random(1.0);
    left  = random(1.0);
    actStep = e.getStep() + 1;
    this.isPulse = isPulse;
    return true;
  }

  void updateNeighbors(Cell[][] cells, int x, int y){
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

  boolean isActivated(){
    return e.getStep() - actStep < deadSteps;
  }

  boolean isValid(){
    return valid;
  }

  double getProb(){
    return probability.get(curStep()) * e.CROSS_PROB;
  }

  double getCharge(){
    return charge.get(curStep());
  }

  double getLife(){
    return (1 - curStep() / (double) deadSteps);
  }

  int curStep(){
    return e.getStep() - actStep;
  }

  void reset(){
    actStep = -1 * deadSteps;;
  }

  PGraphics draw(PGraphics g, float x, float y, float size){
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
