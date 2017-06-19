class Environment{
  private int step;

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
