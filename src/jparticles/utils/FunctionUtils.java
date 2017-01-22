/*
 * FunctionUtils.java
 *
 * Created on 07 October 2001, 14:29
 */

package jparticles.utils;

/**
 *
 * @author  carlos
 * @version 
 */
public class FunctionUtils 
{
    static public class FunctionMask
    {
        public double xmin, xmax;
        public int resolution;
        public double [] maskData;
        public FunctionMask(double xmin, double xmax, int resolution, double [] maskData)
        {
            this.xmin = xmin;
            this.xmax = xmax;
            this.resolution = resolution;
            this.maskData = maskData;
        }
    }
}
