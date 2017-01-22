/*
 * SpatialConstraintPotential.java
 *
 * Created on 24 April 2002, 11:13
 */

package jparticles.particles.potentials;

/**
 *
 * @author  carlos
 */
/*
public class SpatialConstraintPotential extends ConstraintPotential
{
    private double [] localState;
    private double [] localDir;
    private double [] localGrad;
    private double [] localHess;
    private FunctionJet2 localFunction;
    private FunctionJet2 constraintFunction;
    private int particleDim;
    private int particlesCount;
    public SpatialConstraintPotential(ParticleSystem particles, FunctionJet2 function)
    {
        super(particles,particles.getParticlesCount());
        this.localFunction = function;
        particleDim = particles.getParticleDimension();
        particlesCount = particles.getParticlesCount();
        localState = new double[particleDim];
        localDir = new double[particleDim];
        for(int i = 0; i < particlesCount; i++) this.setConstraint(i,createConstraint(i));
    }
    private FunctionJet2 createConstraint(final int i)
    {
        constraintFunction = new FunctionJet2Adapter(baseDimension)
        {
            int index = i*particleDim;
            public void setState(double [] state)
            {
                super.setState(state);
                for(int k = 0; k <particleDim; k++)
                {
                    localState[k] = state[index+k];
                }
                localFunction.setState(localState);
            }
            public void updateValue()
            {
                localFunction.updateValue();
                value = localFunction.getValue();
            }
            public void updateGradient()
            {
                localFunction.updateGradient();
                localGrad = localFunction.getGradient();
                ArrayUtils.makeZero(gradient);                
                for(int k = 0; k < particleDim; k++) gradient[index+k] = localGrad[k];                
            }
            public void updateHessian(double [] dir)
            {
                for(int k = 0; k < particleDim; k++)
                {
                    localDir[k] = dir[index+k];
                }
                localFunction.updateHessian(localDir);
                localHess = localFunction.getHessian();
                ArrayUtils.makeZero(hessian);                
                for(int k = 0; k < particleDim; k++) hessian[index+k] = localHess[k];                
            }
        };
        return constraintFunction;
    }
}
*/