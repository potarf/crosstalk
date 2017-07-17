#!/usr/bin/env python

#simpleplotter.py
#usage: ./simpleplotter.py [filename]
#input file format:
##first line: numberYDataSets xLabel yLabel
##following lines: xValue yValue1 yValue2 yValue3...

import matplotlib.pyplot as plt
import argparse

################################################################################
# Parse command line arguments #################################################
################################################################################
parser = argparse.ArgumentParser(prog='simpleplotter.py', \
        usage='%(prog)s [options] file out_dir', \
        description='Simply plot data from a text file.')
        
parser.add_argument('filename', metavar='file', type=str, \
        help='the input filename')

parser.add_argument('out_directory', metavar='out_dir', type=str, \
        help='the output directory')
        
parser.add_argument('--all', action='store_true', \
        help='all data on one plot (default is separate plots)')

args = parser.parse_args()
onePlot = args.all
filename = args.filename
outDir = args.out_directory
################################################################################

################################################################################
# Open file and run ############################################################
################################################################################
with open(filename, 'r') as infile:
    # read file and parse parameters ###########################################
    lines = infile.readlines()
    numPhot, numPlots, xLabel, yLabel, = lines[0].split()
    labels = lines[1].split()
    numPlots = int(numPlots)
    xValues = []
    yValues = [[] for i in range(numPlots)]
    ############################################################################
    
    for line in lines [2:]:
        l = line.split()
        xValues.append(l[0])
        for i in range(numPlots):
            charge = float(l[i + 1])
            correction = 1.00669 + \
                          1.34646 * 10 ** -5 * charge + \
                          1.57918 * 10 ** -10 * charge * charge;
            yValues[i].append(charge)
            
    #plt.figure(1)
    if onePlot:
        #plot on one plot
        for i in range(numPlots):
            plt.figure(i)
            plt.plot(xValues, yValues[i], label=labels[i + 1])
            plt.xlabel(xLabel)
            plt.ylabel(yLabel)
            plt.legend()
    else:
        #plot on multiple subplots
        for i in range(numPlots):
            plt.subplot(211 + i)
            plt.plot(xValues, yValues[i], label=labels[i])
            plt.xlabel(xLabel)
            plt.ylabel(yLabel)
    plt.savefig(outDir + filename[:(len(filename) - 4)] + ".png")
plt.show()
################################################################################
