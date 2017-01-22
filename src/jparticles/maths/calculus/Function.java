/*
 * Function.java
 *
 * Created on 09 May 2001, 10:58
 */

package jparticles.maths.calculus;

/** This interface abstracts ths concept of a functions function.
 *
 * @author carlosv
 * @version 1.0
 */
public interface Function 
{
/** Sets a point in the domain of the function.
 * @param x a point in the domain of the function.
 */    
    public void setState(double [] x);
/** Returns the current state.
 * @see jparticles.maths.calculus.Function#setState
 * @return a point in the domain of the function
 */    
    public double [] getState();
/** Updates the value of the function at the current state. 
 */    
    public void updateValue();
/** Gets the value of the function at its current state.
 * @return the value of the function
 */    
    public double getValue();    
/** Returns the dimension of the domain of the function.
 * @return the dimension of the domain
 */        
    public int getBaseDimension();    
}

