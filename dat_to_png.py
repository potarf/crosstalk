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
        usage='%(prog)s [options] file', \
        description='Simply plot data from a text file.')
        
parser.add_argument('filename', metavar='file', type=str, \
        help='the input filename')
        
parser.add_argument('--all', action='store_true', \
        help='all data on one plot (default is separate plots)')

args = parser.parse_args()
onePlot = args.all
filename = args.filename
################################################################################

################################################################################
# Open file and run ############################################################
################################################################################
with open(filename, 'r') as infile:
    # read file and parse parameters ###########################################
    lines = infile.readlines()
    numPlots, xLabel, yLabel, = lines[0].split()
    numPlots = int(numPlots)
    xValues = []
    yValues = [[] for i in range(numPlots)]
    ############################################################################
    
    for line in lines [1:]:
        l = line.split()
        xValues.append(l[0])
        for i in range(numPlots):
            yValues[i].append(l[i + 1])
            
    plt.figure(1)
    if onePlot:
        #plot on one plot
        for i in range(numPlots):
            plt.plot(xValues, yValues[i])
            plt.xlabel(xLabel)
            plt.ylabel(yLabel)
    else:
        #plot on multiple subplots
        for i in range(numPlots):
            plt.subplot(211 + i)
            plt.plot(xValues, yValues[i])
            plt.xlabel(xLabel)
            plt.ylabel(yLabel)
    plt.savefig("png/" + filename[5:(len(filename) - 4)] + ".png")
################################################################################
