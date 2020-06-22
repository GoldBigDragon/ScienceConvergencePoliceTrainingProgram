package goldbigdragon.github.io.util;

import java.util.Random;

public class RandomUtil {
    private Random r;

    public RandomUtil(){
        r = new Random();
    }

    public int random(int min, int max)
    {
        if(min<=max)
            return r.nextInt((max-min+1))+min;
        else
            return r.nextInt((min-max+1))+max;
    }

    public long random(long min, long max) {
        if(min<=max)
            return min + (long)(r.nextDouble() * (max - min));
        else
            return max + (long)(r.nextDouble() * (min - max));
    }

    public double random(double min, double max)
    {
        if(min<=max)
            return r.nextDouble()%(max-min+1) + min;
        else
            return r.nextDouble()%(min-max+1) + max;
    }
}
