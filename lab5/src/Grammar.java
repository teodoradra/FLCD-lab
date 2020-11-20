import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Grammar {

    public List<String> nonTerminals;
    public Set<String> terminals;
    public Map<String, List<List<String>>> productions;

    public String getProduction(String nonTerminal) throws Exception {
        if (!nonTerminals.contains(nonTerminal))
            throw new Exception("Nonterminal not in list");

        return productions.get(nonTerminal).stream().map(x -> nonTerminal + " -> " + x.toString()).collect(Collectors.joining("\n"));

    }

}
