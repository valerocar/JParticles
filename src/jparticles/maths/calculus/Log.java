package jparticles.maths.calculus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public final class Log
{           
	
	private static boolean verboseTraces = false;
	
    private static boolean verboseErrors = true;
    
    private static boolean traceEnabled = true;
    
    private static boolean errorEnabled = true;
        
    /**
     * Should debug/trace messages include the class/calling function names.  Under
     * JDK1.3 this will slow down system performance, due to the complexity of obtaining
     * and parsing a stack trace.
     * @param yesNo Include verbose helpful messages.
     */
    static public void error(String message)
    {
    	if(errorEnabled)
    	{
    		// Errors will be verbose
    		System.err.println("MyJSDK ERROR: " + message);
    		if(verboseErrors)
            {
            	System.err.println(getStackTrace());
            }
    	}
    }

    /**
     * Method for tracing code.
     * @param message a message
     */
    static public void trace(String message)
    {               
    	if(traceEnabled)
    	{
            System.out.println(message);
            if(verboseTraces)
            {
            	System.out.println(getStackTrace());
            }
    	}
    }    
    
    private static String getStackTrace()
    {
    	Throwable th = new Throwable();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        th.printStackTrace(ps);
        String strace = baos.toString();
        try
        {
            baos.close();
        }
        catch (IOException e)
        {
        }                
        //
        // The following is to get rid of the first few lines 
        // corresponding to function call in this class
        //
        int index = strace.indexOf('\n');
        index = strace.indexOf('\n',index+1);
        index = strace.indexOf('\n',index+1);
        String out = strace.substring(index+1); 
        return out;
    }

    /**
     * Traces become verbose if set to true.
     * @param verboseTraces
     */
    public static void setVerboseTraces(boolean verboseTraces)
	{
		Log.verboseTraces = verboseTraces;
	}

    /**
     * Errors become verbose if set to true.
     * @param verboseErrors
     */
	public static void setVerboseErrors(boolean verboseErrors)
	{
		Log.verboseErrors = verboseErrors;
	}
	
    public static void main(String[] args)
    {    	    	       
    	Log.setVerboseErrors(true);
        Log.trace("My Trace Message");
        Log.error("My Error message");
    }

	
}
