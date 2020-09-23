
import java.util.*;

import static java.lang.Math.abs;

public class cnf {

    private static Random rand = new Random(); // in order to shuffle the array
    //public int[] symbol_values; // 1 means it is true; -1 means it is false; 0 means it doesn't have a assigned value.
    public List<List<Integer>> data = new LinkedList<>();
    private int count = 0;
    //data array with size (number of clauses) * (number of propositional symbols)
    //0 represents that the symbol was absent,
    // 1 that the symbol was present and unnegated,
    // -1 that the symbol was present and negated

    public static void swap(Integer[] a, int i, int j){
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void shuffle(Integer[] arr) {
        int length = arr.length;
        for ( int i = length; i > 0; i-- ){
            int randInd = rand.nextInt(i);
            swap(arr, randInd, i - 1);
        }
    }

    // the constructor of cnf, so that you can instanciate a cnf by using two integer C and V
    public cnf(int C, int V) {
        if(V < 3)
            throw new IndexOutOfBoundsException("V should be more than 3!");
//        if((2^V) <= C)
//            throw new IndexOutOfBoundsException("We cannot generate so many claueses!");
        List<Integer> clause_list = new LinkedList<>();
        List<Integer> symbols = new LinkedList<>();
        int index1 = 0;
        int index2 = 0;
        int index3 = 0;


        for (int i = 0; i < C; i++) {
            clause_list = new LinkedList<>();
            for (int j = 1; j <= V; j++) {
                symbols.add(j);
            }
            index1 = rand.nextInt(symbols.size());
            //System.out.println((rand.nextInt(2) * 2 - 1) * symbols.get(index1));
            clause_list.add((rand.nextInt(2) * 2 - 1) * symbols.get(index1));
            symbols.remove(index1);
            index2 = rand.nextInt(symbols.size());
            //System.out.println((rand.nextInt(2) * 2 - 1) * symbols.get(index2));
            clause_list.add((rand.nextInt(2) * 2 - 1) * symbols.get(index2));
            symbols.remove(index2);
            index3 = rand.nextInt(symbols.size());
            clause_list.add((rand.nextInt(2) * 2 - 1) * symbols.get(index3));
            symbols.remove(index3);
            symbols = new LinkedList<>();
            Iterator<List<Integer>> iter = data.iterator();
            boolean flag = true;
            while(iter.hasNext()){
                count ++;
                List<Integer> current_cluase = iter.next();
                if(current_cluase.contains(clause_list.get(0))&&current_cluase.contains(clause_list.get(1))
                &&current_cluase.contains(clause_list.get(2))){
                    flag = false;
                    break;
                }
            }
            if(flag){
                data.add(clause_list);
                //System.out.println(clause_list.toString());
            }
            else{
                i--;
            }
        }
    }


//        given symbols which contains values of symbols
//        if all clauses are true:
//            return 1;
//        if at least one clause is false:
//            return -1;
//        if at least one clause is blank:
//            return 0;
    public int check_if_satisfied(List<List<Integer>>data, List<Integer> symbol_values){
        for (int i= 0; i < data.size(); i++){
            int single_result = check(data.get(i), symbol_values);
            if (single_result == -1){
                return -1;
            }
            else if (single_result == 0){
                return 0;
            }
        }
        return 1;
    }

//    check if a single clause is satisfied or not
//    if at least one assignment is 0:
//        return 0;
//    if at least one literal is true:
//        return 1;
//    else:
//        return -1;
    public int check(List<Integer> clause, List<Integer> value) {
        int flag_zero = 0;
        for (int i=0; i<clause.size();i++){
            int single_symbol = clause.get(i);
            int assignment = value.get(abs(single_symbol)-1);
            if (assignment==0){
                flag_zero = 1;
            }
            else if ((single_symbol>0 && assignment==1) || (single_symbol<0 && assignment==-1)){
                return 1;
            }
        }
        if (flag_zero==1){
            return 0;
        }
        else {
            return -1;
        }
    }

    public void printSingleCase(boolean result,List<Integer> symbol_values) {
        System.out.println("Clauses.");
        for (int i=0;i<data.size();i++){
            List<Integer> output = new ArrayList<>();
            for (int j=0;j<symbol_values.size();j++){
                output.add(0);
            }
            for (int j=0;j<data.get(i).size();j++){
                output.set(abs(data.get(i).get(j))-1, (data.get(i).get(j)>0?1:-1));
            }
            for (int j=0;j<output.size();j++){
                System.out.print(output.get(j));
                System.out.print(" ");
            }
            System.out.println();
        }
        if (result) {
            System.out.println("Satisfying Assignment.");
            for (int i=0;i<symbol_values.size();i++){
                System.out.print(symbol_values.get(i));
                System.out.print(" ");
            }
            System.out.println();
        }
        else {
            System.out.println("No Satisfying assignment found.");
        }
    }

    public void printMultiCase(float fractionSucceed, double avgRuntime, double avgSatisfiableRuntime, double avgUnsatisfiableRuntime){
        System.out.print("Fraction of satisfiable formulae: ");
        System.out.println(fractionSucceed);
        System.out.print("Average Runtime: ");
        System.out.print(avgRuntime);
        System.out.println("ms");
        System.out.print("Average Runtime for satisfiable formulae: ");
        System.out.print(avgSatisfiableRuntime);
        System.out.println("ms");
        System.out.print("Average Runtime for unsatisfiable formulae: ");
        System.out.print(avgUnsatisfiableRuntime);
        System.out.println("ms");
    }

//    public static void main(String[] args) {
//        int[] a = {1,2,3,4};
//        Integer[] aa = {1,66,88,100, 201};
//        List<Integer> integerList= Arrays.asList(aa);
//        System.out.println(integerList);
//        System.out.println(2*rand.nextInt(2) -1);
//    }
}

