package simulator;

import java.lang.Math;

/**
* The StatDist class stores statistical information about a distribution of
* values
*
* @author  John Lawrence, Jordan Potarf, Andrew Baas
* @version 1.0
* @since   05-07-2017
*/
class StatDist{

  // Statistical data
  private double current;
  private double mean;
  private double sum;
  private double sqSum;
  private double variance;

  // Number of data values
  private int size;
  
  /**
  * Constructor sets all values to 0
  */
  public StatDist(){
    current = mean = sum = sqSum = variance = size = 0;
  }

  /**
  * Adds another value to the distribution and calculates relevant values 
  *
  * @param value The value to add 
  */
  public void add(double value){
    
    // increment size
    size++;

    // Add to helper variable
    sum += value;

    // Update statistical information
    current = value;
    mean = sum / size;
    sqSum += current * current;
    variance = (sqSum - 2 * mean * sum + size * mean * mean) / size;
  }

  /**
  * Gets the last value added to the distribution
  *
  * @return The last value added to the distribution
  */
  public double getCurrent(){
    return current;
  }

  /**
  * Gets the mean of the distribution
  *
  * @return The mean of the distribution
  */
  public double getMean(){
    return mean;
  }

  /**
  * Gets the variance of the distribution
  *
  * @return The variance of the distribution
  */
  public double getVariance(){
    return variance;
  }

  /**
  * Resets all values to 0
  */
  public void clear(){
    current = mean = sum = sqSum = variance = size = 0;
  }
}
