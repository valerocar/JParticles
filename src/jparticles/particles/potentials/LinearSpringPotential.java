/*
 * SpringEnergy.java
 *
 * Created on 18 June 2001, 13:26
 */

package jparticles.particles.potentials;

/**
 *
 * @author  carlosv
 * @version
 */
import jparticles.gui.*;
import jparticles.gui.JDrawingPanel.*;

import java.awt.*;
import javax.swing.event.*;
import javax.swing.*;

/** This class is used to construct potential terms arising from
 * simple linear springs joining pairs of particles. The term linear spring
 * means that the potential for a spring between two particles is proportional
 * to the square of their separation. The class encapsulates not only
 * th potential term for a single spring, but also for a set of springs
 * for particle pairs in the system. If p1 and p2 are the positions
 * of the particles joined by spring i, then the potential for this pair is
 * <PRE>
 * 0.5*globalStiffnessFactor*stiffness[i]*(p2-p1)^2
 * </PRE>
 * where stiffness[i] is the stiffness of the i-th spring. The number
 * globalStiffnessFactor is used to change the stiffnes of all the springs
 * in the given proportion.
 */
public class LinearSpringPotential extends ParticlesPotentialTerm implements J2DDrawableObject
{
    private int particleDim;
    private double [] stiffness;
    private int [] particlePairs;
    private int springsCount;
    private Color linksColor = Color.lightGray;
    private double globalStiffnessFactor = 1.0;
    private JSlider globalSfiffFactorSlider;
    
