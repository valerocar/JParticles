/*
 * JPropertySlider.java
 *
 * Created on January 2, 2002, 3:42 PM
 */

package jparticles.gui;

import javax.swing.JSlider;
import java.lang.reflect.Method;
import jparticles.utils.ClassUtils;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author  carlos
 * @version
 */
public class JPropertySlider extends JSlider implements ChangeListener
{
    private Method getter = null;
    private Method setter = null;
    private JSlider source;
    private String propertyName;
    private double sliderScale;
    private int  signature;
    private Object theObject;
    private Object [] setterParam = new Object[1];
    CallBack callBack;
    public JPropertySlider(Object object,String propertyName, double scale, int  signature)
    {
        this.theObject = object;
        this.propertyName = propertyName;
        this.sliderScale = scale;
        this.signature = signature;
        if(signature > 0)
        {
            this.setMinimum(0);
            this.setMaximum(100);
        }
        if(signature < 0)
        {
            this.setMinimum(-100);
            this.setMaximum(0);
        }
        if(signature == 0)
        {
            this.setMinimum(-100);
            this.setMaximum(100);
        }                    
    
    try
    {
        getter = ClassUtils.getGetterForProperty(propertyName,object);
        setter = ClassUtils.getSetterForProperty(propertyName,object);
        ClassUtils.printMethodInfo(getter);
        System.out.println();
        ClassUtils.printMethodInfo(setter);
    }
    catch(Exception e)
    {
        System.out.println(e);
    }
    this.setValue((int)(invokeGetter()/sliderScale));
    this.addChangeListener(this);
}
public void stateChanged(ChangeEvent evt)
{
    source = (JSlider)evt.getSource();
    source.setToolTipText(propertyName + "="+ String.valueOf(invokeGetter()));
    invokeSetter(sliderScale*(double)this.getValue());
    callBack.method.run(callBack.object);
}
public void setCallBack(CallBack callBack)
{
    this.callBack = callBack;
}
private double invokeGetter()
{
    Double valueD = null;
    try
    {
        valueD = (Double)getter.invoke(theObject,null);
    }
    catch(Exception e)
    {
        System.out.println(e);
        System.exit(-1);
    }
    return valueD.doubleValue();
}
private void invokeSetter(double value)
{
    Double valueD = new Double(value);
    setterParam[0] = valueD;
    try
    {
        setter.invoke(theObject,setterParam);
    }
    catch(Exception e)
    {
        System.out.println(e);
        System.exit(-1);
    }
}
public void setScaleFactor(double value)
{
    this.sliderScale = value;
}
public double getScaleFactor()
{
    return sliderScale;
}
}

