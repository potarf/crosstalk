int time = 0; //nanoseconds
int numPhotons = 9000;
int activeCells = 0;
float py = 0;
float cells[][];
float temp[][];
int fieldWidth = 550;
int fieldHeight = 550;
int cSize = 2;
float decayRate = 0.05;
float spreadProb = 0.157 / 4;
float landauVal[] = {0.00181951, 0.00923092, 0.0259625, 0.0487989, 0.0698455, 0.0833084,
                      0.0880665, 0.0860273, 0.079872, 0.0718294, 0.0633677, 0.0553056,
                      0.0480232, 0.0416416, 0.0361452, 0.031455, 0.0274701, 0.024088,
                      0.0212148, 0.0187681, 0.0166778, 0.0148852, 0.0133417, 0.0120071,
                      0.010848}; 

void setup()
{
  size(1100, 550);
  cells = new float [fieldWidth / cSize][fieldHeight / cSize];
  temp = new float [fieldWidth / cSize][fieldHeight / cSize];
  for(int i = 0; i < fieldWidth / cSize; i++)
  for(int j = 0; j < fieldHeight / cSize; j++)
  {
    cells[i][j] = temp[i][j] = 0;
  }
  background(255);
  noStroke();
}

void draw()
{
  time++;
  activeCells = 0;
  //cells[(int) random(fieldWidth / cSize)][(int) random(fieldHeight / cSize)] = 1.1;
  //delay(100);
  for(int i = 0; i < fieldWidth / cSize; i++)
   for(int j = 0; j < fieldHeight / cSize; j++)
   {
     temp[i][j] = cells[i][j];
     if(cells[i][j]  <= 0)     
     {
       float prob = 0;
       if(i > 0 && cells[i - 1][j] > 1) prob += spreadProb;
       if(i < fieldWidth / cSize - 1 && cells[i + 1][j] > 1) prob += spreadProb;
       if(j > 0 && cells[i][j - 1] > 1) prob += spreadProb;
       if(j < fieldWidth / cSize - 1 && cells[i][j + 1] > 1) prob += spreadProb;
     
       //probabilistically fire
       if(random(1.0) < prob) temp[i][j] = 1 + decayRate + decayRate / 2.0;
     }
     
     
     //draw each cell
     float p = cells[i][j];
     //stroke(0);
     fill(255 - p*255, 255 - 30 * p, 255 - p * 255);
     noStroke();
     rect(i * cSize, j * cSize, cSize, cSize);
   }
   
  for(int i = 0; i < fieldWidth / cSize; i++)
   for(int j = 0; j < fieldHeight / cSize; j++){
     if(cells[i][j] > 0) temp[i][j] -= decayRate;
     cells[i][j] = temp[i][j];
     if(cells[i][j] >= 1) activeCells++;
   }
  pulse(time % 25);
    
  println(time);
  
  //draw borders
  stroke(0);
  line(fieldWidth, 0, fieldWidth, fieldHeight);
  line(fieldWidth, fieldHeight / 2, 2 * fieldWidth, fieldHeight / 2);
  
  int l = time % (fieldWidth / 2);
  
  if(l != 0) line(fieldWidth + 2 * l - 2, py, fieldWidth + 2 * l, fieldHeight/2 - activeCells / 1.5);
  fill(0, 30, 255);
  noStroke();
  //rect(fieldWidth + 2 * l + 1, 0, fieldWidth + 2 * l + 5, fieldHeight/2);
  //ellipse(fieldWidth + 2 * l, fieldHeight/2 - activeCells / 1.5, 1, 1);
  py = fieldHeight/2 - activeCells / 1.5;
}
void pulse(int time){
  int numPhotonsPulse = (int)(landauVal[time] * numPhotons);//(int) random(5000);
  for(int i = 0; i < numPhotonsPulse; i++){
    int x = (int)random(fieldWidth/cSize);
    int y = (int)random(fieldHeight/cSize);
    if(cells[x][y] <= 0) cells[x][y] = 1 + decayRate / 2.0;
  }
}