/*
 * StateToJ2DMap.java
 *
 * Created on 21 July 2001, 19:38
 */

package jparticles.gui;

/** This class is used to define jmaps between a given n-dimensional space
 * and Java2D coordinates.
 *
 * @author carlos
 * @version 1.0
 */
public  class StateToJ2DMap
{
    protected int stateDim = 0;
    protected double [] state = null;
    public double [] parameters;
    /** Creates a StateToJ2DMap object for a state space of the specified
     * dimension.
     * @param stateDim the dimension of the state space
     */
    public StateToJ2DMap(int stateDim)
    {
        this.stateDim = stateDim;
    }
    public StateToJ2DMap(double hCenter, double vCenter, double hSize, double vSize)
    {
        parameters = new double[4];
        parameters[0] = hCenter;
        parameters[1] = vCenter;
        parameters[2] = hSize;
        parameters[3] = vSize;
    }
    /** The map function from the state space to the Java2D coordinates.
     * @param state he state variable
     * @param java2Dcoord java 2D cooordinates
     */
    public void mapStateToJ2D(double [] state, int [] java2Dcoord)
    {
        java2Dcoord[0] = (int)(parameters[0] + parameters[2]*state[0]);
        java2Dcoord[1] = (int)(parameters[1] - parameters[3]*state[1]);
    }
    /** The map function from the the Java2D coordinates to the state space.
     * @param java2coord java 2D coordinates
     * @param state state coordinates
     */
    public void mapJ2DtoState(int [] java2coord, double [] state)
    {
        state[0] = ((double)(java2coord[0]-parameters[0]))/parameters[2];
        state[1] = ((double)(-java2coord[1]+parameters[1]))/parameters[3];
    }
    public double [] getParameters()
    {
        return parameters;
    }
    public void setParameters(double [] parameters)
    {
        this.parameters = parameters;
    }
}
