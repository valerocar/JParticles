/*
 * VectorUtils.java
 *
 * Created on November 2, 2001, 12:01 PM
 */

package jparticles.maths.linearalgebra;

/**
 *
 * @author  carlosv
 * @version
 */
public class ArrayUtils
{
    ///////////
    //
    //   Add methods
    //
    ////////////
    static public void plusEuqal(double [] A, double [] B)
    {
        int count = A.length;
        for(int i = 0; i < count; i++) A[i] += B[i];
    }
    static public void add(double [] A, double [] B, double[]  AplusB)
    {
        int count = A.length;
        for(int i = 0; i < count; i++) AplusB[i] = A[i] + B[i];
    }
    static public double []  add(double [] A, double [] B)
    {
        double [] AplusB = new double[A.length];
        add(A,B,AplusB);
        return AplusB;
    }
    ///////////
    //
    //   Substract methods
    //
    ////////////
    static public void minusEuqal(double [] A, double [] B)
    {
        int count = A.length;
        for(int i = 0; i < count; i++) A[i] -= B[i];
    }
    static public void substract(double [] A, double [] B, double[] AminusB)
    {
        int count = A.length;
        for(int i = 0; i < count; i++) AminusB[i] = A[i] - B[i];
    }
    static public double []  substract(double [] A, double [] B)
    {
        double [] AminusB = new double[A.length];
        substract(A,B,AminusB);
        return AminusB;
    }
    ///////////
    //
    //   Multiply methods
    //
    ////////////
    static public void multiplyEqual(double scalar, double [] A)
    {
        int count = A.length;
        for(int i = 0; i < count; i++) A[i] *= scalar;
    }
    static public void multiply(double scalar, double [] A, double [] scalarTimesA)
    {
        int count = A.length;
        for(int i = 0; i < count; i++) scalarTimesA[i] = scalar*A[i];
    }
    static public double []  multiply(double scalar, double [] A)
    {
        double [] scalarTimesA = new double[A.length];
        multiply(scalar,A,scalarTimesA);
        return scalarTimesA;
    }
    /////////////////
    //
    //  Mixed methods
    //
    ////////////////////
    static public void addScalarTimes(double [] A, double scalar, double [] B, double [] AplusscalarTimesB)
    {
        int count = A.length;
        for(int i = 0; i < count; i++) AplusscalarTimesB[i] = A[i] + scalar*B[i];
    }
    static public void addScalarTimesEqual(double [] A, double scalar, double [] B)
    {
        int count = A.length;
        for(int i = 0; i < count; i++) A[i] = A[i] + scalar*B[i];
        
    }
    static public double []  addScalarTimes(double [] A, double scalar, double [] B)
    {
        int count = A.length;
        double [] AplusscalarTimesB = new double[count];
        for(int i = 0; i < count; i++) AplusscalarTimesB[i] = A[i] + scalar*B[i];
        return AplusscalarTimesB;
    }
    ///////////
    //
    //   Normalize methods
    //
    ////////////
    static public double norm(double [] A)
    {
        double normSquared = 0.0;
        int count = A.length;
        for(int i = 0; i < count; i++) normSquared += A[i]*A[i];
        return (Math.sqrt(normSquared));
    }
    static public double normalizeEqual(double [] A)
    {
        double norm = norm(A);
        double invNorm =1.0/ norm;
        int count = A.length;
        for(int i = 0; i < count; i++) A[i] *= invNorm;
        return norm;
    }
    static public double normalize(double [] A, double [] normalizedA)
    {
        double norm = norm(A);
        double invNorm =1.0/ norm;
        multiply(invNorm,A,normalizedA);
        return norm;
    }
    static public double dot(double [] A, double [] B)
    {
        double  out = 0.0;
        int count = A.length;
        for(int i = 0; i < count; i++) out += A[i]*B[i];
        return out;
    }
    //////////////////////
    //
    //  Vector2D methods
    //
    ///////////////////////
    static private double sin, cos;
    static public void rotate(double [] A, double angle, double [] rotatedA)
    {
        sin = Math.sin(angle);
        cos = Math.cos(angle);
        rotatedA[0] = A[0]*cos - A[1]*sin;
        rotatedA[1]  = A[0]*sin + A[1]*cos;
    }
    //////////////////////
    //
    // Print tools
    //
    ///////////////////////////
    static public void print(double [] A)
    {
        int count = A.length-1;
        System.out.print("[");
        for(int i = 0; i < count ; i++) System.out.print(A[i]+",");
        System.out.print(A[count]+"]");
    }
    static public void println(double [] A)
    {
        int count = A.length-1;
        System.out.print("[");
        for(int i = 0; i < count ; i++) System.out.print(A[i]+",");
        System.out.println(A[count]+"]");
    }
    static public void projectOntoLine(double [] lineVector, double [] in, double [] out)
    {
        normalize(lineVector,out);
        multiplyEqual(dot(in,out),out);
    }
    static public void projectOntoPlane(double [] planeVector, double [] in, double [] out)
    {
        double [] normalProjection = new double[planeVector.length];
        projectOntoLine(planeVector,in,normalProjection);
        substract(in,normalProjection,out);         
    }
    static public void makeZero(double [] vector)
    {
        for(int i = 0; i < vector.length; i++) vector[i] = 0.0;
    }
}
