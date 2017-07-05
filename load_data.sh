for f in data/raw_data/*
do
 echo "Processing $f"
 ./data/dat_to_png.py --all $f data/plots/
done
