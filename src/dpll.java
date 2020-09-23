import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class dpll {
    public cnf s;
    public List<List<Integer>> data_copy;
    public List<List<Integer>> data_copy_v2;//for instance use
    public List<Integer> symbol_values;

    public static void main(String[] args){
        int C = Integer.valueOf(args[0]);
        int V = Integer.valueOf(args[1]);
        int T = Integer.valueOf(args[2]);
        dpll dpll = new dpll();
        dpll.run(C, V, T);
    }

    public void run(int C, int V, int T){
        if (C<1){
            System.out.println("C>=1 is required.");
            System.exit(1);
        }
        if (V<3){
            System.out.println("V>=3 is required.");
            System.exit(1);
        }
        if (T<1){
            System.out.println("T>=1 is required.");
            System.exit(1);
        }
        if (T==1){
            s = new cnf(C, V);
            symbol_values = new ArrayList<>();
            for (int i=0;i<V;i++){
                symbol_values.add(0);
            }
            try {
                data_copy = deepCopy(s.data);
                data_copy_v2 = deepCopy(s.data);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            dpllValues result=this.dpll_loop(data_copy, symbol_values);
            s.printSingleCase(result.result, result.symbol_values);
        }
        else {
            int timeSucceed = 0;
            double avgRuntime = 0;
            double avgSatisfiableRuntime = 0;
            double avgUnsatisfiableRuntime = 0;
            for (int i = 0; i < T; i++) {
                s = new cnf(C, V);
                symbol_values = new ArrayList<>();
                for (int j=0;j<V;j++){
                    symbol_values.add(0);
                }
                try {
                    data_copy = deepCopy(s.data);
                    data_copy_v2 = deepCopy(s.data);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                long startTime = System.currentTimeMillis();
                dpllValues result = this.dpll_loop(data_copy, symbol_values);
                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                avgRuntime += totalTime;
                if (result.result) {
                    timeSucceed++;
                    avgSatisfiableRuntime += totalTime;
                } else {
                    avgUnsatisfiableRuntime += totalTime;
                }
            }
            float fractionSucceed = (float)timeSucceed / (float)T;
            avgRuntime /= T;
            if (timeSucceed!=0) {
                avgSatisfiableRuntime /= timeSucceed;
            }
            if (T-timeSucceed!=0) {
                avgUnsatisfiableRuntime /= (T - timeSucceed);
            }
            s.printMultiCase(fractionSucceed, avgRuntime, avgSatisfiableRuntime, avgUnsatisfiableRuntime);
        }
    }

    public void run_instance(List<List<Integer>> data, int V){
        s = new cnf(3,3);
        int timeSucceed = 0;
        double avgRuntime = 0;
        double avgSatisfiableRuntime = 0;
        double avgUnsatisfiableRuntime = 0;
        symbol_values = new ArrayList<>();
        for (int j=0;j<V;j++){
            symbol_values.add(0);
        }
        try {
            data_copy_v2 = deepCopy(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        long startTime = System.currentTimeMillis();
        dpllValues result = this.dpll_loop(data_copy_v2, symbol_values);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        avgRuntime += totalTime;
        System.out.println(avgRuntime);
    }

    public dpllValues dpll_loop(List<List<Integer>> data_copy, List<Integer> symbol_values){
        dropSatisfiedClauses(data_copy, symbol_values);
        int if_satisfied = s.check_if_satisfied(data_copy_v2, symbol_values);
        if (if_satisfied == 1){
            return new dpllValues(true,symbol_values, data_copy);
        }
        else if (if_satisfied == -1){
            return new dpllValues(false, symbol_values, data_copy);
        }
        else if (if_satisfied == 0){
//            find pure symbol
            int[] pure_symbol = this.find_pure_symbol(data_copy, symbol_values);
            if (pure_symbol!=null){
//                for (int i=0;i<pure_symbol.length;i++){
//                    System.out.println(pure_symbol[i]);
//                }
                symbol_values.set(pure_symbol[0],pure_symbol[1]);
                dropSatisfiedClauses(data_copy, symbol_values);
//                for (int i=0;i<data_copy.size();i++){
//                    int check_result = s.check(data_copy.get(i), symbol_values);
//                }
                return this.dpll_loop(data_copy, symbol_values);
            }
            else {
//                find unit clause
                int[] unit_clause = this.find_unit_clause(data_copy, symbol_values);
                if (unit_clause!=null){
                    symbol_values.set(unit_clause[0],unit_clause[1]);
                    dropSatisfiedClauses(data_copy, symbol_values);
                    return this.dpll_loop(data_copy, symbol_values);
                }
                else{
//                    randomly assign a value to a symbol
                    for (int i=0;i<symbol_values.size();i++){
                        if (symbol_values.get(i)==0){
                            List<Integer> symbol_assigned_true = new ArrayList<>();
                            List<Integer> symbol_assigned_false = new ArrayList<>();
                            List<List<Integer>> data_copy_true = new ArrayList<>();
                            List<List<Integer>> data_copy_false = new ArrayList<>();
                            try {
                                symbol_assigned_true = deepCopy(symbol_values);
                                symbol_assigned_false = deepCopy(symbol_values);
                                data_copy_true = deepCopy(data_copy);
                                data_copy_false = deepCopy(data_copy);
                            } catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                            symbol_assigned_true.set(i,1);
                            symbol_assigned_false.set(i,-1);
                            dpllValues trueValues = this.dpll_loop(data_copy_true, symbol_assigned_true);
                            if (trueValues.result){
                                data_copy = data_copy_true;
                                symbol_values = symbol_assigned_true;
                                return new dpllValues(true, symbol_values, data_copy);
                            }
                            dpllValues falseValues = this.dpll_loop(data_copy_false, symbol_assigned_false);
                            if (falseValues.result){
                                data_copy = data_copy_false;
                                symbol_values = symbol_assigned_false;
                                return new dpllValues(true, symbol_values, data_copy);
                            }
                            else{
                                return new dpllValues(false, symbol_values, data_copy);
                            }
//                            return this.dpll_loop(symbol_assigned_true) || this.dpll_loop(symbol_assigned_false);
                        }
                    }
                }
            }
        }
        return new dpllValues(false, symbol_values, data_copy);
    }

//    public int[] find_pure_symbol(){
//        for (int j=0;j<symbol_values.size();j++){
//            if (symbol_values.get(j)!=0){
//                continue;
//            }
//            int flag_true=0;
//            int flag_false=0;
//            for (int i=0;i<data_copy.size();i++){
//                if (data_copy.get(i).get(j)==1) {
//                    flag_true=1;
//                }
//                else if (data_copy.get(i).get(j)==-1){
//                    flag_false=1;
//                }
//            }
//            if (flag_true==1 && flag_false==0){
//                return new int[]{j, 1};
//            }
//            else if (flag_true==0 && flag_false==1){
//                return new int[]{j, -1};
//            }
//        }
//        return null;
//    }

    public int[] find_pure_symbol(List<List<Integer>>data_copy, List<Integer>symbol_values){
        for (int j=0;j<symbol_values.size();j++){
            if (symbol_values.get(j)!=0){
                continue;
            }
            int flag_true=0;
            int flag_false=0;
            for (int i=0;i<data_copy.size();i++){
                for (int k=0;k<data_copy.get(i).size();k++) {
                    if (data_copy.get(i).get(k) == j+1) {
                        flag_true = 1;
                    } else if (data_copy.get(i).get(k) == -(j+1)) {
                        flag_false = 1;
                    }
                }
            }
            if (flag_true==1 && flag_false==0){
                return new int[]{j, 1};
            }
            else if (flag_true==0 && flag_false==1){
                return new int[]{j, -1};
            }
        }
        return null;
    }

//    public int[] find_unit_clause(){
//        for (int i=0;i<data_copy.size();i++){
//            int symbol_position = -1;
//            for (int j=0;j<data_copy.get(i).size();j++){
//                if (data_copy.get(i).get(j)!=0 && symbol_values.get(j)==0){
//                    if (symbol_position == -1){
//                        symbol_position = j;
//                    }
//                    else{
//                        symbol_position = -1;
//                        break;
//                    }
//                }
//            }
//            if (symbol_position!=-1){
//                return new int[]{symbol_position, data_copy.get(i).get(symbol_position)};
//            }
//        }
//        return null;
//    }

    public int[] find_unit_clause(List<List<Integer>>data_copy, List<Integer>symbol_values){
        for (int i=0;i<data_copy.size();i++){
            int symbol_position = -1;
            int assign_value=0;
            for (int j=0;j<data_copy.get(i).size();j++){
                int value = data_copy.get(i).get(j);
                if (symbol_values.get(abs(value)-1)==0){
                    if (symbol_position == -1){
                        symbol_position = abs(value)-1;
                        assign_value = (value>0)?1:-1;
                    }
                    else{
                        symbol_position = -1;
                        break;
                    }
                }
            }
            if (symbol_position!=-1){
                return new int[]{symbol_position, assign_value};
            }
        }
        return null;
    }

    public void dropSatisfiedClauses(List<List<Integer>> clausesList, List<Integer> symbolValues){
        for (int i=clausesList.size()-1;i>=0;i--){
            if (s.check(clausesList.get(i), symbolValues)==1){
                clausesList.remove(i);
            }
        }
    }

    public static <T> List<T> deepCopy(List<T> src)throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
}
