/*
 * LinearOperator.java
 *
 * Created on 10 February 2001, 13:05
 */

package jparticles.maths.linearalgebra;

/** Interface used to abstract the concept of a linear operator
 *
 * @author Carlos Valero
 * @version 1.0
 */
public interface LinearOperator 
{
/** transforms the vector in the domain of the operator to a vector in the range
 * of the operator. It is the responsablilty of the user to ensure that this
 * map is linear.
 * @param in vector in the domain of the operator
 * @param out vactor in the range of the operator
 */    
     public void transform(double [] in, double [] out);
/** Gets the dimension of the domain of the operator
 * @return dimension of the domain of the operator
 */     
     public int getBaseDimension();
/** Gets the dimension of the range of the operator
 * @return dimension of the range of the operator
 */     
     public int getTargetDimension();
}

