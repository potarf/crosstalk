#!/usr/bin/env python

#simpleplotter.py
#usage: ./simpleplotter.py [filename]
#input file format:
##first line: numberYDataSets xLabel yLabel
##following lines: xValue yValue1 yValue2 yValue3...

import matplotlib.pyplot as plt
import argparse
import os

################################################################################
# Parse command line arguments #################################################
################################################################################
parser = argparse.ArgumentParser(prog='simpleplotter.py', \
        usage='%(prog)s [options] in_dir out_dir', \
        description='Simply plot data from a text file.')
        
parser.add_argument('in_dir', metavar='in_dir', type=str, \
        help='the input directory')

parser.add_argument('out_file', metavar='out_file', type=str, \
        help='the output file')
        
parser.add_argument('--all', action='store_true', \
        help='all data on one plot (default is separate plots)')

args = parser.parse_args()
onePlot = args.all
inDir = args.in_dir
outDir = args.out_file
################################################################################

################################################################################
# Open file and run ############################################################
################################################################################

out = open(outDir, 'w')
out.write("2 photons charge\n")


for filename in sorted(os.listdir(inDir)):
    with open(os.path.join(inDir,filename), 'r') as infile:
        # read file and parse parameters ###########################################
        lines = infile.readlines()
        numPhotons = lines[0].split()[0]
        tot1 = tot2 = 0
        
        for line in lines [1:]:
            l = line.split()
            tot1 += float(l[1]);
            tot2 += float(l[4]);
        out.write(str(numPhotons) + " " + str(tot1) + " " + str(tot2) + "\n")

out.close();
################################################################################
