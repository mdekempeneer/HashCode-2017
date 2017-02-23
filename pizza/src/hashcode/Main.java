package hashcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JFileChooser;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.readFile();

        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private static void printInput(boolean[][] pizza) {
        for (boolean[] pizza1 : pizza) {
            System.out.println(Arrays.toString(pizza1));
        }
    }

    public void readFile() throws IOException {
        String FILENAME = askSavePath();

        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String cl = br.readLine();
            String[] temp = cl.split(" ");

            ROWS = Integer.parseInt(temp[0]);
            COLS = Integer.parseInt(temp[1]);
            MIN = Integer.parseInt(temp[2]);
            MAX_CELLS = Integer.parseInt(temp[3]);

            pizza = new boolean[ROWS][COLS];

            for (int i = 0; i < ROWS; i++) {
                cl = br.readLine(); //read row

                for (int j = 0; j < COLS; j++) {
                    pizza[i][j] = (cl.charAt(j) != 'M');
                }

            }
        }

    }

    /**
     * Uses the JFileChooser to ask a savePath.
     *
     * @return The savePath, null when canceled.
     */
    private static String askSavePath() {
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));

        if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile().getAbsolutePath();
        } else {
            System.out.println("Failed chosing a file.");
        }

        return null;
    }

}
