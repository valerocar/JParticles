/*
 * SimpleSpringChain.java
 *
 * Created on 27 August 2001, 16:08
 */

package jparticles.particles.potentials;
import jparticles.gui.*;
import jparticles.gui.JDrawingPanel.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
/** This class is used to construct potential terms arising from chains of
 * particles, and which give control of the bend and strech properties of the
 * chain. We will refer to such a chain as a snake. The potential associated
 * to snakes is a quadratic function of the position of the particles.
 * The class encapsulates not only the potential term for a single snake
 * but also for multiple snakes in a particle system. There are two parts to the
 * potential term. The first, which measures the strech properties of an snake,
 * is of the form
 * <PRE>
 * 0.5*globalStretchFactor*stretch*(p2-p1)^2,
 * </PRE>
 * where <CODE>p1</CODE> and <CODE>p2</CODE> are the positions of particles, and
 * the number <CODE>globalStretchFactor</CODE> is used to change the intensity
 * of the stretch for all the snakes.
 * The number <CODE>stretch</CODE> is the local stretch for particles
 * <CODE>p1</CODE> and <CODE>p2</CODE>.
 *
 * The second part of the potential term, which measures the bend
 * properties of a snake, is of the form
 * <PRE>
 * 0.5*globalBendFactor*bend*(p3-p2)-(p2-1))^2,
 * </PRE>
 * where <CODE>p1</CODE>, <CODE>p2</CODE> and <CODE>p3</CODE>
 * are the particles positions, and the number <CODE>globalBendFactor</CODE>
 * is used to change the intensity of the bend
 * for all the snakes. The number <CODE>bend</CODE> is the local bend for
 * particles <CODE>p1</CODE>,<CODE>p2</CODE> and <CODE>p3</CODE>.
 * @author carlosv
 * @version 1.0
 */
public class LinearSnakePotential extends ParticlesPotentialTerm implements J2DDrawableObject
{
    private int [] particleIndices;
    private int [] snakeSizes;
    
    private double [][] stretch;
    private double [][] bend;
    
    private int snakesCount;
    private int particleIndicesCount;
    private int snakeBegins, snakeEnds;
    private int snakeSize;
    private int indexA, indexB, indexC;
    private int particleDim;
    private Color chainColor = Color.white;
    
    private double globalStretchFactor = 1.0;
    private double globalBendFactor = 1.0;
    private JSlider globalStretchFactorSlider;
    private JSlider globalBendFactorSlider;
    private double stretchSliderScale = 1.0;
    private double bendSliderScale = 1.0;
    
    //
    // Hepling Variables
    //
    private double [] chainStretch;
    private double [] chainBend;
    private int stretchIndex;
    private double stretchValue;
    private int bendIndex;
    private double bendValue;
    private int [] j2DcoordA = new int[2];
    private int [] j2DcoordB = new int[2];
    private double [] particlePos;
    private StateToJ2DMap stateToJ2DMap;
    
    /** Creates a LinearSnakePotential for the specified particle indices and
     * snake sizes.
     * The particles involved in the snakes are given by the array
     * <CODE>particleSequences</CODE>.
     * The snakes are constucted by partitioning this array into
     * <CODE> snakesCount = snakeSizes.length </CODE> parts of sizes
     * <CODE> {snakeSizes[0],...,snakeSizes[snakesCount]} </CODE>.
     * @param particleDim dimension of underlying space for particle system
     * @param particleIndices the indices the of particles involved
     * @param snakeSizes the snake sizes
     */
    public LinearSnakePotential(int particleDim,int[] particleIndices,int[] snakeSizes)
    {
        initialize(particleDim, particleIndices, snakeSizes);
        
    }
    /** Constructs a LinearSnakePotential with a single snake of
     * the given size. The particle indices used are
     * <CODE> particleIndices = {0,1,...,snakeSize-1}</CODE>.
     * @param particleDim dimension of underlying space for particle system
     * @param snakeSize the size of the snake
     */
    public LinearSnakePotential(int particleDim, int snakeSize)
    {
        int [] tmpsnakeSizes =
        {snakeSize};
        int [] tmpIndices = new int[snakeSize];
        for(int i  = 0; i < snakeSize; i++) tmpIndices[i] = i;
        initialize(particleDim, tmpIndices, tmpsnakeSizes);
    }
    /** Constructs a LinearSnakePotential with the specified snake sizes.
     * The particle indices used are
     * <CODE> particleIndices = {0,1,...,snakeSize-1}</CODE>.
     * @param particleDim dimension of underlying space for particle system
     * @param snakeSizes the sizes of the chains
     */
    public LinearSnakePotential(int particleDim,int[] snakeSizes)
    {
        int total = 0;
        for(int i = 0; i < snakeSizes.length; i++) total += snakeSizes[i];
        int [] tmpIndices = new int[total];
        for(int i  = 0; i < total; i++) tmpIndices[i] = i;
        initialize(particleDim, tmpIndices, snakeSizes);
    }
    
