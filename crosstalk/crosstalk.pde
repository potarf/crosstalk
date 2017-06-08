// Global Constants
static final int SIM_HEIGHT = 550;
static final int SIM_WIDTH  = 550;

static final int PULSE_TIME = 25;       

static final int MIN_PHOTONS      = 2000;
static final int MAX_PHOTONS      = 20000;
static final int STEP_PHOTONS     = 100;
static final int SIM_LIVES = -1 * PULSE_TIME;

static final float LANDAU_DIST[]  = {0.00181951, 0.00923092, 0.0259625, 0.0487989, 0.0698455, 0.0833084,
                                    0.0880665, 0.0860273, 0.079872, 0.0718294, 0.0633677, 0.0553056,
                                    0.0480232, 0.0416416, 0.0361452, 0.031455, 0.0274701, 0.024088,
                                    0.0212148, 0.0187681, 0.0166778, 0.0148852, 0.0133417, 0.0120071,
                                    0.010848};

static final int CELL_SIZE        = 3;
static final float DECAY_RATE     = 0.05;
static final float SPREAD_PROB    = .157 / 4;
static final int SIM_DIAM         = ((SIM_HEIGHT < SIM_WIDTH)?SIM_HEIGHT:SIM_WIDTH) / CELL_SIZE;

static final float TEMP[][]       = new float [SIM_WIDTH / CELL_SIZE][SIM_HEIGHT / CELL_SIZE];

//Global Variables
static final float g_cells[][]    = new float[SIM_WIDTH / CELL_SIZE][SIM_HEIGHT / CELL_SIZE];
static final float g_active[]    = new float[PULSE_TIME];
static final float g_actSqSum[]   = new float[PULSE_TIME];
static final float g_actSum[]     = new float[PULSE_TIME];
static final float g_actSig[]    = new float[PULSE_TIME];

float g_areaSqSum;
float g_areaSum;
float g_areaMean;
float g_areaSig;
float g_areaElements;

int g_time; //nanoseconds
float g_py;
int g_numPhotons      = MIN_PHOTONS;
PrintWriter output;

void setup(){
  
  // Initialize window
  size(1100, 550);
  background(255);
  noStroke();
  
  // Initialize output file
  output = createWriter("positions.txt"); 
  
  // Initialize global values
  
  g_time = -1;
  
  // Initialize cells
  for(int i = 0; i < SIM_WIDTH / CELL_SIZE; i++)
    for(int j = 0; j < SIM_HEIGHT / CELL_SIZE; j++)
      g_cells[i][j] = 0;
      
  //Initialize data values
  for(int i = 0; i < PULSE_TIME; i++){
    g_active[i] = 0;
    g_actSqSum[i] = 0;
    g_actSum[i]   = 0;
    g_actSig[i]  = 0;
  }
  
  g_areaSqSum     = 0;
  g_areaSum       = 0;
  g_areaMean      = 0;
  g_areaElements  = 0;
  g_py = 0;
}

void draw(){
  println(hi);
  // Update time and environment
  g_time++;
  pulse(g_cells, g_time % PULSE_TIME);
  int active = updateActiveCells(g_time, g_active, g_cells);

  // Step cells
  stepCells(g_cells); 

  // Draw things
  drawBorders();
  drawCells(g_cells);
  drawPlots(g_active);
  drawSignal(active, g_time);
  
  if((g_time + 1) % PULSE_TIME == 0){
    pulseActivity(g_active);
  }
  
  if(g_time == SIM_LIVES){
    output.println(pulseActivity(g_active));

    if(g_numPhotons == MAX_PHOTONS){
      exit();
    }

    g_numPhotons += STEP_PHOTONS;

    g_time = 0;
    // Initialize cells
    for(int i = 0; i < SIM_WIDTH / CELL_SIZE; i++)
      for(int j = 0; j < SIM_HEIGHT / CELL_SIZE; j++)
        g_cells[i][j] = 0;
      
      //Initialize data values
    for(int i = 0; i < PULSE_TIME; i++){
      g_active[i]   = 0;
      g_actSqSum[i] = 0;
      g_actSum[i]   = 0;
      g_actSig[i]  = 0;
    }
    
    g_areaSqSum     = 0;
    g_areaSum       = 0;
    g_areaMean      = 0;
    g_areaElements  = 0;
  }
}

void keyPressed(){
  output.flush(); // Writes the remaining data to the file
  output.close(); // Finishes the file
  exit(); // Stops the program
}

