class Pulse{
  int numPhotons;
  int startStep;

  public Pulse(int numPhotons){
    this.numPhotons = numPhotons;
    startStep = e.getStep();
  }

  void pulse(Cell[][] cells){
    int diameter = cells.length;

    for(int i = 0; i < numPhotons; i++){
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