    private void initialize(int particleDim,int[] particleIndices,int[] snakeSizes)
    {
        this.particleDim = particleDim;
        this.particleIndices = (int []) particleIndices.clone();
        this.particleIndicesCount = particleIndices.length;
        this.snakeSizes = (int []) snakeSizes.clone();
        snakesCount = snakeSizes.length;
        checksnakeSizes();
        //
        // Initializing stretch distribution
        //
        stretch =new  double[snakesCount][];
        int tmp;
        double [] chainStretch;
        for(int i = 0; i< snakesCount; i++)
        {
            tmp = snakeSizes[i]-1;
            chainStretch = stretch[i] = new double[tmp];
            for(int j = 0; j < tmp; j++) chainStretch[j] = globalStretchFactor;
        }
        //
        // Initializing bend distribution
        //
        bend =new  double[snakesCount][];
        double [] chainBend;
        for(int i = 0; i< snakesCount; i++)
        {
            tmp = snakeSizes[i]-2;
            chainBend = bend[i] = new double[tmp];
            for(int j = 0; j < tmp; j++) chainBend[j] = globalBendFactor;
        }
        particlePos = new double[particleDim];
        createGlbStrchFctSlider();
        createGlbBendFctSlider();
        stateToJ2DMap = new StateToJ2DMap(particleDim)
        {
            double hCenter = 256.0;
            double vCenter = 256.0;
            double hScale = 256.0;
            double vScale = 256.0;
            public void mapStateToJ2D(double [] state, int [] java2Dcoord)
            {
                java2Dcoord[0] = (int)(hCenter + hScale*state[0]);
                java2Dcoord[1] = (int)(vCenter - vScale*state[1]);
            }
            public void mapJ2DtoState(int [] java2coord, double [] state)
            {
                state[0] = ((double)(java2coord[0]-hCenter))/hScale;
                state[1] = ((double)(-java2coord[1]+vCenter))/vScale;
            }
        };
    }
    private void checksnakeSizes()
    {
        int total = 0;
        for(int i = 0; i < snakesCount; i++) total += snakeSizes[i];
        if (total != particleIndicesCount)
        {
            System.out.println("Invalid chain sizes");
            System.exit(-1);
        }
    }
    
    /** Sets the stretch factor for a consecutive pair of particles in a given snake.
     *
     * @param snake the snake number
     * @param link the link composed of the particle pair
     * <CODE>
     * {particleIndices[link],particleIndices[link+1]}
     * </CODE>.
     * @param value the stretch value
     */
    public void setSretch(int snake, int link , double value)
    {
        stretch[snake][link] = value;
    }
    /** Sets the stretch at the given link for <CODE> snake = 0 </CODE>
     * @param link the link composed of the particle pair
     * <CODE>
     * {particleIndices[link],particleIndices[link+1]}
     * </CODE>.
     * @param value the stretch value
     */
    public void setStretch(int link, double value)
    {
        stretch[0][link] = value;
    }
    /** Gets the stretch factor for a consecutive pair of particles in a given snake.
     *
     * @param snake the snake number
     * @param link the link composed of the particle pair
     * <CODE>
     * {particleIndices[link],particleIndices[link+1]}
     * </CODE>.
     * @return the stretch value
     */
    public double getSretch(int snake, int link)
    {
        return stretch[snake][link];
    }
    /** Gets the stretch at the given link for <CODE> snake = 0 </CODE>
     * @param link the link composed of the particle pair
     * <CODE>
     * {particleIndices[link],particleIndices[link+1]}
     * </CODE>.
     * @return value the stretch value
     */
    public double getStretch(int link)
    {
        return stretch[0][link] ;
    }
    
    /** Sets the bend factor for a consecutive triple of particles in a given snake.
     *
     * @param snake the snake number
     * @param anglink the link composed of the particle triple
     * <CODE>
     * {particleIndices[anglink],particleIndices[anglink+1], particleIndices[anglink+2]}
     * </CODE>.
     * @param value the stretch value
     */
    public void setBend(int snake, int anglink, double value)
    {
        bend[snake][anglink] = value;
    }
    /** Sets the bend for <CODE> snake = 0 </CODE> at the given angular link.
     * @param anglink the link composed of the particle triple
     * <CODE>
     * {particleIndices[anglink],particleIndices[anglink+1], particleIndices[anglink+2]}
     * </CODE>.
     * @param value the stretch value
     */
    public void setBend(int anglink, double value)
    {
        bend[0][anglink] = value;
    }
    
