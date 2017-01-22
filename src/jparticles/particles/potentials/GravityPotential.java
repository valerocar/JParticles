/*
 * GravityPotential.java
 *
 * Created on 08 July 2001, 20:04
 */

package jparticles.particles.potentials;
import jparticles.maths.calculus.*;

/**
 *
 * @author  carlos
 * @version 
 */
public class GravityPotential extends SpatialPotential
{    
    public GravityPotential(int particleDim, int particlesCount, final double [] gravityVector) 
    {
        super(particleDim, particlesCount);        
        spatialPotential = new  FunctionJet2Adapter(particleDim)
        {
            public void updateValue()
            {
                value = 0.0;
                for(int i = 0; i < baseDimension; i++)
                {
                    value -= gravityVector[i]*state[i];
                }
            }
            public void updateGradient()
            {
                for(int i = 0; i < baseDimension; i++) gradient[i] = -gravityVector[i];                
            }
            public void updateHessian(double [] direction)
            {
                for(int i = 0; i < baseDimension; i++) hessian[i] = 0.0;
            }            
        };
    }

}
