// File of important constatnts for all classes

//Simulation constants
static final int STEPS_PER_NS = 1;
static final int PULSE_LEN = 50;

//Pulse constants
static final int PULSE_SIZE = 10000;

//Cell constants
static final int DEAD_TIME = 20;
static final double CELL_PROB[]   = {0.2, 0, 0, 0,
                                     0, 0, 0, 0,
                                     0, 0, 0, 0,
                                     0, 0, 0, 0,
                                     0, 0, 0, 0};
static final double CELL_Q[]      = {1, 0, 0, 0,
                                     0, 0, 0, 0,
                                     0, 0, 0, 0,
                                     0, 0, 0, 0,
                                     0, 0, 0, 0};/*{0.026909, 0.0622872, 0.080451, 0.0897766,
                                     0.0945645, 0.093261, 0.0816195, 0.0714312,
                                     0.0625147, 0.0547112, 0.0478818, 0.0419049,
                                     0.036674, 0.0320961, 0.0280897, 0.0245833,
                                     0.0215147, 0.0188291, 0.0164787, 0.0144217};*/

final Environment e = new Environment();
