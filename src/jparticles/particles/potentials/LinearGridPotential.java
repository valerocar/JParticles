/*
 * SimpleGridPotential.java
 *
 * Created on 17 July 2001, 06:26
 */

package jparticles.particles.potentials;

import jparticles.particles.constraints.*;
import jparticles.particles.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
//import javax.media.j3d.*;

//import javax.vecmath.*;
//import com.sun.j3d.utils.riemannian.GeometryInfo;
//import jparticles.gui.Viewer3D;


/** This class creates a potential term for given particle system, by creating a
 * rectangular grid with the particles and joining the rows and columns with
 * linear snake potentials
 * @author carlos
 * @version 1.0
 * @see jparticles.particles.potentials.LinearSnakePotential
 */
public class LinearGridPotential extends ParticlesPotentialTerm
{    
    //
    //
    protected int [][] gridIndices;
    protected LinearSnakePotential  horizontalChains;
    protected LinearSnakePotential  verticalChains;
    protected int width, height;
    protected int particleDim;
    
    protected JSlider globalStretchFactorSlider;
    protected JSlider globalBendFactorSlider;
    protected double stretchSliderScale = 1.0;
    protected double bendSliderScale = 1.0;
    protected int columns, rows;
    
    /** Creates a LinearGridPotential with the specified parameters.
     * @param particleDim dimension of the space in which the particles live
     * @param gridIndices The indices of the particles arranged
     * in an array
     * @param columns the number of columns for the grid
     * @param rows the number of rows for the grid
     */
    public LinearGridPotential(int particleDim,int columns,int rows,int[][] gridIndices)
    {
        initialize(particleDim,columns,rows,gridIndices);
    }
    /** Creates a LinearGridPotential with the specified parameters.
     * @param particleDim dimension of the space in which the particles live
     * @param columns the number of columns for the grid
     * @param rows the number of rows for the grid
     */
    public LinearGridPotential(int particleDim,int columns,int rows)
    {
        int [][] tmpGridIndices = new int[columns][rows];
        int count = 0;
        for(int i = 0; i < columns; i++)
        {
            for(int j = 0; j < rows; j++)  tmpGridIndices[i][j] = count++;
        }
        initialize(particleDim,columns,rows,tmpGridIndices);
    }
    private void initialize(int particleDim,int width,int height,int[][] gridIndices)
    {
        this.columns = width;
        this.rows = height;
        this.particleDim = particleDim;
        this.gridIndices = (int[][])gridIndices.clone();
        this.width = width;
        this.height = height;
        createHorizontalChains();
        createVerticalChains();
        createGlbStrchFctSlider();
        createGlbBendFctSlider();
    }
    private void createHorizontalChains()
    {
        int [] horizontalChainSizes = new int[height];
        for(int i = 0; i < height ; i++) horizontalChainSizes[i] = width;
        int [] horizontalIndices = new int[width*height];
        int hCount = height-1;
        int count = 0;
        for(int h = 0; h < height ; h++)
        {
            for(int w = 0; w < width; w++)
            {
                horizontalIndices[count++] = gridIndices[w][h];
            }
        }
        horizontalChains = new LinearSnakePotential(particleDim,horizontalIndices,horizontalChainSizes);
    }
    private void createVerticalChains()
    {
        int [] verticalChainSizes = new int[width];
        for(int i = 0; i < width ; i++) verticalChainSizes[i] = height;
        int [] verticalIndices = new int[width*height];
        int wCount = width-1;
        int count = 0;
        for(int w = 0; w < width; w++)
        {
            for(int h = 0; h < height ; h++)
            {
                verticalIndices[count++] = gridIndices[w][h];
            }
        }
        verticalChains = new LinearSnakePotential(particleDim,verticalIndices,verticalChainSizes);
    }
    /** Gets the index of the particle at the given position.
     * @param i column number
     * @param j row number
     * @return particle index
     */
    public int getIndex(int i, int j)
    {
        return gridIndices[i][j];
    }
    public void setState(double [] state)
    {
        super.setState(state);
        horizontalChains.setState(state);
        verticalChains.setState(state);
    }
    public void setTotalGradient(double [] gradient)
    {
        super.setTotalGradient(gradient);
        horizontalChains.setTotalGradient(gradient);
        verticalChains.setTotalGradient(gradient);
    }
    public void setTotalHessian(double [] hessian)
    {
        super.setTotalHessian(hessian);
        horizontalChains.setTotalHessian(hessian);
        verticalChains.setTotalHessian(hessian);
    }
    