void pulse(float[][] cells, int time){
  float center = (SIM_DIAM - 1) / 2.0;
  int numPhotonsPulse = (int)(LANDAU_DIST[time] * g_numPhotons);//(int) random(5000);
  for(int i = 0; i < numPhotonsPulse; i++){
    int x = (int)random(SIM_WIDTH/CELL_SIZE);
    int y = (int)random(SIM_HEIGHT/CELL_SIZE);
    if((x - center) * (x - center) + (y - center) * (y - center) - SIM_DIAM * SIM_DIAM /  4.0 > 0){
      i--;
      continue;
    }
    if(cells[x][y] <= 0) cells[x][y] = 1 + DECAY_RATE / 2.0;
  }
}

void stepCells(float[][] cells){
  float center = (SIM_DIAM - 1) / 2.0;
  for(int i = 0; i < SIM_WIDTH / CELL_SIZE; i++){
    for(int j = 0; j < SIM_HEIGHT / CELL_SIZE; j++){
      
      TEMP[i][j] = cells[i][j];
      
      if(cells[i][j]  <= 0){
        float iSq = (i - center) * (i - center);
        float jSq = (j - center) * (j - center);
        float rSq = SIM_DIAM * SIM_DIAM / 4.0;
        if(iSq + jSq - rSq > 0){
          continue;
        }
		
        // Check if fired from each bordering cell
        if((i - center - 1) * (i - center - 1) + jSq - rSq <= 0  && cells[i - 1][j] > 1){
          if(random(1.0) < SPREAD_PROB)
            TEMP[i][j] = 1 + DECAY_RATE + DECAY_RATE / 2.0;
        }

        if((i - center + 1) * (i - center + 1) + jSq - rSq <= 0  && cells[i + 1][j] > 1){
          if(random(1.0) < SPREAD_PROB)
            TEMP[i][j] = 1 + DECAY_RATE + DECAY_RATE / 2.0;
        }
        
        if(iSq + (j - center - 1) * (j - center - 1) - rSq <= 0 && cells[i][j - 1] > 1){
          if(random(1.0) < SPREAD_PROB)
            TEMP[i][j] = 1 + DECAY_RATE + DECAY_RATE / 2.0;
        }
        
        if(iSq + (j - center + 1) * (j - center + 1) - rSq <= 0 && cells[i][j + 1] > 1){
          if(random(1.0) < SPREAD_PROB)
            TEMP[i][j] = 1 + DECAY_RATE + DECAY_RATE / 2.0;
        }
      }
    }
  }
   
  for(int i = 0; i < SIM_WIDTH / CELL_SIZE; i++){
    for(int j = 0; j < SIM_HEIGHT / CELL_SIZE; j++){
      if(cells[i][j] > 0) TEMP[i][j] -= DECAY_RATE;
        cells[i][j] = TEMP[i][j];
    }
  }
}

int getActiveCells(float[][] cells){
  int activeCells = 0;

  for(int i = 0; i < SIM_WIDTH / CELL_SIZE; i++){
    
    for(int j = 0; j < SIM_HEIGHT / CELL_SIZE; j++){

      if(cells[i][j] > 1){
         activeCells++;
      }
    }
  }

  return activeCells;
}

int updateActiveCells(int time, float[] avgAct,  float[][] cells){
  
  int activeCells = 0;
  int N = time / 25 + 1;
  int loc = time % 25;


  for(int i = 0; i < SIM_DIAM; i++){
    
    for(int j = 0; j < SIM_HEIGHT / CELL_SIZE; j++){
    
      if(cells[i][j] > 1){
        activeCells++;
      }
    }
  }

  avgAct[loc] = (float)(avgAct[loc] * (N - 1) + activeCells) / (float)(N);
  g_actSqSum[loc] += activeCells * activeCells;
  g_actSum[loc]   += activeCells;
  g_actSig[loc]   = (g_actSqSum[loc] - 2 * avgAct[loc] * g_actSum[loc] + N * avgAct[loc] * avgAct[loc]) / g_areaElements;
  
  return activeCells;
}

float pulseActivity(float[] active){
  float totalArea = 0;
  for(int i = 0; i < PULSE_TIME; i++){
    if(i != PULSE_TIME - 1){
      totalArea += 0.5 * (active[i] + active[i + 1]);
    } else{
      totalArea += 0.5 * (active[i] + active[0]);
    }
  }
  g_areaSqSum += totalArea * totalArea;
  g_areaSum   += totalArea;
  g_areaMean  = (g_areaMean * g_areaElements + totalArea)/(g_areaElements + 1);
  g_areaElements++;
  g_areaSig   = (g_areaSqSum - 2 * g_areaMean * g_areaSum + g_areaElements * g_areaMean * g_areaMean) / g_areaElements;
  
  return totalArea;
}

