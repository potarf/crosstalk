import os
import subprocess
import argparse

parser = argparse.ArgumentParser(
        description='Simulates a SiPM')
        
parser.add_argument('out_dir',
                    metavar = 'o_dir',
                    type    = str,
                    help    = 'The directory for data output'
                    )
parser.add_argument('--min',
                    action  = 'store',
                    default = 1000,
                    type    = int,
                    help    = 'Sets the minimum number of photons in the runs.\
                                Default is 1000'
                    )
parser.add_argument('--max',
                    action  = 'store',
                    default = 100000,
                    type    = int,
                    help    = 'Sets the maximum number of photons in the runs.\
                                Default is 100000'
                    )
parser.add_argument('--granularity', '-g',
                    action  = 'store',
                    default = 5,
                    type    = int,
                    help    = 'Sets the number of steps per nanosecond.\
                                Default is 5'
                    )
parser.add_argument('--step', '-s',
                    action  = 'store',
                    default = 1000,
                    type    = int,
                    help    = 'Sets the step size (num of photons) between runs.\
                                Default is 1000'
                    )
parser.add_argument('--pulses', '-p',
                    action  = 'store',
                    default = 10,
                    type    = int,
                    help    = 'Sets the nubmer of pulses per run.\
                                Default is 10'
                    )
parser.add_argument('--pixels', '-x',
                    action  = 'store',
                    default = 37994,
                    type    = int,
                    help    = 'Sets the number of pixels on a chip.\
                                Default is 37994'
                    )
parser.add_argument('--threads', '-t',
                    action  = 'store',
                    default = 1,
                    type    = int,
                    help    = 'Sets the number of threads used in evaluation.\
                                Default is 1'
                    )
parser.add_argument('--t1',
                    action  = 'store',
                    default = .0001,
                    type    = float,
                    help    = 'Sets the time constant for charge rise on pixel\
                                output. Default is .0001'
                    )
parser.add_argument('--t2',
                    action  = 'store',
                    default = .05,
                    type    = float,
                    help    = 'Sets the risetime for pixel pulse.\
                                Default is .05'
                    )
parser.add_argument('--t3',
                    action  = 'store',
                    default = .5,
                    type    = float,
                    help    = 'Sets the first time constant for pixel pulse \
                                dropoff. Default is .5'
                    )


parser.add_argument('--t4',
                    action  = 'store',
                    default = 5,
                    type    = float,
                    help    = 'Sets the second time constant for pixel pulse \
                                dropoff. Default is 5'
                    )


parser.add_argument('--t5',
                    action  = 'store',
                    default = .6,
                    type    = float,
                    help    = 'Sets the percentage of the contribution of the\
                                first time constant to the dropoff.\
                                Default is .6'
                    )

parser.add_argument('--noCross',
                    action  = 'store_true',
                    help    = 'Sets whether the crosstalk effects are included\
                                in the simulation.'
                    )

parser.add_argument('--noSat',
                    action  = 'store_true',
                    help    = 'Sets whether the saturation effects are included\
                                in the simulation.'
                    )


args = parser.parse_args()

threads = args.threads

proc = []

print(args.noSat)
print(args.noCross)

for i in range(threads):
  print("Starting " + str(i))
  print("java -jar dist/crosstalk.jar " + " " +
                    str(args.min + ( (i *(args.max - args.min)) / (args.step * threads)) * args.step + (0 if (i == 0) else args.step))+" "+
                    str(args.min + ( (i + 1) * ((args.max - args.min)) / (args.step * threads)) * args.step)+" "+
                    str(args.step)+" "+
                    str(args.pulses)+" "+
                    str(args.granularity)+" "+
                    str(args.pixels)+" "+
                    str(args.t1)+" "+
                    str(args.t2)+" "+
                    str(args.t3)+" "+
                    str(args.t4)+" "+
                    str(args.t5)+" "+
                    str(os.path.join(args.out_dir, ""))+" "+
                    ('f' if args.noCross else 't')+" "+
                    ('f' if args.noSat else 't'));

  proc.append(subprocess.Popen(["java", "-jar", "dist/crosstalk.jar",
                    str(args.min + ( (i *(args.max - args.min)) / (args.step * threads)) * args.step + (0 if (i == 0) else args.step)),
                    str(args.min + ( (i + 1) * ((args.max - args.min)) / (args.step * threads)) * args.step),
                    str(args.step),
                    str(args.pulses),
                    str(args.granularity),
                    str(args.pixels),
                    str(args.t1),
                    str(args.t2),
                    str(args.t3),
                    str(args.t4),
                    str(args.t5),
                    str(os.path.join(args.out_dir, "")),
                    ('f' if args.noCross else 't'),
                    ('f' if args.noSat else 't')]))

for p in proc:
  p.wait()
print("Finished")
