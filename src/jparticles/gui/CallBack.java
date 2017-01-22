/*
 * CallBack.java
 *
 * Created on 03 October 2001, 14:14
 */

package jparticles.gui;

/**
 *
 * @author  carlos
 * @version
 */
public class CallBack
{
	public Object object;
    public Method method;
    
    public CallBack(Method method, Object object)
    {
        this.object = object;
        this.method = method;
    }    
    
    static public interface Method
    {
        public void run(Object userObject);
    }
    
    
    
}
