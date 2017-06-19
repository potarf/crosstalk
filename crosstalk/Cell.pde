class Cell{

  private int actStep;
  private int deadSteps;
  private boolean valid;

  public Cell(boolean valid){
    actStep = -1 * CELL_PROB.length;
    deadSteps = DEAD_TIME * STEPS_PER_NS;
    this.valid = valid;
  }
  
  public Cell(){
    actStep = -1 * CELL_PROB.length;
    deadSteps = DEAD_TIME * STEPS_PER_NS;
    this.valid = true;
  }

  boolean activate(){
    if(curStep() < deadSteps){
      return false;
    }
    actStep = e.getStep() + 1;
    return true;
  }

  boolean isActivated(){
    return e.getStep() - actStep < deadSteps;
  }

  boolean isValid(){
    return valid;
  }

  double getProb(){
    if(e.getStep() - actStep >= deadSteps || curStep() < 0){
      return 0;
    }
    return CELL_PROB[curStep()];
  }

  double getCharge(){
    if(e.getStep() - actStep >= deadSteps || curStep() < 0){
      return 0;
    }
    return CELL_Q[curStep()];
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
}
