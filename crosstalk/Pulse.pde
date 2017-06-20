class Pulse{
  int numPhotons;
  int startStep;

  public Pulse(int numPhotons){
    this.numPhotons = numPhotons;
    startStep = e.getStep();
  }

  void pulse(Cell[][] cells){
    int diameter = cells.length;
    int curStep = (e.getStep() - startStep) % PULSE_LEN * STEPS_PER_NS;
    int photons = (int)(numPhotons * PULSE_DIST[curStep]);

    for(int i = 0; i < photons; i++){
      int x = (int)random(diameter);
      int y = (int)random(diameter);
      if(!cells[x][y].isValid()){
        i--;
      } else {
        cells[x][y].activate();
      }
    }

  }

}