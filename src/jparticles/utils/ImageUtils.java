/*
 * ImageUtils.java
 *
 * Created on 19 July 2001, 15:44
 */

package jparticles.utils;

import java.awt.image.BufferedImage;
import java.awt.*;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;

/**
 *
 * @author  carlosv
 * @version
 */
/** Image utilities. All the methods of this class are static.
 */
public class ImageUtils
{
    /** This class is used to represent functions on given rectangular domains.
     */
    static public class FunctionWindow
    {
        /** a function
         */
        public jparticles.maths.calculus.Function function;
        /** x miminum
         */
        public double xMin;
        /** y miminum
         */
        public double yMin;
        /** x maximum
         */
        public double xMax;
        /** y maximum
         */
        public double yMax;
        /** Constructs a FunctionWindow with the specified data.
         * @param function a function
         * @param xMin x miminum
         * @param yMin y minimum
         * @param xMax x maximum
         * @param yMax y maximum
         */
        public FunctionWindow(jparticles.maths.calculus.Function function, double xMin, double yMin, double xMax, double yMax)
        {
            this.function = function;
            this.xMin = xMin;
            this.yMin = yMin;
            this.xMax = xMax;
            this.yMax = yMax;
        }
    }
    /** Stores a specified function window in a buffered image of a given size, .
     * Positive values of the function will appear in blue, negative in red, and
     * zero in black. This method does not create the buffered image,
     * it must be created before calling this method.
     * @param fw a function window
     * @param width the width of the image to be constructed
     * @param height the height of the image to be constructed
     * @param zScale The function value is multiplied by this number when
     * storing it as a pixel image value.
     * @param image the image to store the function values
     */
    public static void getImageFromFunction(jparticles.utils.ImageUtils.FunctionWindow fw, int width, int height, double zScale, java.awt.image.BufferedImage image)
    {
        java.awt.image.WritableRaster rst = image.getRaster();
        
        double lengthX = fw.xMax - fw.xMin;
        double lengthY = fw.yMax - fw.yMin;
        double xStep = lengthX/width;
        double yStep = lengthY/height;
        double x = fw.xMin;
        double y = fw.yMax;
        double fVal;
        jparticles.maths.calculus.Function f = fw.function;
        
        float [] color3f = new float[3];
        double [] state = new double[2];
        for(int j = 0; j < height; j++)
        {
            for(int i = 0; i < width; i++)
            {
                state[0] = x;
                state[1] = y;
                f.setState(state);
                f.updateValue();
                fVal = zScale*f.getValue();
                if(fVal >= 255.0) fVal = 255.0;
                if(fVal <= -255.0) fVal = -255.0;
                if(fVal >= 0)
                {
                    color3f[0] = (float)fVal;
                    color3f[1] = (float)fVal;
                    color3f[2] = (float)fVal;
                }
                else
                {
                    color3f[0] = (float)fVal;
                    color3f[1] = 0.0f;
                    color3f[2] = 0.0f;
                }
                
                rst.setPixel(i,j,color3f);
                x += xStep;
            }
            x = fw.xMin;
            y -= yStep;
        }
    }
    public static java.awt.image.BufferedImage loadImage(String imageFileName)
    {
        BufferedImage image = null;
        try
        {
            Image img = java.awt.Toolkit.getDefaultToolkit().createImage(imageFileName);
            // wait for image to load
            java.awt.MediaTracker tracker = new java.awt.MediaTracker(new java.awt.Frame());
            tracker.addImage(img,0);
            tracker.waitForAll();
            System.out.println("Image size  "  + img.getWidth(null)+"x"+img.getHeight(null));
            image = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.drawImage(img,0,0,null);
            
        }
        catch(Exception e)
        {
            System.out.println("Error getting image: " + e);
        }
        return image;
    }
    
    public static java.awt.image.BufferedImage loadImageWithAlpha(String imageFileName)
    {
        BufferedImage image = null;
        try
        {
            Image img = java.awt.Toolkit.getDefaultToolkit().createImage(imageFileName);
            // wait for image to load
            java.awt.MediaTracker tracker = new java.awt.MediaTracker(new java.awt.Frame());
            tracker.addImage(img,0);
            tracker.waitForAll();
            System.out.println("Image size  "  + img.getWidth(null)+"x"+img.getHeight(null));
            image = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            g.drawImage(img,0,0,null);
            
        }
        catch(Exception e)
        {
            System.out.println("Error getting image: " + e);
        }
        return image;
    }
    public static void saveImage(BufferedImage img, String filename)
    {
        java.io.FileOutputStream fos;
        try
        {
            fos = new java.io.FileOutputStream(filename);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
            encoder.encode(img);
            fos.flush();
            fos.close();
        }
        catch(java.io.FileNotFoundException e)
        {
            System.out.println(e);
        }
        catch(java.io.IOException ioe)
        {
            System.out.println(ioe);
        }       
    }
}
