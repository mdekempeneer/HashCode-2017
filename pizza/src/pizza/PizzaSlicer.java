package pizza;

/**
 *
 * @author Admin
 */
public class PizzaSlicer {
    
    private final int MIN;
    private final int MAX_CELLS;
    private final boolean[][] pizza;
    
    public PizzaSlicer(int MIN, int MAX_CELLS, boolean[][] pizza) {
        this.MIN = MIN;
        this.MAX_CELLS = MAX_CELLS;
        this.pizza = pizza;
    }
    
}
