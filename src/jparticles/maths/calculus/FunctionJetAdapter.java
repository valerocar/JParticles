/*
 * FunctionJetAdapter.java
 *
 * Created on 20 July 2001, 21:32
 */

package jparticles.maths.calculus;

/** Gives a basic implementation of the FunctionJetinterface. 
 * This is mainly a helper class used
 * to ease the construction of  a FunctionsJet. The user is meant to derive this class
 * to construct a FunctionsJet, and use its protected variables when overloading the
 * updateValue and updateGradient  methods. In fact, 
 * this methods are implemented by FunctionJetAdapter
 * so as to perform certain possible optimizations. It is recommended that the user
 * calls the corresponding method of the superclass at the begeining of 
 * the overloaded version of each of them.
 *
 * @author carlosv
 * @version 1.0
 */
public class FunctionJetAdapter extends FunctionAdapter implements FunctionJet
{
/** The gradient of the function.
*/    
    protected double [] gradient;
/** Is true if the gradient has been updated, false otherwise.
*/                
    protected boolean gradientUpToDate = false;
    /** Creates a FunctionJet2Adapter for a function with a domain of a specified
 * dimension.
 * @param baseDimension the dimension of the domain of the function.
 */    
    public FunctionJetAdapter(int baseDimension) 
    {
        super(baseDimension);
        gradient = new double[baseDimension];        
    }        
    
     public void setState(double[] x) 
    {
        state = x;        
        valueUpToDate = false;
        gradientUpToDate = false;        
    }    
     public double[] getGradient() 
    {
        return gradient;
    }       
     public void updateGradient()
    {        
        if(gradientUpToDate) return;
        gradientUpToDate = true;
    }

}

