/*
 * FunctionJet2Adapter.java
 *
 * Created on 20 July 2001, 21:37
 */

package jparticles.maths.calculus;

/** Gives a basic implementation of the FunctionJet2 interface. 
 * This is mainly a helper class used
 * to ease the construction of  a FunctionsJet2. The user is meant to derive this class
 * to construct a FunctionsJet2, and use its protected variables when overloading the
 * updateValue, updateGradient and updateHessian methods. In fact, 
 * this methods are implemented by FunctionJet2Adapter
 * so as to perform certain possible optimizations. It is recommended that the user
 * calls the corresponding method of the superclass at the begeining of 
 * the overloaded version of each of them.
 *
 * @author carlosv
 * @version 1.0
 */
public  class FunctionJet2Adapter extends FunctionJetAdapter implements FunctionJet2
{  

/** The hessian of the function.
 */    
    protected double [] hessian;
/** Is true if the hessian has been updated, false otherwise.
 */                
    protected boolean hessianUpToDate = false;
    
/** Creates a FunctionJet2Adapter for a function with a domain of a specified
 * dimension.
 * @param baseDimension the dimension of the domain of the function.
 */    
    public FunctionJet2Adapter(int baseDimension) 
    {
        super(baseDimension);        
        hessian = new double[baseDimension];
    }



    public void setState(double[] x)
    {
        state = x;        
        valueUpToDate = false;
        gradientUpToDate = false;
        hessianUpToDate = false;
    }       
    public double[] getHessian() 
    {
        return hessian;
    }        
    public void updateHessian(double[] direction)
    {
        if(hessianUpToDate) return;
        hessianUpToDate = true;
    }
}
