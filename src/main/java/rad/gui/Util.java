package rad.gui;

public class Util {
    public static int clamp(int min, int val, int max)
    {
        if(val < min) return min;
        else if(val > max) return max;
        else return val;
    }
    public static int midpoint(int a, int b)
    {
        return a+((b-a)/2);
    }
    public static int lerp(int min,int max,double t)
    {
        return (int)(max*t + min*(1-t));
    }
    public static boolean inrange(int min,int x,int max)
    {
        return x == clamp(min,x,max);
    }
}
