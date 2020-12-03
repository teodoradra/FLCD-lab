import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import javafx.util.Pair;

import java.util.*;

public class Parser {
    private Grammar grammar;
    private Map<String, Set<String>> followSet;
    private Map<String, Set<String>> firstSet;
    public ParserOutput output;
    public ParseTable parseTable = new ParseTable();
    private static Stack<List<String>> rules = new Stack<>();
    private Map<Pair<String, List<String>>, Integer> productionsNumbered = new HashMap<>();


    public Parser(Grammar grammar){
        output = new ParserOutput();
        this.grammar = grammar;
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();
        generateSets();
    }

    private void generateSets(){
        generateFirstSet();
        generateFollowSet();
        createParseTable();
    }

    private void generateFollowSet() {
        for (String nonTerminal : grammar.nonTerminals) {
            followSet.put(nonTerminal, this.follow(nonTerminal, nonTerminal));
        }
    }

    private void generateFirstSet() {
        for (String nonTerminal : grammar.nonTerminals) {
            firstSet.put(nonTerminal, this.first(nonTerminal));
        }
    }

    public void createParseTable(){
        numberingProductions();

        List<String> columnSymbols = new LinkedList<>(grammar.terminals);
        columnSymbols.add("$");

        // M(a, a) = pop
        // M($, $) = acc

        parseTable.put(new Pair<>("$", "$"), new Pair<>(Collections.singletonList("acc"), -1));
        for (String terminal: grammar.terminals)
            parseTable.put(new Pair<>(terminal, terminal), new Pair<>(Collections.singletonList("pop"), -1));

//        1) M(A, a) = (α, i), if:
//            a) a ∈ first(α)
//            b) a != ε
//            c) A -> α production with index i
//
//        2) M(A, b) = (α, i), if:
//            a) ε ∈ first(α)
//            b) whichever b ∈ follow(A)
//            c) A -> α production with index i


        productionsNumbered.forEach((key, value)-> {
            String rowSymbol = key.getKey();
            List<String> rule = key.getValue();
            Pair<List<String>, Integer> parseTableValue = new Pair<>(rule, value);

            for (String columnSymbol: columnSymbols){
                Pair<String, String> parseTableKey = new Pair<>(rowSymbol, columnSymbol);
//                System.out.println(grammar.nonTerminals.contains(rule.get(0)));
//                System.out.println(firstSet.get(rule.get(0)));
//                System.out.println(rule.get(0));
                // if our column-terminal is exactly first of rule
                if (rule.get(0).equals(columnSymbol) && !columnSymbol.equals("ε")) {
                    parseTable.put(parseTableKey, parseTableValue);

                    // if the first symbol is a non-terminal and it's first contain our column-terminal
                }
                else if (grammar.nonTerminals.contains(rule.get(0)) && firstSet.get(rule.get(0)).contains(columnSymbol)) {
                    if (!parseTable.containsKey(parseTableKey)) {
                        parseTable.put(parseTableKey, parseTableValue);
                    }
                }
                else {
                    // if the first symbol is e then everything if FOLLOW(rowSymbol) will be in spare table
                    if (rule.get(0).equals("ε")) {
                        for (String b : followSet.get(rowSymbol))
                            parseTable.put(new Pair<>(rowSymbol, b), parseTableValue);
                        // if ε is in first(rule)
                    } else {
                        Set<String> firsts = new HashSet<>();
                        for (String symbol: rule)
                            if (grammar.nonTerminals.contains(symbol))
                                firsts.addAll(firstSet.get(symbol));
                        if (firsts.contains("ε")){
                            for (String b: firstSet.get(rowSymbol)){
                                if (b.equals("ε"))
                                    b = "$";
                                parseTableKey = new Pair<>(rowSymbol, b);
                                if (!parseTable.containsKey(parseTableKey))
                                    parseTable.put(parseTableKey, parseTableValue);
                            }
                        }
                    }
                }
            }
        });
    }