    /** Gets the bend factor for a consecutive triple of particles in a given snake.
     *
     * @param snake the snake number
     * @param anglink the link composed of the particle triple
     * <CODE>
     * {particleIndices[anglink],particleIndices[anglink+1], particleIndices[anglink+2]}
     * </CODE>.
     * @return the stretch value
     */
    public double getBend(int snake, int anglink)
    {
        return bend[snake][anglink];
    }
    /** Gets the bend for <CODE> snake = 0 </CODE> at the given angular link.
     * @param anglink the link composed of the particle triple
     * <CODE>
     * {particleIndices[anglink],particleIndices[anglink+1], particleIndices[anglink+2]}
     * </CODE>.
     * @return the stretch value
     */
    public double getBend(int anglink)
    {
        return bend[0][anglink];
    }
    
    
    public double getValue()
    {
        double value = 0.0;
        double dcoord;
        snakeBegins = 0;
        snakeEnds = 0;
        //
        //   ADDING STRECH POTENTIAL
        //
        if(globalStretchFactor != 0.0)
        {
            for(int i = 0; i < snakesCount; i++)
            {
                snakeSize = snakeSizes[i];
                snakeEnds = snakeBegins + (snakeSize-1);
                chainStretch = stretch[i]; stretchIndex = 0;
                for(int j = snakeBegins; j < snakeEnds; j++)
                {
                    indexA = particleIndices[j]*particleDim;
                    indexB = particleIndices[j+1]*particleDim;
                    stretchValue = globalStretchFactor*chainStretch[stretchIndex++];
                    for(int k = 0; k < particleDim; k++)
                    {
                        dcoord = state[indexB++]-state[indexA++];
                        value += stretchValue*dcoord*dcoord;
                    }
                }
                snakeBegins += snakeSize;
            }
        }
        //
        //   ADDING BEND POTENTIAL
        //
        if(globalBendFactor != 0.0)
        {
            snakeBegins = 0;
            for(int i = 0; i < snakesCount; i++)
            {
                snakeSize = snakeSizes[i];
                snakeEnds = snakeBegins + (snakeSize-2);
                chainBend = bend[i]; bendIndex = 0;
                for(int j = snakeBegins; j < snakeEnds; j++)
                {
                    indexA = particleIndices[j]*particleDim;
                    indexB = particleIndices[j+1]*particleDim;
                    indexC = particleIndices[j+2]*particleDim;
                    bendValue = globalBendFactor*chainBend[bendIndex++];
                    for(int k = 0; k < particleDim; k++)
                    {
                        dcoord = state[indexA++]-2*state[indexB++]+state[indexC++];
                        value += bendValue*dcoord*dcoord;
                    }
                }
                snakeBegins += snakeSize;
            }
        }
        return (value/2.0);
    }
    public void addGradient()
    {
        double termGrad;
        snakeBegins = 0;
        snakeEnds = 0;
        //
        //   ADDING STRECH GRADIENT
        //
        if(globalStretchFactor != 0.0)
        {
            for(int i = 0; i < snakesCount; i++)
            {
                snakeSize = snakeSizes[i];
                snakeEnds = snakeBegins + (snakeSize-1);
                chainStretch = stretch[i]; stretchIndex = 0;
                for(int j = snakeBegins; j < snakeEnds; j++)
                {
                    indexA = particleIndices[j]*particleDim;
                    indexB = particleIndices[j+1]*particleDim;
                    stretchValue = globalStretchFactor*chainStretch[stretchIndex++];
                    for(int k = 0; k < particleDim; k++)
                    {
                        termGrad = stretchValue*(state[indexB]-state[indexA]);
                        totalGradient[indexA] -= termGrad;
                        totalGradient[indexB] += termGrad;
                        indexA++; indexB++;
                    }
                }
                snakeBegins += snakeSize;
            }
        }
        //
        //   ADDING BEND GRADIENT
        //
        if(globalBendFactor != 0.0)
        {
            snakeBegins = 0;
            for(int i = 0; i < snakesCount; i++)
            {
                snakeSize = snakeSizes[i];
                snakeEnds = snakeBegins + (snakeSize-2);
                chainBend = bend[i]; bendIndex = 0;
                for(int j = snakeBegins; j < snakeEnds; j++)
                {
                    indexA = particleIndices[j]*particleDim;
                    indexB = particleIndices[j+1]*particleDim;
                    indexC = particleIndices[j+2]*particleDim;
                    bendValue = globalBendFactor*chainBend[bendIndex++];
                    for(int k = 0; k < particleDim; k++)
                    {
                        termGrad = bendValue*(state[indexA]-2*state[indexB]+state[indexC]);
                        totalGradient[indexA] += termGrad;
                        totalGradient[indexB] -= 2*termGrad;
                        totalGradient[indexC] += termGrad;
                        indexA++; indexB++; indexC++;
                    }
                }
                snakeBegins += snakeSize;
            }
        }
    }
    public void addHessian(double [] direction)
    {
        double termHess;
        snakeBegins = 0;
        snakeEnds = 0;
        //
        //   ADDING STRETCH HESSIAN
        //
        if(globalStretchFactor != 0.0)
        {
            for(int i = 0; i < snakesCount; i++)
            {
                snakeSize = snakeSizes[i];
                snakeEnds = snakeBegins + (snakeSize-1);
                chainStretch = stretch[i]; stretchIndex = 0;
                for(int j = snakeBegins; j < snakeEnds; j++)
                {
                    indexA = particleIndices[j]*particleDim;
                    indexB = particleIndices[j+1]*particleDim;
                    stretchValue = globalStretchFactor*chainStretch[stretchIndex++];
                    for(int k = 0; k < particleDim; k++)
                    {
                        termHess = stretchValue*(direction[indexB]-direction[indexA]);
                        totalHessian[indexA] -= termHess;
                        totalHessian[indexB] += termHess;
                        indexA++; indexB++;
                    }
                }
                snakeBegins += snakeSize;
            }
        }
        //
        //   ADDING BEND HESSIAN
        //
        if(globalBendFactor != 0.0)
        {
            snakeBegins = 0;
            for(int i = 0; i < snakesCount; i++)
            {
                snakeSize = snakeSizes[i];
                snakeEnds = snakeBegins + (snakeSize-2);
                chainBend = bend[i]; bendIndex = 0;
                for(int j = snakeBegins; j < snakeEnds; j++)
                {
                    indexA = particleIndices[j]*particleDim;
                    indexB = particleIndices[j+1]*particleDim;
                    indexC = particleIndices[j+2]*particleDim;
                    bendValue = globalBendFactor*chainBend[bendIndex++];
                    for(int k = 0; k < particleDim; k++)
                    {
                        termHess = bendValue*(direction[indexA]-2*direction[indexB]+direction[indexC]);
                        totalHessian[indexA] += termHess;
                        totalHessian[indexB] -= 2*termHess;
                        totalHessian[indexC] += termHess;
                        indexA++; indexB++; indexC++;
                    }
                }
                snakeBegins += snakeSize;
            }
        }
    }
    
