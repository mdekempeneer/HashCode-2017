package hashcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
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

    public int remSize[];

    ArrayList<Integer> shuffled;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Main main = new Main();

        if (args.length > 0) {
            main.readFile(args[0]);
        } else {
            main.readFile(askSavePath());
        }

        long start = System.currentTimeMillis();
        main.createShuffled();
        main.alg4();
        long took = System.currentTimeMillis() - start;
        /*main.pList = main.getProfitList();

        main.pList.sort(new ProfitComparator());
        main.processList(main.pList);*/ /*for (boolean[] v : main.store) {
            System.out.println(Arrays.toString(v));
        }*/
        System.out.println("SCORE: " + main.getScore() + " took: " + took);
        main.writeResultToFile();
    }

    public void createShuffled() {
        shuffled = new ArrayList<>(cacheNb);

        for (int i = 0; i < cacheNb; i++) {
            shuffled.add(i);
        }

        Collections.shuffle(shuffled);
    }

    /**
     *
     * @param arr sorted plox
     */
    public void processList(ArrayList<int[]> arr) {

        int[] remSize = new int[cacheNb]; //remaining cache size
        Arrays.fill(remSize, cap);

        for (int[] triple : arr) {
            int vID = triple[0];
            int cID = triple[1];

            if (remSize[cID] >= vidSize[vID]) { //fits in cache
                store[vID][cID] = true;
                remSize[cID] -= vidSize[vID];
            }
        }
    }

    public void alg1() {
        int[] remSize = new int[cacheNb]; //remaining cache size
        Arrays.fill(remSize, cap);

        boolean found = true;

        while (found) {
            found = false;

            pList = getProfitList();
            pList.sort(new ProfitComparator());

            for (int[] triple : pList) {
                int vID = triple[0];
                int cID = triple[1];

                if (remSize[cID] >= vidSize[vID]) { //fits in cache
                    store[vID][cID] = true;
                    remSize[cID] -= vidSize[vID];
                    found = true;
                    break;
                }
            }
        }
    }

    public void alg2() {
        int[] triple = new int[3];
        while (triple.length != 0) {
            triple = getAction();

            if (triple.length > 0) {
                int vID = triple[0];
                int cID = triple[1];

                store[vID][cID] = true;
                remSize[cID] -= vidSize[vID];
            }
        }
    }

    public void alg3() {
        int[] triple = new int[3];
        while (triple.length != 0) {
            triple = getAction3();

            if (triple.length > 0) {
                int vID = triple[0];
                int cID = triple[1];

                store[vID][cID] = true;
                remSize[cID] -= vidSize[vID];
            }
        }
    }

    public void alg4() {
        int[] triple = new int[3];
        while (triple.length != 0) {
            triple = getAction4();

            if (triple.length > 0) {
                int vID = triple[0];
                int cID = triple[1];

                store[vID][cID] = true;
                remSize[cID] -= vidSize[vID];
            }
        }
    }

    public int[] getAction3() {
        int[] bestTriple = new int[3];

        for (int vID = 0; vID < videoNb; vID++) {
            int vSize = vidSize[vID];

            for (int cID = 0; cID < cacheNb; cID++) {
                if (!store[vID][cID]) {

                    int profit = calculateProfit(vID, cID);

                    if (profit > 0 && remSize[cID] >= vSize) {
                        bestTriple[0] = vID;
                        bestTriple[1] = cID;
                        bestTriple[2] = profit;

                        return bestTriple;
                    }
                }
            }
        }

        if (bestTriple[2] == 0) {
            return new int[0];
        }

        return bestTriple;
    }

    public int[] getAction4() {
        int[] bestTriple = new int[3];

        for (int vID = 0; vID < videoNb; vID++) {
            int vSize = vidSize[vID];

            for (int cIndex = 0; cIndex < cacheNb; cIndex++) {

                //for (int cID = 0; cID < cacheNb; cID++) {
                int cID = shuffled.get(cIndex);
                if (!store[vID][cID]) {

                    int profit = calculateProfit(vID, cID);

                    if (profit > 0 && remSize[cID] >= vSize) {
                        bestTriple[0] = vID;
                        bestTriple[1] = cID;
                        bestTriple[2] = profit;

                        return bestTriple;
                    }
                }
            }
        }

        if (bestTriple[2] == 0) {
            return new int[0];
        }

        return bestTriple;
    }

    public int[] getAction() {

        int[] bestTriple = new int[3];

        for (int vID = 0; vID < videoNb; vID++) {
            int vSize = vidSize[vID];

            for (int cID = 0; cID < cacheNb; cID++) {
                if (!store[vID][cID]) {

                    int profit = calculateProfit(vID, cID);

                    if (profit > bestTriple[2] && remSize[cID] >= vSize) {
                        bestTriple[0] = vID;
                        bestTriple[1] = cID;
                        bestTriple[2] = profit;
                    }
                }
            }
        }

        if (bestTriple[2] == 0) {
            return new int[0];
        }

        return bestTriple;
    }

    public ArrayList<int[]> getProfitList() {
        ArrayList<int[]> result = new ArrayList<>(videoNb * cacheNb);

        for (int vID = 0; vID < videoNb; vID++) {
            for (int cID = 0; cID < cacheNb; cID++) {
                int[] triple = new int[3];
                triple[0] = vID;
                triple[1] = cID;

                if (!store[vID][cID]) {
                    triple[2] = calculateProfit(vID, cID);
                    if (triple[2] != 0) {
                        result.add(triple);
                    }
                }
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
            int currMinLat = minimalDelay(videoX, endP) * reqMat[endP][videoX];
            int altLat = latency[endP][cacheY];

            profit += Math.max(currMinLat - altLat, 0);
        }

        return profit / vidSize[videoX];
    }

    /**
     * Calculate minimal delay to find videoX
     *
     * @param videoX
     * @param cacheY
     * @param endP
     * @return
     */
    public int minimalDelay(int videoX, int endP) {
        int minimalDelay = dataLat[endP];

        for (int cID = 0; cID < cacheNb; cID++) {
            if (store[videoX][cID]) {
                int lat = latency[endP][cID];
                minimalDelay = (lat > -1) ? Math.min(minimalDelay, lat) : minimalDelay;
            }
        }

        return minimalDelay;
    }

    public double getScore() {
        double score = 0;
        int totalReq = 0;

        for (int vID = 0; vID < videoNb; vID++) {
            for (int endP = 0; endP < endPointsNb; endP++) {
                int req = reqMat[endP][vID];

                if (req != 0) {
                    totalReq += req;
                    int a = dataLat[endP];
                    int b = minimalDelay(vID, endP);
                    score += req * (a - b);
                }
            }
        }

        System.out.println("totalReq " + totalReq + " and score " + score);
        score *= (1000 / totalReq);

        return score;
    }

    public void writeResultToFile() throws IOException {

        ArrayList<Integer> videosInCache = new ArrayList<>();
        HashMap<Integer, ArrayList<Integer>> result = new HashMap<>();
        int nbOfUsedCaches = 0;

        for (int col = 0; col < store[0].length; col++) {
            videosInCache.clear();
            for (int row = 0; row < store.length; row++) {
                if (store[row][col]) {
                    videosInCache.add(row);
                }

            }
            if (!videosInCache.isEmpty()) {
                result.put(col, new ArrayList<Integer>(videosInCache));
            }

        }

        nbOfUsedCaches = result.size();

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("solution.out"), "utf-8"))) {

            writer.write(Integer.toString(nbOfUsedCaches) + "\n");
            Set<Integer> keys = result.keySet();

            for (int nb : keys) {
                writer.write(nb + " " + splitValues(result.get(nb)) + "\n");
            }
            writer.flush();
            writer.close();
        }
    }

    private String splitValues(ArrayList<Integer> values) {

        String str = values.toString();
        str = str.replace(", ", " ");
        str = str.replace("[", "");
        str = str.replace("]", "");

        return str;
    }

    private static void printInput() {
        //for (boolean[] pizza1 : pizza) {
        //    System.out.println(Arrays.toString(pizza1));
        //}
    }

    public void readFile(String Filename) throws IOException {

        //String FILENAME = askSavePath();
        System.out.println(Filename);
        try (BufferedReader br = new BufferedReader(new FileReader(Filename))) {
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
            remSize = new int[cacheNb];
            Arrays.fill(remSize, cap);

            /* Empty latency */
            for (int i = 0; i < endPointsNb; i++) {
                Arrays.fill(latency[i], -1);
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
