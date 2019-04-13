package comp1206.sushi.exceptions;

public class NegativeStockException extends Exception {
    public NegativeStockException(String m){
        super(m);
    }
}
