package hashcode;

import java.util.Comparator;

public class ProfitComparator implements Comparator<int[]> {

    @Override
    public int compare(int[] o1, int[] o2) {
        if (o1[2] < o2[2]) {
            return 1;
        } else if (o1[2] > o2[2]) {
            return -1;
        } else {
            return 0;
        }
    }
}

public double num = 15;
public Double getNumber{
	return num;
}

public double counter = 0;

public void count(){
	while(true){
		counter++;
	}
	System.out.println(counter.toString());
}


public void howFarCanIPushThis(){
	return "this method is just for error checking";
}
