import javafx.util.Pair;

import java.sql.ClientInfoStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Grammar {

    public List<String> nonTerminals;
    public Set<String> terminals;
    public Map<String, List<List<String>>> productions;
    private String startingSymbol;

    public String getProduction(String nonTerminal) throws Exception {
        if (!nonTerminals.contains(nonTerminal))
            throw new Exception("Non-terminal not in list");

        return productions.get(nonTerminal).stream().map(x -> nonTerminal + " -> " + x.toString()).collect(Collectors.joining("\n"));

    }

    public String getStartingSymbol() {
        return startingSymbol;
    }

    public Set<Pair<String, List<String>>> getProductionContainingNonTerminal(String nonTerminal){
        Set<Pair<String, List<String>>> productionsForNonTerminal = new HashSet<>();

        for (Map.Entry<String, List<List<String>>> production: productions.entrySet()) {
            for (List<String> prod : production.getValue()) {
                if (prod.contains(nonTerminal))
                    productionsForNonTerminal.add(new Pair<>(production.getKey(), prod));
            }
        }
//            if (production.getValue().stream().anyMatch(x -> x.contains(nonTerminal)))
//                productionsForNonTerminal.add(new Pair<>(production.getKey(), production.getValue()));
//        }
        return productionsForNonTerminal;
    }
}
