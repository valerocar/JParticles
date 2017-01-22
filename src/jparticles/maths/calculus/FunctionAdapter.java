/*
 * FunctionAdapter.java
 *
 * Created on 04 June 2001, 13:29
 */

package jparticles.maths.calculus;

/** Gives a basic implementation of the Function interface. This is mainly a helper class used
 * to ease the construction of functions. The user is meant to derive this class
 * to construct a function, and use its protected variables when overloading the
 * updateValue method. In fact, the updateValue method is implemented by FunctionAdapter
 * so as to perform certain possible optimizations. It is recommended that the user
 * calls super.updateValue() at the begeining of the overload of the method.
 *
 * @author carlosv
 * @version 1.0
 */
public class FunctionAdapter implements Function
{
/** A point in the domain of the function.
 */    
    protected double [] state;
/** The value of the function.
 */    
    protected double value;        
/** The dimension of the domain of the function.
 */    
    protected int baseDimension;    
/** Is true if the value has been updated, false otherwise.
 */    
    protected boolean valueUpToDate = false;
    
/** Creates a FunctionAdapter in a space of a specified dimension.
 * @param baseDim dimension of the domain of the function
 */    
    public FunctionAdapter(int baseDim) 
    {
        baseDimension = baseDim;                
    }    
    public void setState(double[] x) 
    {
        state = x;        
        valueUpToDate = false;
    }    
    public double[] getState() 
    {
        return state;
    }
    public double getValue() 
    {
        return value;
    }    
    public int getBaseDimension()
    {
        return baseDimension;
    }        
     public void updateValue()     
     {
         if(valueUpToDate) return;
         valueUpToDate = true;
     }
    
}
