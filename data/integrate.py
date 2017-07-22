#!/usr/bin/env python

#simpleplotter.py
#usage: ./simpleplotter.py [filename]
#input file format:
##first line: numberYDataSets xLabel yLabel
##following lines: xValue yValue1 yValue2 yValue3...

import matplotlib.pyplot as plt
import argparse
import os
import scipy.stats as st

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
        
parser.add_argument('-c', '--corrected', action='store_true', \
        help='apply corrections')

args = parser.parse_args()
inDir = args.in_dir
outDir = args.out_file
correction = args.corrected
################################################################################

################################################################################
# Open file and run ############################################################
################################################################################

out = open(outDir, 'w')
out.write("2 photons charge\n")

out.write("num_photons charge act_photons\n")

ofile = []

for filename in sorted(os.listdir(inDir)):
    with open(os.path.join(inDir,filename), 'r') as infile:
        # read file and parse parameters ###########################################
        lines = infile.readlines()
        numPhotons = lines[0].split()[0]
        tot1 = tot2 = 0
        
        for line in lines [2:]:
            l = line.split()
            tot1 += float(l[1]);
            tot2 += float(l[4]);
        
        if correction:
            tot1 *= 1.00669 + \
                         1.34646 * 10 ** -5 * tot1 + \
                         1.57918 * 10 ** -10 * tot1 * tot1;        
        ofile.append([int(numPhotons), tot1, tot2])

xValues = []
yValues = []

for i in sorted(ofile):
    xValues.append(i[2])
    yValues.append(i[1])
    out.write(str(i[0]) + " " + str(i[1]) + " " + str(i[2]) + '\n')
out.close();

print(st.linregress(xValues, yValues))

################################################################################
