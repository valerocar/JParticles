/*
 * NonSingPotential.java
 *
 * Created on October 31, 2001, 1:46 PM
 */

package jparticles.particles.potentials;
import jparticles.maths.calculus.*;
/**
 *
 * @author  carlosv
 * @version
 */
public class NonSingPotential extends ParticlesPotentialTerm
{
    private int [] triplesIndices;
    private int index, indexA, indexB, indexC;
    private double potentialValue;
    private int triplesCount;
    private double [] U = new double[2];
    private double [] V = new double[2];
    private double [] UV = new double[2];
    private double [] U_d = new double[2];
    private double [] V_d = new double[2];
    private double [] UV_d = new double[2];
    private double gradDetDotDir;
    private FunctionJet2 functionOfDet;
    private double []  functionState = new double[1];
    private double [] functionDer;
    private double [] functionHess;
    private double functionHessValue;
    private double functionDerValue;
    private double [] one =
    {1.0};
    private int sign;
    public NonSingPotential(int [] triplesIndices, FunctionJet2 functionOfDet)
    {
        this.triplesIndices = triplesIndices;
        triplesCount = triplesIndices.length/3;
        this.functionOfDet = functionOfDet;
    }
    public void setState(double [] state)
    {
        super.setState(state);
    }
    public double getValue()
    {
        potentialValue = 0.0;
        int count = 0;
        for(int i = 0; i < triplesCount; i++)
        {
            indexA = 2*triplesIndices[count++];
            indexB = 2*triplesIndices[count++];
            indexC = 2*triplesIndices[count++];
            U[0] = state[indexB]-state[indexA];
            U[1] = state[indexB+1]-state[indexA+1];
            V[0] = state[indexC]-state[indexA];
            V[1] = state[indexC+1]-state[indexA];
            functionState[0] = -U[1]*V[0] + U[0]*V[1];
            functionOfDet.setState(functionState);
            functionOfDet.updateValue();
            potentialValue += functionOfDet.getValue();
        }
        return potentialValue;
    }
    public void addGradient()
    {
        int count = 0;
        for(int i = 0; i < triplesCount; i++)
        {
            indexA = 2*triplesIndices[count++];
            indexB = 2*triplesIndices[count++];
            indexC = 2*triplesIndices[count++];
            U[0] = state[indexB]-state[indexA];
            U[1] = state[indexB+1]-state[indexA+1];
            V[0] = state[indexC]-state[indexA];
            V[1] = state[indexC+1]-state[indexA+1];
            UV[0] = V[0]-U[0];
            UV[1] = V[1]-U[1];
            functionState[0] = -U[1]*V[0] + U[0]*V[1];
            functionOfDet.setState(functionState);
            functionOfDet.updateGradient();
            functionDer = functionOfDet.getGradient();
            totalGradient[indexA] -= functionDer[0]*UV[1];
            totalGradient[indexA+1] += functionDer[0]*UV[0];
            
            totalGradient[indexB] += functionDer[0]*V[1];
            totalGradient[indexB+1] -= functionDer[0]*V[0];
            
            totalGradient[indexC] -= functionDer[0]*U[1];
            totalGradient[indexC+1] += functionDer[0]*U[0];
        }
    }
    public void addHessian(double[] direction)
    {
        int count = 0;
        for(int i = 0; i < triplesCount; i++)
        {
            indexA = 2*triplesIndices[count++];
            indexB = 2*triplesIndices[count++];
            indexC = 2*triplesIndices[count++];
            
            U_d[0] = direction[indexB]-direction[indexA];
            U_d[1] = direction[indexB+1]-direction[indexA+1];
            V_d[0] = direction[indexC]-direction[indexA];
            V_d[1] = direction[indexC+1]-direction[indexA+1];
            UV_d[0] = V_d[0]-U_d[0];
            UV_d[1] = V_d[1]-U_d[1];            
            
            U[0] = state[indexB]-state[indexA];
            U[1] = state[indexB+1]-state[indexA+1];
            V[0] = state[indexC]-state[indexA];
            V[1] = state[indexC+1]-state[indexA+1];
            UV[0] = V[0]-U[0];
            UV[1] = V[1]-U[1];
            
            gradDetDotDir = -direction[indexA]*UV[1]+direction[indexA+1]*UV[0] +
            direction[indexB]*V[1] - direction[indexB+1]*V[0] -
            direction[indexC]*U[1] + direction[indexC+1]*U[0];
            
            functionState[0] = -U[1]*V[0] + U[0]*V[1];
            functionOfDet.setState(functionState);
            functionOfDet.updateGradient();
            functionOfDet.updateHessian(one);
            functionDer = functionOfDet.getGradient();
            functionHess = functionOfDet.getHessian();
            functionHessValue = functionHess[0];
            totalHessian[indexA] -= functionHessValue*gradDetDotDir*UV[1];
            totalHessian[indexA+1] += functionHessValue*gradDetDotDir*UV[0];
            totalHessian[indexB] += functionHessValue*gradDetDotDir*V[1];
            totalHessian[indexB+1] -= functionHessValue*gradDetDotDir*V[0];
            totalHessian[indexC] -= functionHessValue*gradDetDotDir*U[1];
            totalHessian[indexC+1] += functionHessValue*gradDetDotDir*U[0];
            
            functionDerValue = functionDer[0];
            totalHessian[indexA] -= functionDerValue*UV_d[1];
            totalHessian[indexA+1] += functionDerValue*UV_d[0];
            totalHessian[indexB] += functionDerValue*V_d[1];
            totalHessian[indexB+1] -= functionDerValue*V_d[0];
            totalHessian[indexC] -= functionDerValue*U_d[1];
            totalHessian[indexC+1] += functionDerValue*U_d[0];            
        }
    }
    public double getSignature()
    {
        int count = 0;
        double minDet = 1.0;
        double det = 0.0;
        for(int i = 0; i < triplesCount; i++)
        {
            indexA = 2*triplesIndices[count++];
            indexB = 2*triplesIndices[count++];
            indexC = 2*triplesIndices[count++];
            U[0] = state[indexB]-state[indexA];
            U[1] = state[indexB+1]-state[indexA+1];
            V[0] = state[indexC]-state[indexA];
            V[1] = state[indexC+1]-state[indexA+1];
            det = -U[1]*V[0] + U[0]*V[1];
            if(det < minDet) minDet = det;
        }
        return minDet;
    }
    public double getLocalSignature(int triple)
    {
        int count = 3*triple;
        indexA = 2*triplesIndices[count++];
        indexB = 2*triplesIndices[count++];
        indexC = 2*triplesIndices[count];
        
        U[0] = state[indexB]-state[indexA];
        U[1] = state[indexB+1]-state[indexA+1];
        V[0] = state[indexC]-state[indexA];
        V[1] = state[indexC+1]-state[indexA+1];
        double det = -U[1]*V[0] + U[0]*V[1];
        return det;
    }       
}
