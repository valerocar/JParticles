/*
 * ConjGradSolver.java
 *
 * Created on 10 February 2001, 13:12
 */

package jparticles.maths.linearalgebra;

/** This class is used to construct solvers that use 
 * the conjugate gradient method to solve the equation
 * <CODE> A*x = b, </CODE> 
 * where <CODE>A</CODE> is a symmetric matrix.
 * Two parameters affect the performance of the solver:
 * the zero tolerance and the iterations constant. If the solver finds
 * a solution that falls within the zero tolerance, no further processing
 * will be done. The iterations constant is
 * such that the number of iterations done by the solver will have an upper bound 
 * given by the integer
 * <PRE>
 * (int)(iterationsConstant*baseDimension),
 * </PRE>
 * where baseDimension is the dimension of the domain of A. The iterations constat has default value equal to 2.0, and
 * is always be greater than <CODE> 1.0 </CODE>. It is the responsablity of the user to 
 * ensure that the operator A is symmetric.
 * @author Carlos Valero
 * @version 1.0
 */
public class ConjGradSolver implements LinearSystemSolver
{
    private LinearOperator A = null;
    private  LinearOperator projectionMap = null;    
    
    private int baseDimension;
    private int iterCount;
    private double iterationsConstant = 2.0;
    private double [] r;
    private double [] p;
    private double [] Ap;
    private double [] projection;
    private double a;
    private double oldrr,newrr;
    private double zeroTolerance = 0.0000003;
    
/** Creates a new conjugate gradient solver
 */    
    public ConjGradSolver()
    {
    }
    
    private double dot(double [] v, double [] w)
    {
        double out = 0.0;
        for(int i = 0; i < baseDimension; i++) out += v[i]*w[i];
        return out;
    }
    
    private void filter(double [] out)
    {
        projectionMap.transform(out,projection);
        for(int i = 0; i < baseDimension; i++) out[i] = projection[i];
    }
    public void solve(double[] b,double[] sol)
    {
        if(projectionMap != null)
        {
            filter(sol);            
        }
        A.transform(sol,Ap);
        for(int i = 0; i < baseDimension ; i++)  r[i] = (b[i] - Ap[i]);
        if(projectionMap != null) filter(r);
        for(int i = 0; i < baseDimension ; i++)  p[i] = r[i];
        if(dot(p,p) <= zeroTolerance) return; // We already have a solution
        for(int count = 0; count < iterCount ; count++)
        {
            A.transform(p,Ap);
            oldrr = dot(r,r);
            a =  (oldrr/dot(Ap,p));
            for(int i = 0; i < baseDimension; i++)
            {
                sol[i] = sol[i] +a*p[i];
                r[i] = r[i] - a*Ap[i];
            }
            if(projectionMap != null) filter(r);
            newrr = dot(r,r);
            if(newrr <= zeroTolerance)
            {
                if(projectionMap != null)
                {
                    filter(sol);
                }
                return;
            }
            for(int i = 0; i < baseDimension; i++)
            {
                p[i] = r[i] + (newrr/oldrr)*p[i];
            }
            if(projectionMap != null) filter(p);
        }
        if(projectionMap != null)
        {
            filter(sol);
        }
    }
/** Sets constraints on the solution of the system A*x = b. The operator 
 * <CODE> A:V->V </CODE>
 * (being symmetric) induces a quadratic form on the vector space
 * <CODE>
 * W = projectionMap(V).
 * </CODE>
 * This quadratic form can be seen as an operator
 * <CODE>
 * A' = P*A*i:W->W, 
 * </CODE>
 * where <CODE>i:W->V</CODE> is the inclusion map. The solver method then returns
 * the solution of the system
 * <CODE>
 * A'x = P*b 
 * </CODE> 
 * with the restriction: <CODE>x</CODE> is am element of <CODE>W</CODE>. 
 * It is the responsablity of
 * the user to ensure that P is a projection map, i.e <CODE> P^2 = P </CODE>.
 *
 * @param projectionMap a projection map
 */    
    public void setFilter(LinearOperator projectionMap)
    {
        this.projectionMap = projectionMap;
    }
    public void set(LinearOperator A)
    {
        this.A = A;
        baseDimension = A.getBaseDimension();
        iterCount =(int)(iterationsConstant*baseDimension);
        r = new double[baseDimension];
        p = new double[baseDimension];
        Ap = new double[baseDimension];
        projection = new double[baseDimension];        
    }
    /** Sets the zero tolerance for this object
     * @param tolerance the zero tolerance
     */
    public void setzeroTolerance(double tolerance)
    {
        this.zeroTolerance = tolerance;
    }
/** Returns this object's zero tolerance
 * @return the zero tolerance
 */
    public double getzeroTolerance()
    {
        return zeroTolerance;
    }
/** Sets the iterations' constant for this object
 * @param constant iterations constant
 */
    public void setIterationsConstant(double constant)
    {
        if (iterationsConstant <= 1.0) iterationsConstant = 1.0;
        else iterationsConstant = constant;
    }
/** Returns the iterations constant.
 * @return iterations constant
 */
    public double getIterationsConstant()
    {
        return iterationsConstant;
    }
}
