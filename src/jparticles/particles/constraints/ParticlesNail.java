  /*
   * ParticlesNail.java
   *
   * Created on 20 June 2001, 13:36
   */

package jparticles.particles.constraints;

import jparticles.maths.linearalgebra.*;

import java.awt.*;
import jparticles.particles.*;

  /** This class is used to nail particles in particles systems. By nail we mean
 * that the particles are forced to have zero velocity.
 *
 * @author carlosv
 * @version 1.0
 */
public class ParticlesNail extends LinearOperatorAdapter
{
    private Color freeColor = Color.orange;
    private Color fixedColor = Color.red;
    
    private int particleDim;
    private int particlesCount;
    private double [] op;
    private int fixedParticlesCount = 0;
    
    /** Constructs a ParticlesNail.
     * @param particleDim the dimension of the space in which the particles live
     * @param particlesCount the number of particles
     */
    public ParticlesNail(int particleDim, int particlesCount)
    {
        super(particleDim*particlesCount, particleDim*particlesCount);
        this.particleDim = particleDim;
        this.particlesCount = particlesCount;
        op = new double[baseDimension];
        for(int i = 0; i < baseDimension; i++) op[i] = 1.0;
    }
    private void setFixedParticles(int []  fixedParticles)
    {
        fixedParticlesCount = fixedParticles.length;
        for(int i = 0; i < baseDimension; i++)
        {
            op[i] = 1.0;
        }
        int index = 0;
        int count = fixedParticles.length;
        for(int i = 0; i < count; i++)
        {
            index = fixedParticles[i]*particleDim;
            for(int j = 0; j < particleDim; j++)
            {
                op[index]=0.0;
                index++;
            }
        }
    }
    /** Fixes the given particle.
     * @param particleIndex the number of the particle
     */
    public void fix(int particleIndex)
    {
        
        int posIndex = particleIndex*particleDim;
        if(op[posIndex] == 1.0)
        {
            fixedParticlesCount++;
            for(int j = 0; j < particleDim; j++)
            {
                op[posIndex]=0.0;
                posIndex++;
            }
        }
    }
    /** Relases the given particle.
     * @param particleIndex the particle number
     */
    public void release(int particleIndex)
    {
        int posIndex = particleIndex*particleDim;
        if(op[posIndex] == 0.0)
        {
            fixedParticlesCount--;
            for(int j = 0; j < particleDim; j++)
            {
                op[posIndex]=1.0;
                posIndex++;
            }
        }
    }
    
    public void transform(double [] in, double [] out)
    {
        for(int i = 0; i < baseDimension; i++)
        {
            if(op[i] == 0.0) out[i] = 0.0;
            else out[i] = in[i];
        }
    }
    
    /** Informs if the given particle is fixed or not.
     * @param particleNumber the number of the particle
     * @return true of false
     */
    public boolean isFixed(int particleNumber)
    {
        if(op[particleNumber*particleDim] == 1.0) return false;
        else return true;
    }
    /** Sets the colors for the particles system depending wether they are fixed
     * or not.
     * @param p a particle system
     * @see jparticles.particles.constraints.ParticlesNail#setFreeColor
     * @see jparticles.particles.constraints.ParticlesNail#setFixedColor
     */
    public void setParticleColors(ParticleSystem p)
    {
        for(int i = 0; i < particlesCount; i++)
        {
            if(isFixed(i))
            {
                p.setParticleColor(i,fixedColor);
            }
            else
            {
                p.setParticleColor(i,freeColor);
            }
        }
    }
    /** Sets the color for the free particles.
     * @param color a color
     * @see jparticles.particles.constraints.ParticlesNail#setParticleColors
     */
    public void setFreeColor(Color color)
    {
        freeColor = color;
    }
    /** Sets the color for the fixed particles.
     * @param color a color
     * @see jparticles.particles.constraints.ParticlesNail#setParticleColors
     */
    public void setFixedColor(Color color)
    {
        fixedColor = color;
    }
    /** Gets the color of the fixed particles.
     * @return color a color
     * @see jparticles.particles.constraints.ParticlesNail#setParticleColors
     */
    public Color getFixedColor()
    {
        return fixedColor;
    }
    /** Gets the color of the free particles.
     * @return color a color
     * @see jparticles.particles.constraints.ParticlesNail#setParticleColors
     */
    public Color getFreeColor()
    {
        return freeColor;
    }
    public void releaseAll()
    {
        for(int i = 0; i < particlesCount; i++) release(i);
        fixedParticlesCount = 0;
    }
    public int getFixedParticlesCount()
    {
        return fixedParticlesCount;
    }
}
