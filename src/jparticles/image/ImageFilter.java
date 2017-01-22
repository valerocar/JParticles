/*
 * ImageFilter.java
 *
 * Created on 26 September 2001, 11:46
 */

package jparticles.image;

import java.awt.image.*;
/**
 *
 * @author  carlosv
 * @version 
 */
public abstract class ImageFilter implements ImageProcess
{        
    private WritableRaster raster;
    private double [] pixel = new double[3];
    
    abstract public double filter(double [] pixel);   
    public double getValue(BufferedImage image, int x, int y)
    {
        if((x < 0) || (x >= image.getWidth()))return 0.0;
        if((y < 0) || (y >= image.getHeight())) return 0.0;        
        raster = image.getRaster();
        raster.getPixel(x,y,pixel);        
        return filter(pixel);
    }    
    public void process(BufferedImage source, BufferedImage target)
    {
        int width = source.getWidth();
        int height = source.getHeight();
        double [] sourcePixel = new double[3];
        double [] filteredPixel = new double[3];
        double filteredVal;
        
        WritableRaster sourceRaster = source.getRaster();
        WritableRaster targetRaster = target.getRaster();        
        for(int i = 0;i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                sourceRaster.getPixel(i,j,sourcePixel);
                filteredVal = filter(sourcePixel);
                filteredPixel[0] = filteredVal;
                filteredPixel[1] = filteredVal;
                filteredPixel[2] = filteredVal;
                targetRaster.setPixel(i,j,filteredPixel);
            }
        }
    }
    
}