    // Helping Variables
    private int indexA, indexB;
    private  StateToJ2DMap stateToJ2DMap;
    private int particleIndicesCount;
    private int [] j2DcoordA = new int[2];
    private int [] j2DcoordB = new int[2];
    private double [] particlePos;
    private double sliderScale = 1.0;
    /** Creates a LinearSpring object with a given number of springs for particles
     * living in a space of a specified dimension.
     * @param particleDim dimension of particles underlying space
     * @param springsCount number of springs
     */
    public LinearSpringPotential(int particleDim,int springsCount)
    {
        this.particleDim = particleDim;
        this.springsCount = springsCount;
        particleIndicesCount = 2*springsCount;
        particlePairs = new int[particleIndicesCount];
        this.stiffness = new double[springsCount];
        for(int i = 0; i < springsCount; i++) this.stiffness[i] = globalStiffnessFactor;
        particlePos = new double[particleDim];
        createGlbStffFctSlider();
    }
    /** Sets the particle pair for a given spring.
     * @param i the spring number
     * @param particleA particle number.
     * @param particleB particle number
     */
    public void setParticlePair(int i, int particleA, int particleB)
    {
        int k = 2*i;
        particlePairs[k] = particleA;
        k++;
        particlePairs[k] = particleB;
    }
    synchronized public double getValue()
    {
        if(globalStiffnessFactor == 0.0) return 0.0;
        double dx, stiff;
        double value;
        int k = 0;
        int count = 0;
        value = 0.0;
        while(k < particleIndicesCount)
        {
            indexA = particlePairs[k]*particleDim;
            k++;
            indexB =  particlePairs[k]*particleDim;
            k++;
            stiff = globalStiffnessFactor*stiffness[count];
            for(int i = 0; i < particleDim; i++)
            {
                dx = state[indexB]-state[indexA];
                value += stiff*dx*dx;
                indexA++; indexB++;
            }
            count++;
        }
        value /= 2.0;
        return value;
    }
    synchronized public void addGradient()
    {
        if(globalStiffnessFactor == 0.0) return;
        indexA = particlePairs[0]*particleDim;
        indexB =  particlePairs[1]*particleDim;
        double termGradient;
        int k = 0;
        int count = 0;
        double stiffval =0.0;
        while(k < particleIndicesCount)
        {
            indexA = particlePairs[k]*particleDim;
            k++;
            indexB =  particlePairs[k]*particleDim;
            k++;
            stiffval = globalStiffnessFactor*stiffness[count];
            for(int i = 0; i < particleDim; i++)
            {
                termGradient = stiffval*(state[indexB]-state[indexA]);
                totalGradient[indexA] -= termGradient;
                totalGradient[indexB] += termGradient;
                indexA++; indexB++;
            }
            count++;
        }
    }
    synchronized public void addHessian(double [] direction)
    {
        if(globalStiffnessFactor == 0.0) return;
        int k = 0;
        int count = 0;
        double stiffVal = 0.0;
        double termHessian;
        while(k < particleIndicesCount)
        {
            indexA = particlePairs[k]*particleDim;
            k++;
            indexB =  particlePairs[k]*particleDim;
            k++;
            stiffVal = globalStiffnessFactor*stiffness[count];
            for(int i = 0; i < particleDim; i++)
            {
                termHessian = stiffVal*(direction[indexB]-direction[indexA]);
                totalHessian[indexA] -= termHessian;
                totalHessian[indexB] += termHessian;
                indexA++; indexB++;
            }
            count++;
        }
    }
    /** Draws spring as Graphics2D-lines joining the corresponding particles.
     */
    public void draw(Graphics2D g,Object userObject)
    {
        if(state != null)
        {
            stateToJ2DMap = (StateToJ2DMap)userObject;
            int k = 0;
            g.setColor(linksColor);
            for(int i = 0; i < springsCount; i++)
            {
                System.arraycopy(state, particlePairs[k++]*particleDim,particlePos,0,particleDim);
                stateToJ2DMap.mapStateToJ2D(particlePos, j2DcoordA);
                System.arraycopy(state, particlePairs[k++]*particleDim,particlePos,0,particleDim);
                stateToJ2DMap.mapStateToJ2D(particlePos, j2DcoordB);
                g.drawLine(j2DcoordA[0],j2DcoordA[1],j2DcoordB[0],j2DcoordB[1]);
            }
        }
    }
    /** Sets the stiffness of a given spring.
     * @param i the spring number
     * @param value the stiffness value
     */
    public void setStiffness(int i, double value)
    {
        stiffness[i] = value;
    }
    /** Gets the stiffness value of a given spring.
     * @param i spring number
     * @return stiffness value
     */
    public double getStiffness(int i)
    {
        return stiffness[i];
    }
    /** Gets the value of globalStiffnessFactor.
     * @return the value of globalStiffnessFactor
     */
    public double getGlobalStiffnessFactor()
    {
        return globalStiffnessFactor;
    }
    /** Sets the globalStiffness factor.
     * @param globalStiffnessFactor the value for globalStiffnessFactor
     */
    synchronized public void setGlobalStiffnessFactor(double globalStiffnessFactor)
    {
        this.globalStiffnessFactor = globalStiffnessFactor;
    }
    /** Gets the color of the links for the springs joining the particles.
     * @return a color
     */
    public Color getLinksColor()
    {
        return linksColor;
    }
    /** Sets the color of the lines for the springs joining the particles.
     * @param linksColor a color
     */
    public void setLinksColor(Color linksColor)
    {
        this.linksColor = linksColor;
    }
    /** Gets a swing slider to control the global stiffness factor. This is done
     * according to the rule
     * <CODE>globalStiffnessFactor = sliderScale*sliderValue </CODE>. The
     * integer sliderValue varies between 0 and 100.
     * @return a JSlider swing component
     * @see jparticles.particles.potentials.LinearSpringPotential#setGSFSliderScale
     */
    public JSlider getGSFSlider()
    {
        return globalSfiffFactorSlider;
    }
    /** Sets the slider scale for the GSFSlider
     * @param scale the scale which the slider value must be multiplied to
     * obtain the global stiffness factor.
     * @see jparticles.particles.potentials.LinearSpringPotential#getGSFSlider
     */
    public void setGSFSliderScale(double scale)
    {
        sliderScale = scale;
    }
    private void createGlbStffFctSlider()
    {
        globalSfiffFactorSlider = new JSlider();
        globalSfiffFactorSlider.setMinimum(0);
        globalSfiffFactorSlider.setMaximum(100);
        globalSfiffFactorSlider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent evt)
            {
                JSlider slider = (JSlider)evt.getSource();
                slider.setToolTipText("global stiffness factor = "+getGlobalStiffnessFactor());
                setGlobalStiffnessFactor(sliderScale*(double)slider.getValue());
            }
        });
    }       
}
