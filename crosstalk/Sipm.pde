import java.util.*;
class Sipm{
  Cell cells[][];
  Pulse p;
  Environment e;

  private int diameter;

  private int numActive;
  private double curCharge;

  public Sipm(int diameter, Pulse p, NormExpression cellCharge, NormExpression cellProb, Environment e){
    this.diameter = diameter;
    this.p = p;
    this.e = e;

    cells = new Cell[diameter][diameter];
    
    double center = (diameter - 1) / 2.0;

    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        boolean inCircle = (x - center) * (x - center) + (y - center) * (y - center) - diameter * diameter /  4.0 <= 0;
        cells[x][y] = new Cell(inCircle, cellCharge, cellProb, e);
      }
    }

  }

  void update(){
    p.pulse(cells);
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        double spreadProb = cells[x][y].getProb();

        if(x - 1 > 0 && cells[x - 1][y].isValid()){ 
  		    if(random(1.0) < spreadProb)
            cells[x - 1][y].activate();
        }

        if(x + 1 < diameter && cells[x + 1][y].isValid()){ 
  		    if(random(1.0) < spreadProb)
            cells[x + 1][y].activate();
        }

        if(y - 1 > 0 && cells[x][y - 1].isValid()){ 
  		    if(random(1.0) < spreadProb)
            cells[x][y - 1].activate();
        }

        if(y + 1 < diameter && cells[x][y + 1].isValid()){ 
  		    if(random(1.0) < spreadProb)
            cells[x][y + 1].activate();
        } 
      }
    }
  }

  void clear(){
    
    for(int x = 0; x < diameter; x++){
      for(int y = 0; y < diameter; y++){
        cells[x][y].reset();
      }
    }
  }

  PGraphics draw(PGraphics g, int xOr, int yOr, int sideLen){

    float ratio = sideLen / (float) diameter;

    for(int x = 0; x < diameter; x++){
    
      for(int y = 0; y < diameter; y++){

        float p = (float)cells[x][y].getLife();

        fill(255 - p*255, 255 - 30 * p, 255 - p * 255);
        noStroke();
        rect(xOr + x * ratio,
             yOr + y * ratio,
             ratio,
             ratio);
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
