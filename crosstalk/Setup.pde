// File of important constatnts for all classes

//Simulation constants
static final int STEPS_PER_NS = 1;
static final int PULSE_LEN = 25;

//Pulse constants
static final float PULSE_DIST[] = {0.00181951, 0.00923092, 0.0259625, 0.0487989,
                                    0.0698455, 0.0833084, 0.0880665, 0.0860273,
                                    0.079872, 0.0718294, 0.0633677, 0.0553056,
                                    0.0480232, 0.0416416, 0.0361452, 0.031455,
                                    0.0274701, 0.024088, 0.0212148, 0.0187681,
                                    0.0166778, 0.0148852, 0.0133417, 0.0120071,
                                    0.010848};


//Cell constants
static final int DEAD_TIME = 20;
static final double CELL_PROB[]   = {0.15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
static final double CELL_Q[] = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

final Environment e = new Environment();