    public double getValue()
    {
        return (horizontalChains.getValue()+verticalChains.getValue());
    }
    public void addGradient()
    {
        horizontalChains.addGradient();
        verticalChains.addGradient();
    }
    public void addHessian(double [] direction)
    {
        horizontalChains.addHessian(direction);
        verticalChains.addHessian(direction);
    }
    /** Initializes a ParticlesNail to fix the grid boundary.
     * @param frameNail a ParticlesNail
     * @see jparticles.particles.constraints.ParticlesNail
     */
    public void fixBorders(ParticlesNail frameNail)
    {
        int maxW = width-1;
        int maxH = height-1;
        for(int i = 0; i < width; i++)
        {
            frameNail.fix(gridIndices[i][0]);
            frameNail.fix(gridIndices[i][maxH]);
        }
        for(int i = 0; i < height; i++)
        {
            frameNail.fix(gridIndices[0][i]);
            frameNail.fix(gridIndices[maxW][i]);
        }
    }
    public int[][] getGridIndices()
    {
        return gridIndices;
    }
    /** Sets the positions for boundary particles of the grid,
     * to the specified rectangle.
     * The lower left corner of the rectangle is (xmin,ymin) and the upper right
     * corner is (xmax,ymax). Observe tha you have to pass the underlying particle
     * system as a parameter to this method.
     * @param xmin x minimum value
     * @param xmax x maximum value
     * @param ymin y minimum value
     * @param ymax y maximum value
     * @param p the underlying particles system for the grid
     */
    public void setFrame(double xmin, double xmax, double ymin, double ymax, ParticleSystem p)
    {
        int maxW= width-1;
        int maxH = height-1;
        double []  pos = new double[particleDim];
        double [] zero = new double[particleDim];
        int index;
        double xStep =(xmax - xmin)/maxW;
        double yStep =(ymax - ymin)/maxH;
        pos[0] = xmin;
        for(int i = 0; i < width; i++)
        {
            pos[1] = ymin;
            index = gridIndices[i][0];
            p.setPosition(index,pos);
            p.setVelocity(index, zero);
            pos[1] = ymax;
            index = gridIndices[i][maxH];
            p.setPosition(index,pos);
            p.setVelocity(index, zero);
            pos[0] += xStep;
        }
        pos[1] = ymin;
        for(int i = 0; i < height; i++)
        {
            pos[0] = xmin;
            index = gridIndices[0][i];
            p.setPosition(index,pos);
            p.setVelocity(index, zero);
            pos[0] = xmax;
            index = gridIndices[maxW][i];
            p.setPosition(index,pos);
            p.setVelocity(index, zero);
            pos[1] += yStep;
        }
    }
    /** Sets the global stretch factor.
     * @param factor the global stretch factor
     */
    public void setGlobalStretchFactor(double factor)
    {
        horizontalChains.setGlobalStretchFactor(factor);
        verticalChains.setGlobalStretchFactor(factor);
    }
    /** Returns the global stretch factor.
     * @return the global stretch factor
     */
    public double getGlobalStretchFactor()
    {
        return horizontalChains.getGlobalStretchFactor();
    }
    /** Sets the global bend factor.
     * @param factor the global bend factor
     */
    public void setGlobalBendFactor(double factor)
    {
        horizontalChains.setGlobalBendFactor(factor);
        verticalChains.setGlobalBendFactor(factor);
    }
    /** Returns the global bend factor.
     * @return the global bend factor
     */
    public double getGlobalBendFactor()
    {
        return horizontalChains.getGlobalBendFactor();
    }
    public void draw(Graphics2D g,Object userObject)
    {
        horizontalChains.draw(g,userObject);
        verticalChains.draw(g,userObject);
    }
    /** Gets a swing slider to control the global stretch factor. This is done
     * according to the rule
     * <CODE>globalStretchFactor = scale*sliderValue </CODE>. The
     * integer sliderValue varies between 0 and 100.
     * @return a JSlider swing component
     */
    public JSlider getGSFSlider()
    {
        return globalStretchFactorSlider;
    }
    /**
     * Sets the scale for slider controlling the global stretch factor.
     * @param scale the scale which the slider value must be multiplied to
     * obtain the global stretch factor.
     */
    public void setGSFSliderScale(double scale)
    {
        stretchSliderScale = scale;
    }
    private void createGlbStrchFctSlider()
    {
        globalStretchFactorSlider = new JSlider();
        globalStretchFactorSlider.setMinimum(0);
        globalStretchFactorSlider.setMaximum(100);
        globalStretchFactorSlider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent evt)
            {
                JSlider slider = (JSlider)evt.getSource();
                slider.setToolTipText("global stiffness factor = "+getGlobalStretchFactor());
                setGlobalStretchFactor(stretchSliderScale*(double)slider.getValue());
            }
        });
    }
    /** Gets a swing slider to control the global bend factor. This is done
     * according to the rule
     * <CODE>globalBendFactor = scale*sliderValue </CODE>. The
     * integer sliderValue varies between 0 and 100.
     * @return a JSlider swing component
     */
    public JSlider getGBFSlider()
    {
        return globalBendFactorSlider;
    }
    /**
     * Sets the scale for slider controlling the global bend factor.
     * @param scale the scale which the slider value must be multiplied to
     * obtain the global bend factor.
     */
    public void setGBFSliderScale(double scale)
    {
        bendSliderScale = scale;
    }
    private void createGlbBendFctSlider()
    {
        globalBendFactorSlider = new JSlider();
        globalBendFactorSlider.setMinimum(0);
        globalBendFactorSlider.setMaximum(100);
        globalBendFactorSlider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent evt)
            {
                JSlider slider = (JSlider)evt.getSource();
                slider.setToolTipText("global bend factor = "+getGlobalBendFactor());
                setGlobalBendFactor(bendSliderScale*(double)slider.getValue());
            }
        });
    }
    /** Sets the horizontal stretch at the given position.
     * @param i grid column
     * @param j grid row
     * @param value stretch value
     * @see jparticles.particles.potentials.LinearSnakePotential#setStretch
     */
    public void setHorizontalStretch(int i, int j, double value)
    {
        horizontalChains.setSretch(j,i,value);
    }
    /** Sets the horizontal bend at the given position.
     * @param i grid column
     * @param j grid row
     * @param value stretch value
     * @see jparticles.particles.potentials.LinearSnakePotential#setBend
     */
    public void setHorizontalBend(int i, int j, double value)
    {
        verticalChains.setSretch(i,j,value);
    }
    /** Sets the vertical stretch at the given position.
     * @param i grid column
     * @param j grid row
     * @param value stretch value
     * @see jparticles.particles.potentials.LinearSnakePotential#setStretch
     */
    public void setVerticalStretch(int i, int j, double value)
    {
        horizontalChains.setBend(i,j,value);
    }
    /** Sets the vertical bend at the given position.
     * @param i grid column
     * @param j grid row
     * @param value stretch value
     * @see jparticles.particles.potentials.LinearSnakePotential#setBend
     */
    public void setVerticalBend(int i, int j, double value)
    {
        verticalChains.setBend(i,j,value);
    }
    public void setColor(Color color)
    {
        verticalChains.setColor(color);
        horizontalChains.setColor(color);
    }               
}
