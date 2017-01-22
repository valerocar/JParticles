/*
 * ClassUtils.java
 *
 * Created on January 2, 2002, 7:09 PM
 */

package jparticles.utils;

import java.lang.reflect.Method;

/**
 *
 * @author  carlos
 * @version
 */
public class ClassUtils
{
    static public Method getGetterForProperty(String propertyName, Object object) throws Exception
    {        
        Method getterMethod = null;
        String getMethodName = "get"+ getCapitalized(propertyName);                                
        Class objectClass = object.getClass();
        getterMethod = objectClass.getMethod(getMethodName, null);        
        Class returnType = getterMethod.getReturnType();                           
        return getterMethod;
    }
    
    static public Method getSetterForProperty(String propertyName, Object object) throws Exception
    {                
        String setMethodName = "set"+ getCapitalized(propertyName);                        
        Class objectClass = object.getClass();                
        Method [] methods = objectClass.getMethods();                       
        for(int i = 0; i < methods.length; i++)
        {
            if(methods[i].getName().equals(setMethodName)) return methods[i];
        }        
        return null;
    }
    static private String getCapitalized(String string)
    {
        String out = new String();
        char [] array = string.toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        for(int i = 0; i < array.length; i++) out += array[i];
        return out;
    }
    
    static public void printMethodInfo(Method method)
    {
        System.out.println("Name = " + method.getName());
        System.out.println("Return type = " + method.getReturnType().getName());                
        Class [] parameters = method.getParameterTypes();
        System.out.print("Paramaters: ");
        for(int i = 0; i < parameters.length; i++) System.out.print(parameters[i].getName()+" ");  
        System.out.println();
    }
}
