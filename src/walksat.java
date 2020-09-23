import java.util.*;

public class walksat {
    List<List<Integer>> cnf;
    List<Integer> assignment;
    int MAX_FLIPS = 1000000;
    double probability = 0.5;

    //flip a symbol such that satisfied clauses are maximized
    public List<Integer> maximize_filp(List<List<Integer>> cnf, List<Integer> assignment, List<Integer> clause){
        int max_number = 0;
        int index = 0;
        List<Integer> optimal_assignment = assignment;
        Iterator<Integer> iter_clause_symbol = clause.iterator();
        while(iter_clause_symbol.hasNext()){
            Iterator<List<Integer>> iter_cnf = cnf.iterator();
            List<Integer> temp = assignment;
            temp = flip_assigment(Math.abs(iter_clause_symbol.next()), temp);
            int current_max = 0;
            while (iter_cnf.hasNext()){
                List<Integer> current_clause = iter_cnf.next();
                if (check_clause(current_clause, temp)){
                    current_max += 1;
                }
            }
            if (current_max>max_number){
                max_number = current_max;
                optimal_assignment = temp;
            }
            index += 1;
        }
        return optimal_assignment;
    }
    //randomly flip an assignment of a symbol
    public List<Integer> random_flip(List<Integer> assignment, List<Integer> clause){
        Random r = new Random();
        int random = r.nextInt(3);
        Integer index = Math.abs(clause.get(0));
        return flip_assigment(index,assignment);
    }

    //flip assignment of variables at position index
    public List<Integer> flip_assigment(int index, List<Integer> assignment){
        assignment.set(index-1,-assignment.get(index-1));
        return assignment;
    }

    //check if given clause is satisfied
    public boolean check_clause(List<Integer> clause, List<Integer> assignment){
        Iterator<Integer> iter_clase = clause.iterator();
        while(iter_clase.hasNext()){
            if(check_symbol(iter_clase.next(), assignment)){
                return true;
            }
        }
        return false;
    }

