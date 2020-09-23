import java.util.List;

public class dpllValues {
    boolean result;
    List<Integer> symbol_values;
    List<List<Integer>> data;
    public dpllValues(boolean result,  List<Integer> symbol_values,List<List<Integer>> data){
        this.result= result;
        this.symbol_values = symbol_values;
        this.data = data;
    }
}
