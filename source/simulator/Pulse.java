package simulator;

import java.lang.Math;

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
    int curStep = (e.getStep() - startStep) % (e.timeToStep(e.getPulseLen()));
    double actPhot = numPhotons * shape.get(curStep) + remainder;
    int photons = (int)actPhot;
    remainder = actPhot - photons;

    for(int i = 0; i < photons; i++){
      int x = (int)((double)Math.random() * diameter);
      int y = (int)((double)Math.random() * diameter);
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

