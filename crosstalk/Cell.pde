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

  boolean activate(boolean isPulse){
    if(curStep() < deadSteps){
      return false;
    }
    up    = random(1.0);
    right = random(1.0);
    down  = random(1.0);
    left  = random(1.0);
    actStep[actNum % actStep.length] = e.getStep() + 1;
    actCharge[actNum % actStep.length] = (float)getRecharge(); 
    actNum = actNum % actStep.length + 1;
    this.isPulse = isPulse;
    return true;
  }

  void updateNeighbors(Cell[][] cells, int x, int y){
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

  boolean isActivated(){
    return curStep() < deadSteps;
  }

  boolean isValid(){
    return valid;
  }

  double getProb(){
    return probability.get(curStep()) * curCharge() * e.CROSS_PROB;
  }

  double getCharge(){
    float total = 0;
    for(int i = 0; i < actStep.length; i++){
      if(actStep[i] != -1){
        total += charge.get(e.getStep() - actStep[i]) * actCharge[i];
      }
    }
    return charge.get(curStep());
  }

  double getRecharge(){
    return recharge.get(curStep());
  }

  double getLife(){
    return (1 - curStep() / (double) (e.DEAD_TIME * e.STEPS_PER_NS));
  }

  int curStep(){
    if(actNum == 0){
      return e.DEAD_TIME * e.STEPS_PER_NS + 1;
    }
    return e.getStep() - actStep[actNum - 1];
  }

  float curCharge(){
    if(actNum == 0){
      return 0;
    }
    return actCharge[actNum - 1];
  }

/*  void reset(){
    actStep = -1 * deadSteps;;
  }*/

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
