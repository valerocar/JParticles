package jparticles.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by valeroc on 20/01/17.
 */
public class FileUtils {
    public static String getDemosPath()
    {

        String path = null;
        try {
            path = Paths.get(ClassLoader.getSystemResource(".").toURI()).toString()+ "/jparticles/demos/";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return path;
    }
    public static String readTextFile(String pathToFile)
    {
        String out = "";
        File file  = new File(pathToFile);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(scanner.hasNext())
        {
            out += scanner.nextLine();
        }
        scanner.close();
        return out;
    }

    public static void main(String[] args) {
        System.out.println(getDemosPath());

    }
}
