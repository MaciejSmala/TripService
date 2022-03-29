package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

public class WriteToTxtFiles {

    public static void writeDatabase(String ID) throws IOException {
        File file = new File("database.txt");
        if (file.exists() && !file.isDirectory()) {
            FileWriter fw = new FileWriter(file, true);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.newLine();
                bw.write(ID);
            }
        } else {
            try {
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write(ID);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
