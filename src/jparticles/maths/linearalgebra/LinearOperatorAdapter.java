/*
 * LinearOperatorAdapter.java
 *
 * Created on 08 June 2001, 15:00
 */

package jparticles.maths.linearalgebra;

/** Gives a basic implementation of LinearOperator. This is mainly a helper class used
 * to ease the construction of linear operators. The user is meant to derive this class
 * to construct a linear operator, and use its protected variables when overloading the
 * transform method.
 * @author carlosv
 * @version 1.0
 */
public abstract class LinearOperatorAdapter implements LinearOperator
{
/** The dimension of the domain of the operator.
 */    
    protected int baseDimension;
/** The dimension of the range of the operator.
 */    
    protected int targetDimension;
/** Creates a LinearOperator with specified domain and range dimensions.
 * @param baseDim the dimension of the domain space
 * @param targetDim the dimension of the range space
 */    
    public LinearOperatorAdapter(int baseDim, int targetDim)
    {
        this.baseDimension = baseDim;
        this.targetDimension = targetDim;
    }
    public int getBaseDimension()
    {
        return baseDimension;
    }
    public int getTargetDimension()
    {
        return targetDimension;
    }    
    abstract public void transform(double[] in,double[] out);                        
}
