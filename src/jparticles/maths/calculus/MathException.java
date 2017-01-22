package jparticles.maths.calculus;

/**
 * Used to generate maths exceptions.
 */
public class MathException
{
    static public int DIMENSION_MISMATCH = 0;
    static public int BYREFERENCE_ERROR = 1;
    static public int MAPSINGULARITY = 2;

    static String [] errorNames = {"Dimension Mismatch","ByReference Error", "Map Singularity"};

    /**
     * Generates maths error
     * @param o The object generating the error.
     * @param error_type The error type.
     * @param extraMessage An extra message to be displayed by the method.
     */
    static public void error(Object o, int error_type, String extraMessage)
    {
        String message = errorNames[error_type];
        if(o!=null)
        {
            message += " at "+o;
        }
        if(extraMessage!=null)
        {
            message+= ". "+extraMessage;
        }
        System.out.println(message);
    }
}
