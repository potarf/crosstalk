from ROOT import TFile, TTree, TH1D
import argparse
from array import array
import os

def datToRoot(iFilename, oFilename):
  f = TFile(oFilename, 'recreate')
  histo = f.mkdir("Histograms")
  data_dir = f.mkdir("Data")
  data_tree = TTree('data', "Data")
  data_dir.Add(data_tree)

  with open(iFilename, 'r') as infile:
    lines = infile.readlines()

    if len(lines[0].split()) >= 4:
      numPhot, numPlots, xLabel, yLabel, = lines[0].split()
    else:
      numPlots, xLabel, yLabel, = lines[0].split()

    labels = lines[1].split()

    data = [[] for i in range(len(labels))]

    for line in lines[2:]:
      values = line.split()
      for i in range(len(values)):
        data[i].append(float(values[i]))

    # Plug data into root tree
    arr_dat = []
    for i in range(len(labels)):
      arr_dat.append(array('f', [0.]))
      h = data_tree.Branch(labels[i], arr_dat[i], labels[i] + "/F")

    for j in range(len(data[0])):
      for k in range(len(labels)):
        arr_dat[k][0] = data[k][j]
      data_tree.Fill()
    
    data_dir.Write()
    # Make histograms of data
    for i in range(len(labels) - 1):
      temp = array('d')
      temp.fromlist(data[0])
      hist = TH1D(labels[i + 1] + "hist", labels[i+1] + " vs. time",
                    len(data[0]) - 1, temp)
      for j in range(len(data[i + 1])):
        hist.SetBinContent(j + 1, data[i + 1][j])
      histo.Add(hist)
    histo.Write()

  f.Close()


parser = argparse.ArgumentParser(prog='simpleplotter.py', \
        usage='%(prog)s  i_file o_file', \
        description='Simply plot data from a text file.')
        
parser.add_argument('in_file', metavar='i_file', type=str, \
        help='the input filename')

parser.add_argument('out_file', metavar='o_file', type=str, \
        help='the output filename')
 
args = parser.parse_args()
inFilename = args.in_file
outFilename = args.out_file

#for filename in sorted(os.listdir(inFilename)):
#  print(filename)
#  datToRoot(os.path.join(inFilename,filename), os.path.join(outFilename,os.path.basename(filename) + ".root"))
datToRoot(inFilename, outFilename)
