class HScrollbar {
  float barWidth, barHeight;    // width and height of bar
  float xPos, yPos;       // x and y position of bar
  
  float sliderWidth;
  float sliderPos;    // x position of slider
  float maxVal, minVal;
  
  boolean over;           // is the mouse over the slider?
  boolean locked;

  HScrollbar (float xPos, float yPos, float barWidth, float barHeight, float minVal, float maxVal, float curVal) {
    this.xPos = xPos;
    this.yPos = yPos;
    this.barWidth = barWidth;
    this.barHeight = this.sliderWidth = barHeight;
    this.maxVal = maxVal;
    this.minVal = minVal;
    this.sliderPos = (curVal - minVal) / (maxVal - minVal) * barWidth;
  }

  void update() {
    if (mousePressed && over) {
      locked = true;
    }
    if (!mousePressed) {
      locked = false;
    }
    if (overEvent() && !mousePressed) {
      over = true;
    } else {
      over = false;
    }
    if (locked) {
      sliderPos = constrain(mouseX - sliderWidth / 2, 0, barWidth - sliderWidth);
    }
  }

  float constrain(float val, float minv, float maxv) {
    return min(max(val, minv), maxv);
  }

  boolean overEvent() {
    if (mouseX > xPos && mouseX < xPos+barWidth &&
       mouseY > yPos && mouseY < yPos+barHeight) {
      return true;
    } else {
      return false;
    }
  }

  void display() {
    noStroke();
    fill(204);
    rect(xPos, yPos, barWidth, barHeight);
    if (over || locked) {
      fill(0, 0, 0);
    } else {
      fill(102, 102, 102);
    }
    rect(sliderPos, yPos, sliderWidth, sliderWidth);
  }

  float getPos() {
    // Convert sliderPos to be values between
    // 0 and the total width of the scrollbar
    return sliderPos;
  }
  
  float getValue(){
    //Interpolate a value on the scrollbar to a value between given min and max
    return (sliderPos - xPos) / (barWidth) * (maxVal - minVal) + minVal;
  }
}
