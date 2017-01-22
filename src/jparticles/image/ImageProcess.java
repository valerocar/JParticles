/*
 * ImageProcess.java
 *
 * Created on 24 September 2001, 12:47
 */

package jparticles.image;
import java.awt.image.*;
/**
 *
 * @author  carlosv
 * @version 
 */
public interface ImageProcess 
{
    public void process(BufferedImage source, BufferedImage target);    
}

