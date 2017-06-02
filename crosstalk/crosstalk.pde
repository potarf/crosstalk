// Global Constants
static final int SIM_WIDTH  = 550;
static final int SIM_HEIGHT = 550;

static final int PULSE_TIME = 25;       

static final int NUM_PHOTONS      = 30000;
static final float LANDAU_DIST[]  = {0.00181951, 0.00923092, 0.0259625, 0.0487989, 0.0698455, 0.0833084,
                                    0.0880665, 0.0860273, 0.079872, 0.0718294, 0.0633677, 0.0553056,
                                    0.0480232, 0.0416416, 0.0361452, 0.031455, 0.0274701, 0.024088,
                                    0.0212148, 0.0187681, 0.0166778, 0.0148852, 0.0133417, 0.0120071,
                                    0.010848};

static final int CELL_SIZE        = 2;
static final float DECAY_RATE     = 0.05;
static final float SPREAD_PROB    = .157 / 4;

static final float TEMP[][]       = new float [SIM_WIDTH / CELL_SIZE][SIM_HEIGHT / CELL_SIZE];

//Global Variables
static final float g_cells[][]    = new float[SIM_WIDTH / CELL_SIZE][SIM_HEIGHT / CELL_SIZE];
static final double g_active[]     = new double[PULSE_TIME];
int g_time; //nanoseconds
float g_py;

void setup()
{
  
  // Initialize window
  size(1100, 550);
  background(255);
  noStroke();
  
  // Initialize global values
  
  g_time = -1;
  g_py = 0;
  
  // Initialize cells
  for(int i = 0; i < SIM_WIDTH / CELL_SIZE; i++)
    for(int j = 0; j < SIM_HEIGHT / CELL_SIZE; j++)
      g_cells[i][j] = 0;
      
  //Initialize data values
  for(int i = 0; i < PULSE_TIME; i++)
    g_active[i] = 0;
}
void draw()
{
  
  // Update time and environment
  g_time++;
  pulse(g_cells, g_time % PULSE_TIME);
  updateActiveCells(g_time, g_active, g_cells);

  // Step cells
  stepCells(g_cells); 

  // Draw things
  drawBorders();
  drawCells(g_cells);
  drawPlots(g_active);
}

void pulse(float[][] cells, int time){
  int numPhotonsPulse = (int)(LANDAU_DIST[time] * NUM_PHOTONS);//(int) random(5000);
  for(int i = 0; i < numPhotonsPulse; i++){
    int x = (int)random(SIM_WIDTH/CELL_SIZE);
    int y = (int)random(SIM_HEIGHT/CELL_SIZE);
    if(cells[x][y] <= 0) cells[x][y] = 1 + DECAY_RATE / 2.0;
  }
}

void stepCells(float[][] cells){
  for(int i = 0; i < SIM_WIDTH / CELL_SIZE; i++){
    for(int j = 0; j < SIM_HEIGHT / CELL_SIZE; j++){
      
      TEMP[i][j] = cells[i][j];
      
      if(cells[i][j]  <= 0){
        
        // Check if fired from each bordering cell
        if(i > 0 && cells[i - 1][j] > 1){
          if(random(1.0) < SPREAD_PROB)
            TEMP[i][j] = 1 + DECAY_RATE + DECAY_RATE / 2.0;
        }

        if(i < SIM_WIDTH / CELL_SIZE - 1 && cells[i + 1][j] > 1){
          if(random(1.0) < SPREAD_PROB)
            TEMP[i][j] = 1 + DECAY_RATE + DECAY_RATE / 2.0;
        }
        
        if(j > 0 && cells[i][j - 1] > 1){
          if(random(1.0) < SPREAD_PROB)
            TEMP[i][j] = 1 + DECAY_RATE + DECAY_RATE / 2.0;
        }
        
        if(j < SIM_WIDTH / CELL_SIZE - 1 && cells[i][j + 1] > 1){
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

void drawPlots(double[] active){
  int scale = 22;
  float totalArea = 0;
  float numScale = 275.0 / NUM_PHOTONS * 6;
  float landScale = numScale;
  
  for(int i = 0; i < PULSE_TIME; i++){
    int l = i % (SIM_WIDTH / scale);
    
    fill(255);
    noStroke();
    rect(SIM_WIDTH + scale * l, 0, SIM_WIDTH + scale * l + scale, SIM_HEIGHT/2);
    stroke(255, 30, 0);
  
    if(i != PULSE_TIME - 1){
      line(SIM_WIDTH + scale * l, SIM_HEIGHT/2 - (float)active[i] * numScale, SIM_WIDTH + scale * l + scale, SIM_HEIGHT/2 - (float)active[i + 1] * numScale);
      totalArea += 0.5 * (active[i] + active[i + 1]);
      stroke(0, 30, 255);
      line(SIM_WIDTH + scale * l, SIM_HEIGHT/2 - (float)(LANDAU_DIST[i] * NUM_PHOTONS) * landScale, SIM_WIDTH + scale * l + scale, SIM_HEIGHT/2 - (float)(LANDAU_DIST[i + 1] * NUM_PHOTONS) * landScale);
    } else{
      line(SIM_WIDTH + scale * l, SIM_HEIGHT/2 - (float)active[i] * numScale, SIM_WIDTH + scale * l + scale, SIM_HEIGHT/2 - (float)active[0] * numScale);
      totalArea += 0.5 * (active[i] + active[0]);
      stroke(0, 30, 255);
      line(SIM_WIDTH + scale * l, SIM_HEIGHT/2 - (float)(LANDAU_DIST[i] * NUM_PHOTONS) * landScale, SIM_WIDTH + scale * l + scale, SIM_HEIGHT/2 - (float)(LANDAU_DIST[0] * NUM_PHOTONS) * landScale);
    }
  }
  println(totalArea);
}

void updateActiveCells(int time, double[] active, float[][] cells){
  
  int activeCells = 0;
  int iteration = time / 25;
  int loc = time % 25;


  for(int i = 0; i < SIM_WIDTH / CELL_SIZE; i++){
    
    for(int j = 0; j < SIM_HEIGHT / CELL_SIZE; j++){
    
      if(cells[i][j] > 1){
        activeCells++;
      }
    }
  }

  active[loc] = (double)(active[loc] * iteration + activeCells) / (double)(iteration + 1);
}
