class Sipm{
  Cell cells[][];
  Pulse p;
  Environment e;

  private int diameter;

  private int numActive;
  private double curCharge;

  public Sipm(int diameter, Pulse p, NormExpression cellCharge, NormExpression cellProb, NormExpression cellRecharge, Environment e){
    this.diameter = diameter;
    this.p = p;
    this.e = e;

    cells = new Cell[diameter][diameter];
    
    double center = (diameter - 1) / 2.0;

    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        boolean inCircle = (x - center) * (x - center) + (y - center) * (y - center) - diameter * diameter /  4.0 <= 0;
        cells[x][y] = new Cell(inCircle, cellCharge, cellProb, cellRecharge, e);
      }
    }

  }

  void update(){
    p.pulse(cells);
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        if(cells[x][y].isValid()){
          cells[x][y].updateNeighbors(cells, x, y);
        }
      }
    }
  }

/*  void clear(){
    
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        cells[x][y].reset();
      }
    }
  }*/

  PGraphics draw(PGraphics g, int xOr, int yOr, int sideLen){

    float ratio = sideLen / (float) diameter;

    for(int x = 0; x < diameter; x++){
    
      for(int y = 0; y < diameter; y++){
        cells[x][y].draw(g, xOr + x * ratio, yOr + y * ratio, ratio);
      }
    }

    return g;
  }

  double getCharge(){
    double total = 0;
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        total += cells[x][y].getCharge();
      }
    }
    return total;
  }
}
