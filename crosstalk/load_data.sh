for f in data/*
do
 echo "Processing $f"
 ./dat_to_png.py --all $f
done
