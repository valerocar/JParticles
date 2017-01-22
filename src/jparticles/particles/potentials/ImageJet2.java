/*
 * ImagePotential.java
 *
 * Created on February 12, 2002, 11:24 AM
 */

package jparticles.particles.potentials;

import java.awt.image.BufferedImage;

import java.awt.image.WritableRaster;
import jparticles.gui.StateToJ2DMap;
import jparticles.maths.calculus.FunctionJet2Adapter;
import jparticles.image.ImageFilter;

/**
 *
 * @author  carlosv
 */
public class ImageJet2 extends FunctionJet2Adapter
{
    private BufferedImage image;
    private WritableRaster raster;
    private ImageFilter filter;
    private double [] pixel = new double[3];
    private int [] j2dcoord = new int[2];
    private StateToJ2DMap map;
    private double dx, dy;
    private double factor = 1.0;

    private double [] pos = new double[3];

    public ImageJet2(BufferedImage image, ImageFilter filter)
    {
        super(2);
        this.image = image;
        this.filter = filter;
        this.map = map;
        raster = image.getRaster();
        int hSize = image.getWidth();
        int vSize = image.getHeight();
        map = new StateToJ2DMap(hSize/2,vSize/2,hSize/2,vSize/2);
        dx = 2.0/hSize;
        dy = 2.0/vSize;
    }

    public void updateValue()
    {
        value =  getValue(state);
    }
    public void updateGradient()
    {
        pos[0] = state[0]+dx;
        pos[1] = state[1];
        gradient[0] = (getValue(pos)-getValue(state))/dx;
        pos[0] = state[0];
        pos[1] = state[1]+dy;
        gradient[1] = (getValue(pos)-getValue(state))/dy;
    }

    public StateToJ2DMap getStateToJ2DMap()
    {
        return map;
    }
    public void updateHessian(double[] direction)
    {
    }
    private double getValue(double [] pos)
    {
        if(pos[0] <= -1.0) return 0.0;
        if(pos[0] >= 1.0) return 0.0;
        if(pos[1] <= -1.0) return 0.0;
        if(pos[1] >= 1.0) return 0.0;
        map.mapStateToJ2D(pos,j2dcoord);
        raster.getPixel(j2dcoord[0], j2dcoord[1],pixel);
        return factor*filter.filter(pixel);
    }
    public void setFactor(double factor)
    {
        this.factor = factor;
    }
}
