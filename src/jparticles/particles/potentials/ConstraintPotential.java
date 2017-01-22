        /*
 * ConstraintPotential.java
 *
 * Created on 08 August 2001, 11:45
 */

package jparticles.particles.potentials;

import jparticles.particles.ParticleSystem;

/**
 *
 * @author  carlosv
 * @version
 */
public abstract class ConstraintPotential extends ParticlesPotentialTerm
{    
    private double energy ;
    private double [] constraintsCoeff;    
    protected int constraintsCount = 0;
    protected int baseDimension = 0;
    protected  ParticleSystem particles = null;
    //
    // Helping variables    
    //
    private double constraintValue;
    private double directionalDer;
    private double constraintCoeff;
    private double dampingCoeff;
    private double [] constraintGradient;
    private double [] constraintHessian;
    private double [] dampingsCoeff;
    private double [] particlesVel;
    private double constraintVel;
    private double timeInc;
    
    public ConstraintPotential(ParticleSystem particles, int constraintsCount)
    {
        this.particles = particles;
        baseDimension = particles.getParticlesCount()*particles.getParticleDimension();
        this.constraintsCount = constraintsCount;
        constraintsCoeff = new double[constraintsCount];
        dampingsCoeff = new double[constraintsCount];
        for(int i = 0; i < constraintsCount; i++)
        {
            constraintsCoeff[i] = 1.0;
            dampingsCoeff[i] = 1.0;
        }
    }   
    public void setConstraintCoefficient(int i, double value)
    {
        constraintsCoeff[i] = value;
    }
    public void setDampingsCoefficient(int i, double value)
    {
        dampingsCoeff[i] = value;
    }
    public void setConstraintsCeofficients(double value)
    {
        for(int i = 0; i < constraintsCount; i++) constraintsCoeff[i] = value;
    }
    public void setDampingsCoefficients(double value)
    {
        for(int i = 0; i < constraintsCount; i++) dampingsCoeff[i] = value;
    }
    public double getValue()
    {
        energy = 0.0;
        for(int i = 0; i < constraintsCount; i++)
        {                        
            constraintValue = getLocalValue(i);
            energy += constraintsCoeff[i]*constraintValue*constraintValue;
        }
        energy *= .5;
        return energy;
    }
    public void addGradient()
    {        
        double coeff;
        for(int i = 0; i < constraintsCount; i++)
        {               
            dampingCoeff = dampingsCoeff[i];            
            constraintVel = getDotLocalGradient(i,particles.getVelocity()); // used for damping
            constraintCoeff = constraintsCoeff[i];            
            constraintValue = getLocalValue(i);                       
            coeff = constraintCoeff*constraintValue;            
            coeff += dampingCoeff*constraintVel; // damping part            
            addMultiplyLocalGradient(i,coeff ,totalGradient);
        }
    }
    public void addHessian(double [] direction)
    {                     
        timeInc = particles.getTimeIncrement();
        double coeff;
        for(int i = 0; i < constraintsCount; i++)
        {            
            dampingCoeff = dampingsCoeff[i];
            constraintVel  = getDotLocalGradient(i,particles.getVelocity());
            
            constraintCoeff = constraintsCoeff[i];
            constraintValue = getLocalValue(i);
            directionalDer = getDotLocalGradient(i,direction);            
            
             coeff = constraintCoeff*directionalDer;
             coeff +=(dampingCoeff/timeInc)*directionalDer;
            addMultiplyLocalGradient(i,coeff,totalHessian);       
            
             coeff =   constraintCoeff*constraintValue;          
            coeff += dampingCoeff*constraintVel;       
            addMultiplyLocalHessian(i,coeff,direction,totalHessian);           
        }               
    }
    abstract double getLocalValue(int i);
    abstract double getDotLocalGradient(int i, double [] vector);
    abstract void addMultiplyLocalGradient(int i, double coeff, double [] vector);
    abstract void addMultiplyLocalHessian(int i, double coeff, double [] direction, double [] vector);
    
}