    public void draw(Graphics2D g,Object userObject)
    {
        if(state != null)
        {
            if(userObject != null) stateToJ2DMap = (StateToJ2DMap)userObject;            
            int k = 0;
            snakeBegins = 0;
            snakeEnds = 0;
            g.setColor(chainColor);
            for(int i = 0; i < snakesCount; i++)
            {
                snakeSize = snakeSizes[i];
                snakeEnds = snakeBegins + (snakeSize-1);
                for(int j = snakeBegins; j < snakeEnds; j++)
                {
                    System.arraycopy(state, particleIndices[j]*particleDim,particlePos,0,particleDim);
                    stateToJ2DMap.mapStateToJ2D(particlePos, j2DcoordA);
                    System.arraycopy(state, particleIndices[j+1]*particleDim,particlePos,0,particleDim);
                    stateToJ2DMap.mapStateToJ2D(particlePos, j2DcoordB);
                    g.drawLine(j2DcoordA[0],j2DcoordA[1],j2DcoordB[0],j2DcoordB[1]);
                }
                snakeBegins += snakeSize;
            }
        }
    }
    /** Returns the global stretch factor.
     * @return the global stretch factor
     */
    public double getGlobalStretchFactor()
    {
        return globalStretchFactor;
    }
    /** Sets the globalStretchFactor.
     * @param value the global stretch factor
     */
    public void setGlobalStretchFactor(double value)
    {
        globalStretchFactor = value;
    }
    /** Returns the global bend factor.
     * @return the global bend factor
     */
    public double getGlobalBendFactor()
    {
        return globalBendFactor;
    }
    /** Sets the globalBendFactor.
     * @param value the value for the global bend factor
     */
    public void setGlobalBendFactor(double value)
    {
        globalBendFactor = value;
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
     * Sets the scale for the slider controlling the global stretch factor.
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
     * Sets the scale for the slider controlling the global bend factor.
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
    public void setColor(Color color)
    {
        chainColor = color;
    }       
}