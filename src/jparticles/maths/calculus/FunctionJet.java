/*
 * FunctionJet.java
 *
 * Created on 20 July 2001, 21:31
 */

package jparticles.maths.calculus;

/** This interface abstract the concept of a  first order jet of a function.
 * By a first order jet we mean that for every point we consider the pair
 * formed by the value and the gradient of the given function. Some
 * algorithms require such information in order to perform their computations
 *
 * @author carlosv
 * @version 1.0
 */
public interface FunctionJet extends Function
{
 /** At the its current state, updates the value of the gradient.
 *  Recall that the gradient of a function <CODE> f:R^n->R </CODE> is given by
 *  <PRE> grad(f) = (df/dx_0, ..., df,dx_{n-1}), </PRE>
 *  where <CODE> df/dx_i </CODE> is the partial derivative of <CODE> f </CODE> 
 *  in the <CODE> x_i </CODE> direction. 
 */    
    public void updateGradient();
/** At its current state, returns the gradient of the function.
 */    
    public double [] getGradient();   
}

