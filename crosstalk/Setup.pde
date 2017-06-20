// File of important constatnts for all classes

//Simulation constants
static final int STEPS_PER_NS = 1;
static final int PULSE_LEN = 25;

//Pulse constants
static final int PULSE_SIZE = 1000;
static final float PULSE_DIST[] = {0.00181951, 0.00923092, 0.0259625, 0.0487989,
                                   0.0698455, 0.0833084, 0.0880665, 0.0860273,
                                   0.079872, 0.0718294, 0.0633677, 0.0553056,
                                   0.0480232, 0.0416416, 0.0361452, 0.031455,
                                   0.0274701, 0.024088, 0.0212148, 0.0187681,
                                   0.0166778, 0.0148852, 0.0133417, 0.0120071,
                                   0.010848};


//Cell constants
static final int DEAD_TIME = 20;
static final double CELL_PROB[]   = {0.15, 0, 0, 0,
                                     0, 0, 0, 0,
                                     0, 0, 0, 0,
                                     0, 0, 0, 0,
                                     0, 0, 0, 0};
static final double CELL_Q[]      = {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};/*{0.026909, 0.0622872, 0.080451, 0.0897766,
                                     0.0945645, 0.093261, 0.0816195, 0.0714312,
                                     0.0625147, 0.0547112, 0.0478818, 0.0419049,
                                     0.036674, 0.0320961, 0.0280897, 0.0245833,
                                     0.0215147, 0.0188291, 0.0164787, 0.0144217};*/

final Environment e = new Environment();
