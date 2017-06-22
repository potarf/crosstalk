class Cell{

  private int actStep;
  private int deadSteps;
  private boolean valid;
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
    return probability.get(curStep()) * 0.04;
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
}
