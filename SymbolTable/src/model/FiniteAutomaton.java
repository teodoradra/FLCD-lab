package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class FiniteAutomaton {
    private String path;
    private List<String> alphabet;
    private List<String> states;
    private List<String> finalStates;
    private String initialState;
    private List<String>[][] matrix = new ArrayList[100][100];
    private int iMatrix ;
    private int jMatrix;

    public FiniteAutomaton(String path)  {
        this.path = path;
        alphabet = new ArrayList<>();
        finalStates = new ArrayList<>();
        states = new ArrayList<>();
        tokenize();
    }

    private void tokenize() {
        File file = new File(path);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        }catch (FileNotFoundException e){
            e.getMessage();
        }
        StringTokenizer tokenizer;

        tokenizer = new StringTokenizer(scanner.nextLine(), " ");
        while (tokenizer.hasMoreTokens()) {
            alphabet.add(tokenizer.nextToken());
        }
        jMatrix = alphabet.size();
        int j,i = 0;
        while (scanner.hasNextLine()){
            j=0;

            tokenizer = new StringTokenizer(scanner.nextLine(), " ");
            if (tokenizer.hasMoreTokens()){
                String token = tokenizer.nextToken();
                if (token.equals("->") && tokenizer.hasMoreTokens()){
                    token = tokenizer.nextToken();
                    if (token.equals("*") && tokenizer.hasMoreTokens()) {
                        token = tokenizer.nextToken();
                        finalStates.add(token);
                    }
                    initialState = token;
                } else if (token.equals("*") && tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken();
                    finalStates.add(token);
                }
                states.add(token);

                while (tokenizer.hasMoreTokens()){
                    matrix[i][j] = new ArrayList<>();
                    String resultState = tokenizer.nextToken();
                    if (resultState.equals("ε")){
                        matrix[i][j].add("ε");
                    } else {
                        StringTokenizer resultStateTokenizer = new StringTokenizer(resultState, ",");
                        while (resultStateTokenizer.hasMoreTokens()){
                            matrix[i][j].add(resultStateTokenizer.nextToken());
                        }
                    }
                    j++;
                }
                i++;
            }
        }
        iMatrix = states.size();
    }

    public String alphabet(){
        return alphabet.toString();
    }

    public String getInitialState() {
        return initialState;
    }

    public String getFinalStates() {
        return finalStates.toString();
    }

    public String getStates() {
        return states.toString();
    }

    public String transitions(){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < iMatrix; i++){
            for (int j = 0; j<jMatrix; j++){
                for (String e: matrix[i][j]) {
                    result.append("δ(").append(states.get(i)).append(",").append(alphabet.get(j)).append(") = ")
                            .append(e).append("\n");
                }
            }
        }
        return result.toString();
    }

    public boolean isDeterministic(){
        boolean isDeterministic = true;
        for (int i = 0; i < iMatrix; i++){
            for (int j = 0; j<jMatrix; j++){
                if (matrix[i][j].size() > 1) {
                    isDeterministic = false;
                    break;
                }
            }
        }
        return isDeterministic;
    }

    public boolean isOk(String str){
        boolean isOk = true;
        String firstState = initialState;

        while (isOk && str.length()>0){
            String character = String.valueOf(str.charAt(0));
            int index_state = states.indexOf(firstState);
            int index_alphabet = alphabet.indexOf(character);
            if (alphabet.contains(character)){
                List<String> resultSymbol = matrix[states.indexOf(firstState)][alphabet.indexOf(character)];
                if (resultSymbol.size() > 0 && !resultSymbol.get(0).equals("ε")){
                    str = str.substring(1);
                    firstState = resultSymbol.get(0);
                } else {
                    isOk = false;
                }
            } else {
                isOk = false;
            }
        }
        return isOk;
    }

}
