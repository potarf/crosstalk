#!/usr/bin/env python

#simpleplotter.py
#usage: ./simpleplotter.py [filename]
#input file format:
##first line: numberYDataSets xLabel yLabel
##following lines: xValue yValue1 yValue2 yValue3...

import matplotlib.pyplot as plt
from matplotlib.widgets import Button
import argparse
import os

def plotfile(filename):
    with open(filename, 'r') as infile:
        # read file and parse parameters #######################################
        lines = infile.readlines()
        
        if len(lines[0].split()) >= 4:
            numPhot, numPlots, xLabel, yLabel, = lines[0].split()
        else:
            numPlots, xLabel, yLabel, = lines[0].split()

        labels = lines[1].split()
        
        toPlot = []
    
        if "all" in plotNames:
            for i in range(len(labels)):
                found = not (labels[i] in plotNames)
                toPlot.append(found) 
        else:
            for i in range(len(labels)):
                found = labels[i] in plotNames
                toPlot.append(found)
        
        numPlots = int(numPlots)
        xValues = []
        yValues = [[] for i in range(len(labels))]
        ########################################################################
        
        for line in lines [2:]:
            l = line.split()
            xValues.append(l[0])
            for i in range(len(yValues)):
                charge = float(l[i])
                yValues[i].append(charge)
                
        fig = plt.figure(1)
        if onePlot:
            #plot on one plot
            for i in range(len(yValues)):
                #plt.figure(i)
                if toPlot[i]:
                    plt.plot(xValues, yValues[i], label=labels[i])
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

 

################################################################################
# Parse command line arguments #################################################
################################################################################
parser = argparse.ArgumentParser(
            description='Simply plot data from a text file.')

parser.add_argument('--plots',
                    action  = 'store',
                    default = ["all"],
                    type    = str,
                    nargs   = '*',
                    help    = 'Names of data to plot'
                    )

parser.add_argument('filename', metavar='file', type=str, \
        help='the input filename')

parser.add_argument('out_directory', metavar='out_dir', type=str, \
        help='the output directory')
        
parser.add_argument('--all', action='store_true', \
        help='all data on one plot (default is separate plots)')


args = parser.parse_args()

plotNames = args.plots
onePlot   = args.all
inLoc     = args.filename
outDir    = args.out_directory



################################################################################

################################################################################
# Open file and run ############################################################
################################################################################

if os.path.isdir(inLoc):
    for filename in os.listdir(inLoc):
        fig, ax = plt.subplots()
        plotfile(os.path.join(inLoc, filename))
        plt.savefig(os.path.join(outDir, os.path.basename(filename).split('.')[0]) + 
              ".png")
        plt.close();
else:
    fig, ax = plt.subplots()
    plotfile(inLoc)
    plt.show()

################################################################################
