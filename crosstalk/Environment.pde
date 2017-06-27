class Environment{
  private int step;
  
  //Simulation constants
  public final int STEPS_PER_NS = 2;
  public final int PULSE_LEN = 125;
  public final int CELL_DIAM = 195;
  
  //Cell constants
  public final int RISE_TIME = 4;
  public final int DEAD_TIME = 40;
  private float crossProb = .046;

  void Evironment(){
    step = 0;
  }

  int increment(){
    step++;
    return step;
  }

  int getStep(){
    return step;
  }

  double getTime(){
    return step / (double)(STEPS_PER_NS);
  }

  int pulseNum(){
    return (step / STEPS_PER_NS) / PULSE_LEN;
  }
  
  float getCrossProb(){
    return crossProb;
  }

  void setCrossProb(float crossProb){
    this.crossProb = crossProb;
  }
}
