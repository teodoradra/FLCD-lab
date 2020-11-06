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
    private List<String>[][] matrix = new ArrayList[25][25];
    private int iMatrix = 4;
    private int jMatrix = 11;

    public FiniteAutomaton()  {
        path = "D:\\Facultate\\Anul 3\\Semestrul 1\\LFTC\\Laboratoare\\FLCD-lab\\SymbolTable\\src\\data\\fa\\FA.in";
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
//                if (matrix[i][j] != null){
//                    result.append("δ(").append(states.get(i)).append(",").append(alphabet.get(j)).append(") = ")
//                            .append(matrix[i][j]).append("\n");
//                }
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

}
