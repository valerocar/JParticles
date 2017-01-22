/*
 * ParticlesLocalEnergy.java
 *
 * Created on 18 June 2001, 13:04
 */

package jparticles.particles.potentials;

/**
 *
 * @author  carlosv
 * @version 1.0
 */

import java.awt.*;

import jparticles.gui.JDrawingPanel.*;

/** This class abstracts the concept of a potential term in a particle system.
 * The total potential in a particle system is the constructed as a sum of
 * potential terms.
 */
public abstract class ParticlesPotentialTerm implements J2DDrawableObject
{            
    protected double [] totalGradient;
    /** The hessian to which the hessian of this term  will be added.
     * So the hessian of this term is a summand of the totalHessian.
     */
    protected double [] totalHessian;
    /** The state of the particle system.
     */
    protected double [] state;
    /**  This is an abstract class, so a potential term object cannot be constructed from it directly..
     */
    public ParticlesPotentialTerm()
    {
    }
    /** Sets the state variable, which is usually the state vector of the
     * particle system under consideration.
     * @param state the state of the system
     */
    public void setState(double [] state)
    {
        this.state = state;
    }
    /** Sets the totalGradient variable, which is usually the gradient vector of the
     * particle system under consideration.
     * @param gradient the gradient of the particle system
     */
    public void setTotalGradient(double [] gradient)
    {
        totalGradient = gradient;
    }
    public double [] getTotalGradient()
    {
    	return totalGradient;
    }
    /** Sets the totalHessian variable, which is usually the hessian vector of the
     * particle system under consideration.
     * @param hessian the hessian of the particle system
     */
    public void setTotalHessian(double [] hessian)
    {
        totalHessian = hessian;
    }
    /** Gets the value of the potential term.
     * @return value of this potential term
     */
    abstract public double getValue();
    /** Adds the gradient of this potential term to the totalGradient.
     */
    abstract public void addGradient();
    /** Adds the hessian of this potential term (in the given direction)
     * to the totalHessian. The direction is usually passed from a given
     * direction associated with the particle system.
     * @param direction a direction in the particle system state space
     */
    abstract public void addHessian(double [] direction);
    
    /** This method has to be overriden by the subclasses, is empty by default.
     */
    public void draw(Graphics2D g,Object userObject)
    {
    }           
}