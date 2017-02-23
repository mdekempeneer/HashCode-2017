package hashcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFileChooser;

public class Main {

    public ArrayList<int[]> pList;

    public int videoNb;
    public int endPointsNb;
    public int reqDescNb;
    public int cacheNb;
    public int cap;

    public int latency[][]; //latency from endpoint to cache
    public int reqMat[][]; //request amount by endPoint for videoID
    public int dataLat[]; //latency for endpoint to datacenter
    public int vidSize[];

    public boolean store[][]; // Whether video is stored in cache

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.readFile();
        main.pList = main.getProfitList();
        
        main.pList.sort(new ProfitComparator());
        main.processList(main.pList);
        
        /*for(int[] triple : main.pList) {
            System.out.println(Arrays.toString(triple));
        }*/
        
        for(boolean[] v : main.store) {
            System.out.println(Arrays.toString(v));
        }
    }
    
    /**
     * 
     * @param arr sorted plox
     */
    public void processList(ArrayList<int[]> arr) {
        
        int[] remSize = new int[cacheNb]; //remaining cache size
        Arrays.fill(remSize, cap);
        
        for(int[] triple : arr) {
            int vID = triple[0];
            int cID = triple[1];
            
            if(remSize[cID] >= vidSize[vID]) { //fits in cache
                store[vID][cID] = true;
                remSize[cID] -= vidSize[vID];
            }
        }
    }

    public ArrayList<int[]> getProfitList() {
        ArrayList<int[]> result = new ArrayList<>(videoNb * cacheNb);

        for (int vID = 0; vID < videoNb; vID++) {
            for (int cID = 0; cID < cacheNb; cID++) {
                int[] triple = new int[3];
                triple[0] = vID;
                triple[1] = cID;
                triple[2] = calculateProfit(vID, cID);
                result.add(triple);
            }
        }

        return result;
    }

    /**
     * Calc profit for videoX if in cache Y. relative to size video
     *
     * @param videoX
     * @param cacheY
     * @return
     */
    public int calculateProfit(int videoX, int cacheY) {
        int profit = 0;

        for (int endP = 0; endP < endPointsNb; endP++) {
            int defLat = dataLat[endP] * reqMat[endP][videoX];
            int altLat = latency[endP][cacheY];

            profit += Math.max(defLat - altLat, 0);
        }

        return profit / vidSize[videoX];
    }

    private static void printInput() {
        //for (boolean[] pizza1 : pizza) {
        //    System.out.println(Arrays.toString(pizza1));
        //}
    }

    public void readFile() throws IOException {
        String FILENAME = askSavePath();

        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String cl = br.readLine();
            String[] temp = cl.split(" ");

            videoNb = Integer.parseInt(temp[0]);
            endPointsNb = Integer.parseInt(temp[1]);
            reqDescNb = Integer.parseInt(temp[2]);
            cacheNb = Integer.parseInt(temp[3]);
            cap = Integer.parseInt(temp[4]);

            reqMat = new int[endPointsNb][videoNb];
            vidSize = new int[videoNb];
            dataLat = new int[endPointsNb];
            latency = new int[endPointsNb][cacheNb];
            store = new boolean[videoNb][cacheNb];

            /* Empty latency */
            for (int i = 0; i < endPointsNb; i++) {
                for (int j = 0; j < cacheNb; j++) {
                    latency[i][j] = -1;
                }
            }

            /* read rest */
            cl = br.readLine(); //read row
            temp = cl.split(" "); //video sizes

            for (int i = 0; i < videoNb; i++) {
                vidSize[i] = Integer.parseInt(temp[i]);
            }

            // Process endpoints
            for (int endP = 0; endP < endPointsNb; endP++) { //each endpoint set
                cl = br.readLine(); //read row
                temp = cl.split(" "); //video sizes

                dataLat[endP] = Integer.parseInt(temp[0]);

                int cNb = Integer.parseInt(temp[1]);
                for (int i = 0; i < cNb; i++) {
                    cl = br.readLine(); //read row
                    temp = cl.split(" "); //video sizes

                    int cIndex = Integer.parseInt(temp[0]);
                    latency[endP][cIndex] = Integer.parseInt(temp[1]);
                }
            }

            // Video requests
            for (int i = 0; i < reqDescNb; i++) {
                cl = br.readLine(); //read row
                temp = cl.split(" "); //video sizes

                int videoID = Integer.parseInt(temp[0]);
                int endPointID = Integer.parseInt(temp[1]);
                int nb = Integer.parseInt(temp[2]);

                reqMat[endPointID][videoID] = nb;
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
