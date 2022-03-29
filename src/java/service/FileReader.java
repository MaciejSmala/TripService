package service;

import java.io.File;
import java.io.FileNotFoundException; 
import java.util.Scanner; 

public class FileReader {
    public static boolean checkID(String ID) {
        boolean idExists = false;
        try {
            File file = new File("database.txt");
            if (file.isFile()) {
                try (Scanner myReader = new Scanner(file)) {
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        if (data.equals(ID)) {
                            idExists = true;
                        }
                    }
                }
                return idExists;
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        return idExists;
    }
}
