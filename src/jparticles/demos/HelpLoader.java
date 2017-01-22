package jparticles.demos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by valeroc on 20/01/17.
 */
public class HelpLoader {

    public static String loadHelpFile(String name)
    {
        String fileName = "help/"+ name+ ".txt";
        InputStream is = HelpLoader.class.getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        //long count = reader.lines().count();
        String text = "";
        String line = "";
        while(line!=null) {
            try {
                line = reader.readLine();
                if(line != null) text += line + "\n";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return text;
    }
    public static void main(String[] args) {
        System.out.println(HelpLoader.loadHelpFile("Cloth"));
    }
}
