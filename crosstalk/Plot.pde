static class Plot{
  static public float drawPlot(PGraphics g, float[] data, int xOr, int yOr, int w, int h, int r, int green, int b, boolean clear, boolean setScale, float yGivenScale){
    int size = data.length;
    float xScale = w / (float)size;
    float yScale;
    if(!setScale){
      float maxY = 0;
      for(int i = 0; i < size; i++){
        if(data[i] > maxY)
          maxY = data[i];
      }
      yScale = (h * 0.85) / maxY;
    } else {
      yScale = yGivenScale;
    }

    if(clear){
      g.fill(255);
      g.stroke(255);
      g.rect(xOr, yOr, w, h);
    }
    
    g.stroke(r, green, b);
    for(int i = 0; i < size; i++){
      g.line(xOr + i * xScale, yOr + h - data[i] * yScale, xOr + (i + 1) * xScale, yOr + h - data[(i + 1) % size] * yScale);
    }
    
    g.stroke(0);
    g.line(xOr, yOr, xOr + w, yOr);
    g.line(xOr, yOr + h, xOr + w, yOr + h);
    g.line(xOr, yOr, xOr, yOr + h);
    g.line(xOr + w, yOr, xOr + w, yOr + h);

    return yScale;

  }
  static public float drawPlot(PGraphics g, float[] data, int xStr, int yStr, int w, int h, int r, int green, int b){
    return drawPlot(g, data, xStr, yStr, w, h, r, green, b, true, false, 0); 
  }
}
