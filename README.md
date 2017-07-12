# HCAL SiPM Simulator                                                            
                                                                                 
This project is specifically designed to simulate the SiPM chips used in the     
HCAL scintillator detectors and generate both graphical and numerical output for 
analysis. It can also easily be modified to model a general cell/crosstalk 
system.

## Getting Started

These instructions will get you a copy of the project up and running on your 
local machine for development and testing purposes.

### Prerequisites

To run this software, you will need
```
Java 9      (For numerical output)
Processing  (For live graphical output)
Python      (For post-simulation analysis)
```

### Compiling and Running this Project

These code examples are run in the folder where the use wants the repository to be stored

Clone the repository

```Shell
$ git clone git@github.com:potarf/crosstalk.git
```

Move into the repository

```Shell
$ cd crosstalk
```

Compile the simulator

```Shell
$ ./compile.sh
```

### Obtaining Data

#### To run the simulator for numerical output

Execute this command with the appropriate arguments

```Shell
$ java -jar dist/crosstalk.jar [min_photons] [max_photons] [step_size] [granularity] [runs_per_data_set] [output_folder]
```

For example, to run a simulation with 
-    a granularity of 3 steps per nanosecond
-    5 pulses measured and averaged for each data set
-    with pulse sizes ranging from 1000 to 50000 photons in steps of 1000
-    stored in data/raw_data/

run this code

```Shell
$ java -jar dist/crosstalk.jar 1000 50000 1000 3 5 data/raw_data/
```

#### To run the simulator with a live, interactive graphical panel

Execute this command with processing installed and in path

```Shell
$ processing-java --sketch=crosstalk --run
```

Or open the folder 'crosstalk/crosstalk' in the processing IDE

## Authors

* **Jordan Potarf** - *Visualization and Simulation work* - [Potarf](https://github.com/Potarf)
* **John Lawrence** - *Simulation work* - [JLawrenc](https://github.com/JLawrenc)
* **Master Goldfish** - *Simulation work* - [bosonBaas](https://github.com/bosonBaas)
