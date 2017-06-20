static class Plot{
  static public void drawPlot(PGraphics g, float[] data, int xOr, int yOr, int w, int h, int r, int green, int b, boolean clear){
    int size = data.length;
    float xScale = w / (float)size;

    float maxY = 0;
    for(int i = 0; i < size; i++){
      if(data[i] > maxY)
        maxY = data[i];
    }
    float yScale = (h * 0.85) / maxY;

    if(clear){
      g.fill(255);
      g.noStroke();
      g.rect(xOr, yOr, w, h);
    }
    
    for(int i = 0; i < size; i++){
      g.stroke(r, green, b);
      g.line(xOr + i * xScale, yOr + h - data[i] * yScale, xOr + (i + 1) * xScale, yOr + h - data[(i + 1) % size] * yScale);
    }

  }
  static public void drawPlot(PGraphics g, float[] data, int xStr, int yStr, int w, int h, int r, int green, int b){
    drawPlot(g, data, xStr, yStr, w, h, r, green, b, true); 
  }
}
