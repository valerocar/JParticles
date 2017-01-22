/*
 * QuadraticCGMinimizer.java
 *
 * Created on 18 March 2001, 14:29
 */

package jparticles.maths.calculus;
import jparticles.maths.linearalgebra.*;

/** This class implements the conjugate gradient method for quadratic
 * functions. Two parameters affect its performance:
 * the zero tolerance and the iterations constant. If the minimizer finds
 * a solution that falls within the zero tolerance, no further processing
 * will be done. The iterations constant is
 * such that the number of iterations will have an upper bound given by the integer
 * <CODE>
 * (int)(iterationsConstant*functionBaseDim),
 * </CODE>
 * where functionBaseDim is the baseDimension of the functionJet associated
 * with the minimizer. The iterations constat has default value equal to 2.0, and
 * should always be greater that <CODE> 1.0 </CODE>.
 *
 * @author Carlos Valero
 * @version 1.0
 */

public class QuadraticCGMinimizer
{
    private  LinearOperator projectionMap = null;
    private double [] projection;
    private FunctionJet2 jet;
    private int baseDimension;
    private int iterCount;
    private double [] r;
    private double [] p;
    private double [] Ap;
    private double tmp;
    private double oldrr,newrr;
    private double zeroTolerance = 0.0003;
    private double iterationsConstant = 2.0;
    
    /** Creates a QuadraticCGMininizer
     */
    public QuadraticCGMinimizer()
    {
    }
    private double dot(double [] v, double [] w)
    {
        double out = 0.0;
        for(int i = 0; i < baseDimension; i++) out += v[i]*w[i];
        return out;
    }
    /** Minimizes the function associated with this object. This minimal state
     * is stored in corresponding FunctionJet2 object.
     *
     */
    public void minimizeFunction()
    {        
        double [] min = jet.getState();        
        jet.updateGradient();
        double [] grad = jet.getGradient();
        jet.updateHessian(min);
        Ap = jet.getHessian();
        for(int i = 0; i < baseDimension ; i++)
        {
            r[i] = -grad[i];
        }
        if(projectionMap != null) filter(r);
        for(int i = 0; i < baseDimension ; i++)  p[i] = r[i];
        if(dot(p,p) <= zeroTolerance) return; // We already have a solution!
        
        for(int count = 0; count < iterCount ; count++)
        {
            jet.updateHessian(p);
            Ap = jet.getHessian();
            oldrr = dot(r,r);
            tmp =  (oldrr/dot(Ap,p));
            for(int i = 0; i < baseDimension; i++)
            {
                min[i] = min[i] +tmp*p[i];
                r[i] = r[i] - tmp*Ap[i];
            }
            if(projectionMap != null) filter(r);
            newrr = dot(r,r);
            if(newrr <= zeroTolerance)
            {                
                return;
            }
            for(int i = 0; i < baseDimension; i++)
            {
                p[i] = r[i] + (newrr/oldrr)*p[i];
            }
            if(projectionMap != null) filter(p);
        }        
    }
    /** Sets the function jet associated with this QuadraticCGMininizer. It is
     * the responsablity of the user to ensure that the function is quadratic.
     * @param jet parameter contains all the necessary information
     * of the quadratic function used in the conjugate gradient
     * method
     * @see jparticles.maths.Optimization.FunctionJet2
     */
    public void set(FunctionJet2 jet)
    {
        this.jet = jet;
        baseDimension = jet.getBaseDimension();
        iterCount = (int)iterationsConstant*baseDimension;
        r = new double[baseDimension];
        p = new double[baseDimension];
        projection = new double[baseDimension];
    }
    /** Sets the zero tolerance for this object
     * @param tolerance the zero tolerance
     */
    public void setZeroTolerance(double tolerance)
    {
        this.zeroTolerance = tolerance;
    }
    /** Returns this object's zero tolerance
     * @return the zero tolerance
     */
    public double getZeroTolerance()
    {
        return zeroTolerance;
    }
    /** Sets the iterations constant for this object.
     * @param constant iterations constant
     */
    public void setIterationsConstant(double constant)
    {
        iterationsConstant = constant;
    }
    /** Returns this object's  iterations' constant.
     * @return iterations constant
     */
    public double getIterationsConstant()
    {
        return iterationsConstant;
    }
    /** Sets constraints to the minimization problem.  The operator
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
    private void filter(double [] out)
    {
        projectionMap.transform(out,projection);
        for(int i = 0; i < baseDimension; i++) out[i] = projection[i];
    }
    
}

