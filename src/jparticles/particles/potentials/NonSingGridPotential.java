/*
 * NonSingGridPotential.java
 *
 * Created on November 19, 2001, 3:28 PM
 */

package jparticles.particles.potentials;

import jparticles.maths.calculus.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 *
 * @author  carlosv
 * @version
 */
public class NonSingGridPotential extends LinearGridPotential
{
    private NonSingPotential nonSingPart;
    private int [] nonSingInd;
    /** Holds value of property nonSingStiffness. */
    private double nonSingStiffness = 10.0;
    /** Holds value of property localVolumeValue. */
    private double localVolumeValue = .3;
    
    private JSlider nonSingFactorSlider;
    private double nonSingSliderScale = 1.0;
   
    
    FunctionJet2Adapter f = new FunctionJet2Adapter(1)
    {
        public void updateValue()
        {
            value = nonSingStiffness*(state[0]-localVolumeValue)*(state[0]-localVolumeValue);
        }
        public void updateGradient()
        {
            gradient[0] = 2.0*nonSingStiffness*(state[0]-localVolumeValue);
        }
        public void updateHessian(double [] dir)
        {
            hessian[0] = 2.0*nonSingStiffness;
        }
    };
        
    public NonSingGridPotential(int particleDim,int columns,int rows)
    {
        super(particleDim,columns,rows);
        createNonSingPart();
        createNonSingFctSlider();
    }
    void createNonSingPart()
    {
        nonSingInd = new int [12*(columns-1)*(rows-1)];
        int xCount = columns-1;
        int yCount = rows-1;
        int count = 0;        
        for(int y = 0; y < yCount; y++)
        {            
            for(int x = 0; x < xCount; x++)
            {                                
                {
                    nonSingInd[count++] = gridIndices[x][y];
                    nonSingInd[count++] = gridIndices[x+1][y];
                    nonSingInd[count++] = gridIndices[x][y+1];
                    
                    nonSingInd[count++] = gridIndices[x+1][y+1];
                    nonSingInd[count++] = gridIndices[x][y+1];
                    nonSingInd[count++] = gridIndices[x+1][y];
                    
                    nonSingInd[count++] = gridIndices[x+1][y];
                    nonSingInd[count++] = gridIndices[x+1][y+1];
                    nonSingInd[count++] = gridIndices[x][y];
                    
                    nonSingInd[count++] = gridIndices[x][y+1];
                    nonSingInd[count++] = gridIndices[x][y];
                    nonSingInd[count++] = gridIndices[x+1][y+1];
                }                
            }
        }
        nonSingPart = new NonSingPotential(nonSingInd,f);
    }
    public void setState(double [] state)
    {
        super.setState(state);
        nonSingPart.setState(state);
    }
    public void setTotalGradient(double [] gradient)
    {
        super.setTotalGradient(gradient);
        nonSingPart.setTotalGradient(gradient);
    }
    public void setTotalHessian(double [] hessian)
    {
        super.setTotalHessian(hessian);
        nonSingPart.setTotalHessian(hessian);
    }
    public double getValue()
    {
        double value =super.getValue();
        if(nonSingStiffness != 0.0) value += nonSingPart.getValue();
        return value;
    }
    public void addGradient()
    {
        super.addGradient();
        if(nonSingStiffness != 0.0) nonSingPart.addGradient();
    }
    public void addHessian(double [] direction)
    {
        super.addHessian(direction);
        if(nonSingStiffness != 0.0) nonSingPart.addHessian(direction);
    }
    
    /** Getter for property nonSingStiffness.
     * @return Value of property nonSingStiffness.
     */
    public double getNonSingStiffness()
    {
        return nonSingStiffness;
    }
    
    /** Setter for property nonSingStiffness.
     * @param nonSingStiffness New value of property nonSingStiffness.
     */
    public void setNonSingStiffness(double nonSingStiffness)
    {
        this.nonSingStiffness = nonSingStiffness;
    }
    
    /** Getter for property localVolumeValue.
     * @return Value of property localVolumeValue.
     */
    public double getLocalVolumeValue()
    {
        return localVolumeValue;
    }
    
    /** Setter for property localVolumeValue.
     * @param localVolumeValue New value of property localVolumeValue.
     */
    public void setLocalVolumeValue(double localVolumeValue)
    {
        this.localVolumeValue = localVolumeValue;
    }
    
    private void createNonSingFctSlider()
    {
        nonSingFactorSlider = new JSlider();
        nonSingFactorSlider.setMinimum(0);
        nonSingFactorSlider.setMaximum(100);
        nonSingFactorSlider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent evt)
            {
                JSlider slider = (JSlider)evt.getSource();
                slider.setToolTipText("nonsing-stiffness factor = "+getNonSingStiffness());
                setNonSingStiffness(nonSingSliderScale*(double)slider.getValue());
            }
        });
    }
    public JSlider getNonSingFactorSlider()
    {
        return nonSingFactorSlider;
    }
    public double getSignature()
    {
        return (nonSingPart.getSignature());
    }
    public double getLocalSignature(int triple)
    {
        return (nonSingPart.getLocalSignature(triple));
    }
    public int [] getTriangleIndices()
    {
        return nonSingInd;
    }
    public void setNonSingSliderScale(double scale)
    {
        nonSingSliderScale = scale;
    }
}
