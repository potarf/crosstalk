class Environment{
  private int step;
  
  //Simulation constants
  public final int STEPS_PER_NS = 1;
  public final int PULSE_LEN = 50;
  public final int CELL_DIAM = 195;
  
  //Cell constants
  public final int DEAD_TIME = 20;
  public final float CROSS_PROB = .047;

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
}