    public boolean parse(String input){

        List<String> w = Arrays.asList(input.split(" "));

        Stack<String> alpha = new Stack<>();
        Stack<String> beta = new Stack<>();
        Stack<String> pi = new Stack<>();
        alpha.push("$");
        for (int i = w.size() - 1; i >= 0; i--){
            alpha.push(w.get(i));
        }

        beta.push("$");
        beta.push(grammar.getStartingSymbol());

        pi.push("ε");

        boolean go = true;
        int index = 0, depth = 0;
        boolean result = true;

        while (go){
//            System.out.println(beta);
            String betaHead = beta.peek();
            String alphaHead = alpha.peek();

            if (betaHead.equals("$") && alphaHead.equals("$")){
                return result;
            }

            Pair<String, String> heads = new Pair<>(betaHead, alphaHead);
            Pair<List<String>, Integer> parseTableEntry = parseTable.get(heads);

            if (parseTableEntry == null){
                heads = new Pair<>(betaHead, "ε");
                parseTableEntry = parseTable.get(heads);
                if (parseTableEntry != null) {
                    beta.pop();
                    continue;
                }
            }

            if (parseTableEntry == null){
                go = false;
                result = false;
            }
            else
            {
                List<String> production = parseTableEntry.getKey();
                Integer productionPosition = parseTableEntry.getValue();

                if (productionPosition == -1 && production.get(0).equals("acc"))
                    go = false;
                else if (productionPosition == -1 && production.get(0).equals("pop")) {
                    beta.pop();
                    alpha.pop();
                }
                else {
                    String current = beta.pop();
                    if (!production.get(0).equals("ε")) {
                        for (int i = production.size() - 1; i >= 0; i--) {
                            beta.push(production.get(i));
                        }

                        output.getOutput().put(index, 0, Collections.singletonList(current));
                        output.getOutput().put(index, 1, production);
                        output.getOutput().put(index, 2, Collections.singletonList(String.valueOf(depth)));
                        index++;

                        /*for(String value : production){
                            output.put(index, 0, current);
                            output.put(index, 1, value+ ":" + (index + 1));
                            output.put(index, 2, String.valueOf(depth));
                            index++;
                        }*/
                    }
                    pi.push(productionPosition.toString());
                    depth++;
                }
            }
        }
        return result;
    }

    public Set<String> first(String nonTerminal){
        if (firstSet.containsKey(nonTerminal))
            return firstSet.get(nonTerminal);

        Set<String> temp = new HashSet<>();
        Set<String> terminals = grammar.terminals;

//        System.out.println(nonTerminal);

        for (List<String> list: grammar.productions.get(nonTerminal)){
            String firstSymbol = list.get(0);
            if (firstSymbol.equals("ε"))
                temp.add("ε");
            else if (terminals.contains(firstSymbol))
                temp.add(firstSymbol);
            else
                temp.addAll(first(firstSymbol));
        }
        return temp;
    }

    public Set<String> follow(String nonTerminal, String initialNonTerminal){
        if (followSet.containsKey(nonTerminal))
            return followSet.get(nonTerminal);
        Set<String> temp = new HashSet<>();
        Set<String> terminals = grammar.terminals;

        if (nonTerminal.equals(grammar.getStartingSymbol()))
            temp.add("$");

        for (Pair<String, List<String>> production: grammar.getProductionContainingNonTerminal(nonTerminal)){
            String productionStart = production.getKey();
            List<String> rule = production.getValue();
            List<String> ruleConflict = new ArrayList<>();
            ruleConflict.add(nonTerminal);
            ruleConflict.addAll(rule);
            if (rule.contains(nonTerminal) && !rules.contains(ruleConflict)){
                rules.push(ruleConflict);
                int indexNonTerminal = rule.indexOf(nonTerminal);
                temp.addAll(followOperation(nonTerminal, temp, terminals,productionStart, rule, indexNonTerminal, initialNonTerminal));

                List<String> sublist = rule.subList(indexNonTerminal + 1, rule.size());
                if (sublist.contains(nonTerminal))
                    temp.addAll(followOperation(nonTerminal, temp, terminals, productionStart, rule, indexNonTerminal + 1 + sublist.indexOf(nonTerminal), initialNonTerminal));

                rules.pop();
                }
            }
        return temp;
    }

    private Set<String> followOperation(String nonTerminal, Set<String> temp, Set<String> terminals, String productionStart, List<String> rule, int indexNonTerminal, String initialNonTerminal) {
        if (indexNonTerminal == rule.size() - 1) {
            if (productionStart.equals(nonTerminal))
                return temp;
            if (!initialNonTerminal.equals(productionStart)){
                temp.addAll(follow(productionStart, initialNonTerminal));
            }
        }
        else
        {
            String nextSymbol = rule.get(indexNonTerminal + 1);
            if (terminals.contains(nextSymbol))
                temp.add(nextSymbol);
            else{
                if (!initialNonTerminal.equals(nextSymbol)) {
                    Set<String> fists = new HashSet<>(first(nextSymbol));
                    if (fists.contains("ε")) {
                        temp.addAll(follow(nextSymbol, initialNonTerminal));
                        fists.remove("ε");
                    }
                    temp.addAll(fists);
                }
            }
        }
        return temp;
    }

    private void numberingProductions(){
        int index = 1;
        for (Map.Entry<String, List<List<String>>> productions : grammar.productions.entrySet())
            for (List<String> rule: productions.getValue())
                productionsNumbered.put(new Pair<>(productions.getKey(), rule), index++);
    }

    public void toTree(){
        output.toTree(this);
    }
}
