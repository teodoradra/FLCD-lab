import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParserOutput {
    public Table<Integer, Integer, List<String>> output;

    public ParserOutput(){
        output = TreeBasedTable.create();
    }

    public Table<Integer, Integer, List<String>> getOutput() {
        return output;
    }

    public void toTree(Parser parser) {
        List<String> previous = new ArrayList<>();
        previous.add(parser.output.getOutput().row(0).get(0).get(0));

        int index = previous.size();
        for (Integer row: parser.output.getOutput().rowKeySet()) {
            System.out.println(previous.toString());
            System.out.println(" | ");
            //System.out.println(parser.output.row(row).get(0).get(0));
            try{
                index = previous.indexOf(
                        previous.stream().filter(
                                elem -> elem.equals(parser.output.getOutput().row(row).get(0).get(0))
                        ).findFirst().get()
                );
            }catch (Exception ex) {
                index++;
            }

            if(index>=previous.size()) {
                //System.out.println(parser.output.row(row).get(1).get(0));
                previous.add(parser.output.getOutput().row(row).get(1).get(0));
            }
            else
                previous.set(
                        index,
                        parser.output.getOutput().row(row).get(1).get(0)
                );

            for(String newElem : parser.output.getOutput().row(row).get(1).stream().skip(1).collect(Collectors.toList()))
            {
                index++;
                if(index>=previous.size()) {
                    //System.out.println(newElem);
                    previous.add(newElem);
                }
                else
                    previous.add(
                            index,
                            newElem
                    );
            }
        }

        System.out.println(previous);
    }
}
