class Cell{
  double actStep;

  void activate(int step){
    actStep = step;
  }

  double getProb(int step){
    if(step - actStep >= cellProb.size()){
      return 0;
    }
    return cellProb[step - actStep];
  }

  double getCharge(int step){
    if(step - actStep >= cellCharge.size()){
      return 0;
    }
    return cellCharge[step - actStep];
  }
}
