import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class instance {
    public static void main(String[] args){
        String filePath = args[0];
        List<List<Integer>> f = readTxtFileIntoStringArrList(filePath);
        dpll dpll=new dpll();
        dpll.run_instance(f, 100);
        walksat walksat = new walksat();
        walksat.run_instance(f, 100);
    }
    public static List<List<Integer>> readTxtFileIntoStringArrList(String filePath)
    {
        List<List<Integer>> list = new ArrayList<>();
        try
        {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists())
            {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                bufferedReader.readLine();
                bufferedReader.readLine();
                bufferedReader.readLine();
                bufferedReader.readLine();
                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                    String[] split = lineTxt.split("\\s+");
                    List<Integer> singleLine = new ArrayList<>();
                    for (int i=0;i<3;i++) {
                        singleLine.add(Integer.parseInt(split[i]));
                    }
                    list.add(singleLine);
                }
                bufferedReader.close();
                read.close();
            }
            else
            {
                System.out.println("No such file!");
            }
        }
        catch (Exception e)
        {
            System.out.println("Error when reading file.");
            e.printStackTrace();
        }

        return list;
    }
}