    //check if symbol is satisfied in current assignment
    public boolean check_symbol(Integer symbol, List<Integer> assignment) {
        Iterator<Integer> iter = assignment.iterator();
        while (iter.hasNext()) {
            Integer specific_assignment = iter.next();
            if (Math.abs(symbol) == Math.abs(specific_assignment)) {
                if(symbol==specific_assignment){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        return false;
    }

    public List<Integer> run(List<List<Integer>> cnf, List<Integer> assignment){
        for(int i = 0; i< MAX_FLIPS; i++){
            List<Integer> unsatisfied_clause = cnf.get(0);//arbitrary initialization
            boolean if_satisfied = true;
            for(int j = 0; j<cnf.size();j++){
                if(!check_clause(cnf.get(j),assignment)){
                    unsatisfied_clause = cnf.get(j);
                    if_satisfied = false;
                    break;
                }
            }
            if (if_satisfied){
                return assignment;
            }
            if (probability>Math.random()){
                assignment = maximize_filp(cnf,assignment,unsatisfied_clause);
            }
            else{
                assignment = random_flip(assignment,unsatisfied_clause);
            }
        }
        //System.out.println("No satisfying  assignment found.");
        List<Integer> foo = new LinkedList<>();
        foo.add(-1000);
        return foo;
    }

    public List<Integer> run_instance(List<List<Integer>> cnf, int v){
        long startTime = System.nanoTime();
        List<Integer> assignment = new LinkedList<>();
        for (int j = 1; j <= v; j++) {
            assignment.add(j);
        }
        for(int i = 0; i< MAX_FLIPS; i++){
            List<Integer> unsatisfied_clause = cnf.get(0);//arbitrary initialization
            boolean if_satisfied = true;
            for(int j = 0; j<cnf.size();j++){
                if(!check_clause(cnf.get(j),assignment)){
                    unsatisfied_clause = cnf.get(j);
                    if_satisfied = false;
                    break;
                }
            }
            if (if_satisfied){
                long endTime   = System.nanoTime();
                long totalTime = endTime - startTime;
                System.out.println(((double)totalTime)/(1000000));
                return assignment;
            }
            if (probability>Math.random()){
                assignment = maximize_filp(cnf,assignment,unsatisfied_clause);
            }
            else{
                assignment = random_flip(assignment,unsatisfied_clause);
            }
        }
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println(((double)totalTime)/(1000000));
        //System.out.println("No satisfying  assignment found.");
        List<Integer> foo = new LinkedList<>();
        foo.add(-1000);
        return foo;
    }

    public static List<Integer> convertAssignment(List<Integer> assignment){
        List<Integer> result = new LinkedList<>();
        Iterator<Integer> iter = assignment.iterator();
        while(iter.hasNext()){
            if (iter.next()<0){
                result.add(-1);
            }
            else{
                result.add(1);
            }
        }
        return result;
    }

    public static void main(String[] args){
        int m = Integer.valueOf(args[0]);
        int n = Integer.valueOf(args[1]);
        double T = Double.valueOf(args[2]);
        double working_solution = 0;
        List<List<Integer>> solution = new LinkedList<>();
        List<Long> runtime_for_all = new LinkedList<>();
        List<Long> runtime_for_SAT = new LinkedList<>();
        List<Long> runtime_for_UNSAT = new LinkedList<>();
        List<List<Integer>> data = new LinkedList<>();
        for (int i = 0;i<T;i++){
            walksat walksat = new walksat();
            cnf cnf = new cnf(m,n);
            data = cnf.data;
            List<Integer> assignment = new LinkedList<>();
            for (int j = 1; j <= n; j++) {
                assignment.add(j);
            }
            long startTime = System.nanoTime();
            assignment = convertAssignment(walksat.run(data,assignment));
            long endTime   = System.nanoTime();
            long totalTime = endTime - startTime;
            runtime_for_all.add(totalTime);
            if (assignment.size()!=1){
                working_solution +=1.0;
                solution.add(assignment);
                runtime_for_SAT.add(totalTime);
            }
            else{
                runtime_for_UNSAT.add(totalTime);
            }
        }
        Iterator<Long> avg_runtime_iter = runtime_for_all.iterator();
        long avg_runtime =0;
        while(avg_runtime_iter.hasNext()){
            avg_runtime += avg_runtime_iter.next();
        }
        Iterator<Long> avg_runtime_SAT_iter = runtime_for_SAT.iterator();
        long avg_runtime_SAT =0;
        while(avg_runtime_SAT_iter.hasNext()){
            avg_runtime_SAT += avg_runtime_SAT_iter.next();
        }
        Iterator<Long> avg_runtime_UNSAT_iter = runtime_for_UNSAT.iterator();
        long avg_runtime_UNSAT =0;
        while(avg_runtime_UNSAT_iter.hasNext()){
            avg_runtime_UNSAT += avg_runtime_UNSAT_iter.next();
        }

        if(T == 1){
            System.out.println("Clauses:");
            Iterator<List<Integer>> iter = data.iterator();
            while(iter.hasNext()){
                List<Integer> list = new ArrayList<Integer>(Collections.nCopies(n, 0));
                List<Integer> clause = iter.next();
                Iterator<Integer> clause_iter = clause.iterator();
                while(clause_iter.hasNext()){
                    int temp = clause_iter.next();
                    list.set(Math.abs(temp)-1,Integer.signum(temp));
                }
                for (int i=0;i<list.size();i++) {
                    System.out.print(list.get(i));
                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println("Satisfying Assignment: ");
            if (solution.size()!=0){
                for (int i=0;i<solution.get(0).size();i++) {
                    System.out.print(solution.get(0).get(i));
                    System.out.print(" ");
                }
            }
            else{
                System.out.println("No satisfying assignment found.");
            }

        }
        else{
            System.out.print("Fraction of satisfiable formulae: ");
            System.out.println(working_solution/T);
            System.out.print("Average Runtime: ");
            System.out.print(((double)avg_runtime)/(T*1000000));
            System.out.println("ms");
            System.out.print("Average Runtime for satisfiable formulae: ");
            System.out.print(((double)avg_runtime_SAT)/(working_solution*1000000));
            System.out.println("ms");
            System.out.print("Average Runtime for unsatisfiable formulae: ");
            System.out.print(((double)avg_runtime_UNSAT)/((T-working_solution)*1000000+1));
            System.out.println("ms");
        }
    }
}
