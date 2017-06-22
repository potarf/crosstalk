class Pulse{
  int numPhotons;
  int startStep;
  NormExpression shape;

  public Pulse(int numPhotons, NormExpression shape){
    this.numPhotons = numPhotons;
    startStep = e.getStep();
    this.shape = shape;
  }

  void pulse(Cell[][] cells){
    int diameter = cells.length;
    int curStep = (e.getStep() - startStep) % (PULSE_LEN * STEPS_PER_NS);
    int photons = (int)(numPhotons * shape.get(curStep));

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

  void setNum(int number){
    this.numPhotons = number;
  }

  int getNum(){
    return numPhotons;
  }

}
