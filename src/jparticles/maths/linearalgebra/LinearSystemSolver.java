/*
 * EquationSolver.java
 *
 * Created on 10 February 2001, 07:18
 */

package jparticles.maths.linearalgebra;

/** Interface used to abstract solvers for systems of linear
 * equations.
 *
 * @author Carlos Valero
 * @version 1.0
 */
public interface LinearSystemSolver
{
/** Sets the linear operator associated with this solver. It is
 * the responsablity of the user to ensure that the operator is linear.
 *
 * @param A a linear operator
 */
    public void set(LinearOperator A);
/** Given a vector b, this method constructs the solution of the linear
 * system <CODE> A x = b </CODE>, where <CODE> A </CODE> is the operator
 * associated with the solver. The solution is stored in the array sol.
 * The value of this array previous to the call
 * of the method is used as an initial conditions by the
 * solver.
 * @see jparticles.maths.linearalgebra.LinearSystemSolver#set
 * @param sol the solution to the linear system
 * @param b right element in equation <CODE> A x = b </CODE>
 */
    public void solve(double [] b, double [] sol);
}