void drawCells(float[][] cells){
  
  for(int i = 0; i < SIM_WIDTH / CELL_SIZE; i++){
    
    for(int j = 0; j < SIM_HEIGHT / CELL_SIZE; j++){

      float p = cells[i][j];

      fill(255 - p*255, 255 - 30 * p, 255 - p * 255);
      noStroke();
      rect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }
  }
}

void drawBorders(){
  stroke(0);
  line(SIM_WIDTH, 0, SIM_WIDTH, SIM_HEIGHT);
  line(SIM_WIDTH, SIM_HEIGHT / 2, 2 * SIM_WIDTH, SIM_HEIGHT / 2);
}

void drawPlots(float[] active){
  int scale = 22;
  float numScale = 275.0 / g_numPhotons * 6;
  float landScale = numScale;
  
  for(int i = 0; i < PULSE_TIME; i++){
    int l = i % (SIM_WIDTH / scale);
    
    fill(255);
    noStroke();
    rect(SIM_WIDTH + scale * l + 1, 0, scale, SIM_HEIGHT/2);
    
    stroke(0, 0, 0);
    line(SIM_WIDTH + scale * l, SIM_HEIGHT/2 - (float)active[i] * numScale + sqrt(g_actSig[i]), SIM_WIDTH + scale * l , SIM_HEIGHT/2 - (float)active[i] * numScale - sqrt(g_actSig[i]));
  
    stroke(255, 30, 0);
    if(i != PULSE_TIME - 1){
      line(SIM_WIDTH + scale * l + 1, SIM_HEIGHT/2 - (float)active[i] * numScale, SIM_WIDTH + scale * l + 1 + scale, SIM_HEIGHT/2 - (float)active[i + 1] * numScale);
      stroke(0, 30, 255);
      line(SIM_WIDTH + scale * l + 1, SIM_HEIGHT/2 - (float)(LANDAU_DIST[i] * g_numPhotons) * landScale, SIM_WIDTH + scale * l + 1 + scale, SIM_HEIGHT/2 - (float)(LANDAU_DIST[i + 1] * g_numPhotons) * landScale);
    } else{
      line(SIM_WIDTH + scale * l + 1, SIM_HEIGHT/2 - (float)active[i] * numScale, SIM_WIDTH + scale * l + scale + 1, SIM_HEIGHT/2 - (float)active[0] * numScale);
      stroke(0, 30, 255);
      line(SIM_WIDTH + scale * l + 1, SIM_HEIGHT/2 - (float)(LANDAU_DIST[i] * g_numPhotons) * landScale, SIM_WIDTH + scale * l + scale + 1, SIM_HEIGHT/2 - (float)(LANDAU_DIST[0] * g_numPhotons) * landScale);
    }
  }
}

void drawSignal(int active, int time){
  int scale = 11;
  float numScale = 275.0 / g_numPhotons * 6;
  float landScale = numScale;
  
  int i = (time - 1 + PULSE_TIME * 2) % (PULSE_TIME * 2);
  
  int l = i % (SIM_WIDTH / scale);
  
  fill(255);
  noStroke();
  rect(SIM_WIDTH + scale * l + 1, SIM_HEIGHT / 2 + 1, scale, SIM_HEIGHT / 2);

  stroke(255, 30, 0);
  if(i != PULSE_TIME * 2 - 1){
    line(SIM_WIDTH + scale * l, SIM_HEIGHT - (float)g_py * numScale, SIM_WIDTH + scale * l + scale, SIM_HEIGHT - (float)active * numScale);
    stroke(0, 30, 255);
    line(SIM_WIDTH + scale * l, SIM_HEIGHT - (float)(LANDAU_DIST[i % PULSE_TIME] * g_numPhotons) * landScale, SIM_WIDTH + scale * l + scale, SIM_HEIGHT - (float)(LANDAU_DIST[(i + 1) % PULSE_TIME] * g_numPhotons) * landScale);
  } else{
    line(SIM_WIDTH + scale * l, SIM_HEIGHT - (float)g_py * numScale, SIM_WIDTH + scale * l + scale, SIM_HEIGHT - (float)active * numScale);
    stroke(0, 30, 255);
    line(SIM_WIDTH + scale * l, SIM_HEIGHT - (float)(LANDAU_DIST[i % PULSE_TIME] * g_numPhotons) * landScale, SIM_WIDTH + scale * l + scale, SIM_HEIGHT - (float)(LANDAU_DIST[0] * g_numPhotons) * landScale);
  }
  g_py = active;
  
}
