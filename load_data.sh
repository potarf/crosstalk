for f in data/raw_data/*
do
 echo "Processing $f"
 ./source/dat_to_png.py --all $f data/plots/
done
