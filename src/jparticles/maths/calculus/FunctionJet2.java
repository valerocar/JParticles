/*
 * FunctionJet.java
 *
 * Created on 16 March 2001, 16:27
 */

package jparticles.maths.calculus;

/** This interface abstract the concept of a second order jet of a function.
 * By a second order jet we mean that for every point we consider the triple
 * formed by the value, the gradient and the hessian of the given function. Some
 * algorithms require such information in order to perform their computations
 *
 * @author carlosv
 * @version 1.0
 */
public interface FunctionJet2 extends FunctionJet
{

/** At its current state, updates the value of the hessian in the given direction.
 * If we let <CODE> H </CODE>
 * stand for the martix of second derivative of the function <CODE> f </CODE>, i.e
 * <PRE> H = (d^2f/dx_i dx_j), </PRE>
 * then this method should update the value of H*direction.
 * @param direction the direction in which to evaluate the hessian
 */    
    public void updateHessian(double [] direction);     
/** At its current state, returns the hessian of the function
 * in the direction specified in the update method.
 * @return the hessian of the function in a given direction
 */    
    public double [] getHessian();    
}

