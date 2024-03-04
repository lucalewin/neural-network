package me.lucraft.neuralnetwork;

/**
 * 
 * @author Luca - Created on 06.07.2018
 * @version 1.1.5
 *
 */
public class Utils {

	/**
	 * 
	 * @param size
	 * @param init_value
	 * @return
	 */
    public static double[] createArray(int size, double init_value){
        if(size < 1) return null;
        double[] ar = new double[size];
        for(int i = 0; i < size; i++)
            ar[i] = init_value;
        return ar;
    }

    /**
     * 
     * @param size
     * @param lower_bound
     * @param upper_bound
     * @return
     */
    public static double[] createRandomArray(int size, double lower_bound, double upper_bound){
        if(size < 1) return null;
        double[] ar = new double[size];
        for(int i = 0; i < size; i++)
            ar[i] = randomValue(lower_bound, upper_bound);
        return ar;
    }

    /**
     * 
     * @param sizeX
     * @param sizeY
     * @param lower_bound
     * @param upper_bound
     * @return
     */
    public static double[][] createRandomArray(int sizeX, int sizeY, double lower_bound, double upper_bound){
        if(sizeX < 1 || sizeY < 1) return null;
        double[][] ar = new double[sizeX][sizeY];
        for(int i = 0; i < sizeX; i++)
            ar[i] = createRandomArray(sizeY, lower_bound, upper_bound);
        return ar;
    }

    /**
     * 
     * @param lower_bound
     * @param upper_bound
     * @return
     */
    public static double randomValue(double lower_bound, double upper_bound){
        return Math.random() * (upper_bound - lower_bound) + lower_bound;
    }

    /**
     * 
     * @param lowerBound
     * @param upperBound
     * @param amount
     * @return
     */
    public static Integer[] randomValues(int lowerBound, int upperBound, int amount) {
        lowerBound--;
        if(amount > (upperBound-lowerBound)) return null;
        Integer[] values = new Integer[amount];
        for(int i = 0; i< amount; i++) {
            int n = (int) (Math.random() * (upperBound - lowerBound + 1) + lowerBound);
            while(containsValue(values, n)) n = (int) (Math.random() * (upperBound-lowerBound + 1) + lowerBound);
            values[i] = n;
        }
        return values;
    }
    
    /**
     * 
     * @param <T>
     * @param ar
     * @param value
     * @return
     */
    public static <T extends Comparable<T>> boolean containsValue(T[] ar, T value){
        for(int i = 0; i < ar.length; i++)
            if(ar[i] != null)
                if(value.compareTo(ar[i]) == 0)
                    return true;
        return false;
    }

    /**
     * 
     * @param values
     * @return
     */
    public static int indexOfHighestValue(double[] values){
        int index = 0;
        for(int i = 1; i < values.length; i++)
            if(values[i] > values[index]) index = i;
        return index;
    }

}