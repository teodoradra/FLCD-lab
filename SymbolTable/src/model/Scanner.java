package model;

import datastructure.HashNode;
import datastructure.SymbolTable;
import error.IllegalIdentifierNameException;
import error.PrintoutError;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Scanner {
    private static final String SOURCE_FILE_PATH = "D:\\Facultate\\Anul 3\\Semestrul 1\\LFTC\\Laboratoare\\FLCD-lab\\SymbolTable\\src\\data\\p1.in";
    private static final String CODIFICATION_FILE_PATH = "D:\\Facultate\\Anul 3\\Semestrul 1\\LFTC\\Laboratoare\\FLCD-lab\\SymbolTable\\src\\data\\token.in";

    private List<String> codification;
    private SymbolTable symbolTable;
    private List<Pair<String, Integer>> pif;

    private List<String> specialRelational = new ArrayList<>();
    private List<String> regularRelational = new ArrayList<>();
    private List<String> allTokens = new ArrayList<>();


    public Scanner(){
        symbolTable = new SymbolTable();
        pif = new ArrayList<>();
        codification = new ArrayList<>();
        this.getCodif();
        this.specialRelational.add(">=");
        this.specialRelational.add("<=");
        this.specialRelational.add("==");
        this.specialRelational.add("in");
        this.regularRelational.add(">");
        this.regularRelational.add("<");
    }

    /**
     * Run the scanner.
     *
     * return: list of errors
   * @throws IOException
     */

    public List<String> run(){
        // prepare list of potential errors
        List<String> errors = new ArrayList<>();

        // parse each line of the file
        int lineNr = 1;
        int skip = 0;
        try {
            for (String line : Files.readAllLines(Paths.get(SOURCE_FILE_PATH))) {
                line = removeExtraSpaces(line);

                if (!line.equals("")) {
                    allTokens.clear();
                    // tokenize line
                    List<String> tokens = new LinkedList<>(Arrays.asList(line.split(" ")));
                    for (String token : tokens){
                        boolean hasSeparator = false;

                        if(skip > 0){
                            skip--;
                        }else {
                            for (String separator: codification){
                                if (token.contains(separator)){
                                    hasSeparator = true;
                                    try {
                                        skip = this.splitWordWithSeparator(token, separator, line) - 1;
                                        break;
                                    }catch (PrintoutError e){
                                        errors.add("Line " + lineNr + e.getMessage() );
                                    }

                                }
                            }
                            if (!hasSeparator) {
                                allTokens.add(token);
                            }
                        }
                    }

                    // identify tokens and log exceptions
                    for (String token : allTokens) {
                        try {
                            identify(token);
                        } catch (IllegalIdentifierNameException e) {
                            errors.add("Line " + lineNr + ": Illegal token \"" + token + "\".");
                        }
                    }
                }
                lineNr++;
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
        return errors;
    }

    private String removeExtraSpaces(String line) {
        return line.trim().replaceAll(" {2,}", " ");
    }

    private String removeExtraSpace(String line){
        return line.trim().replaceAll(" {1,}", "");
    }

    private int splitWordWithSeparator(String word, String separator, String line) throws PrintoutError {
        String[] splitList;
        int toSkip = 0;

        // Parenthesis
        // Strings
        // Treat RHS and LHS in same function

        boolean specialCase = false;
        boolean containsRelational = false;


        if (separator.equals("+=")){
            specialCase = true;
            String[] splitWord = word.split("\\+=");
            String RHS = splitWord[0];
            String LHS = splitWord[1].split(";")[0];
            allTokens.add(RHS);
            allTokens.add(separator);
            allTokens.add(LHS);
            allTokens.add(";");
        }


        if (separator.equals("(")) {
            if (word.contains("PrintOut")){
                    allTokens.add("PrintOut");
                    allTokens.add("(");
                    allTokens.add("\"");
                if (line.indexOf("\"") == line.lastIndexOf("\"")){
                    throw new PrintoutError(" you forgot about one \"");
                }
                else {
                    allTokens.add(removeExtraSpace(line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""))));
                }
                allTokens.add("\"");
                allTokens.add(")");
                allTokens.add(";");
                return line.split(" ").length;
            }
            if (word.contains("ReadFromConsole")){
                allTokens.add("ReadFromConsole");
                allTokens.add("(");
                allTokens.add(removeExtraSpace(line.substring(line.indexOf("(") + 1, line.lastIndexOf(")"))));
                allTokens.add(")");
                allTokens.add(";");
                return line.split(" ").length;
            }

            String extendedWord = line.substring(line.indexOf("(") + 1, line.indexOf(")") );
            List<String> separators = new ArrayList<>();

            specialCase = true;
            String[] RHS;
            String[] LHS;
            for (String specialSeparator : this.codification) {
                if (extendedWord.contains(specialSeparator)) {
                    separators.add(specialSeparator);
                }
            }
            if (separators.size() > 1){
                String[] splited;
                allTokens.add("(");
                toSkip = extendedWord.split(" ").length;
                for (String s: separators) {
                    splited = extendedWord.split(s);
                    extendedWord = splited[1];
                    allTokens.add(removeExtraSpace(splited[0]));
                    allTokens.add(removeExtraSpace(s));
                }
                allTokens.add(removeExtraSpace(extendedWord));
                allTokens.add(")");
                return toSkip;
            }

            for (String specialSeparator : this.specialRelational) {
                if (word.contains(specialSeparator) && separators.size() <= 1 ) {
                    containsRelational = true;
                    splitList = word.split(specialSeparator);
                    RHS = splitList[0].split("\\(");
                    LHS = splitList[1].split("\\)");
                    allTokens.add("(");
                    allTokens.add(RHS[1]);
                    allTokens.add(specialSeparator);
                    allTokens.add(LHS[0]);
                    allTokens.add(")");
                }
            }
            for (String regularSeparator : this.regularRelational) {
                if (word.contains(regularSeparator) && !containsRelational) {
                    containsRelational = true;
                    splitList = word.split(regularSeparator);
                    RHS = splitList[0].split("\\(");
                    LHS = splitList[1].split("\\)");
                    allTokens.add("(");
                    allTokens.add(RHS[1]);
                    allTokens.add(regularSeparator);
                    allTokens.add(LHS[0]);
                    allTokens.add(")");
                }
            }
            if (!containsRelational) {
                splitList = word.split("\\(");
                allTokens.add(separator);
                allTokens.add(splitList[1]);
            }
        }

        if (separator.equals(")")) {
            specialCase = true;
            splitList = word.split("\\)");
            allTokens.add(splitList[0]);
            allTokens.add(separator);
        }

        if (separator.equals("[")) {
            specialCase = true;
            splitList = word.split("\\[");
            String substring = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
            String[] splitedByComa = substring.split(",");
            String[] LHS = splitList[1].split("\\]");
            if (LHS.length == 1 && splitedByComa.length == 1) {
                allTokens.add(splitList[0]);
                allTokens.add(separator);
                allTokens.add(LHS[0]);
                allTokens.add("]");
            } else if (LHS.length == 2 && splitedByComa.length == 1) {
                allTokens.add(splitList[0]);
                allTokens.add(separator);
                allTokens.add(LHS[0]);
                allTokens.add("]");
                allTokens.add(LHS[1]);
            }
            else if(splitedByComa.length >1 ){
                allTokens.add("[");
                String text;
                int currentLength = 0;
                for (String elem: splitedByComa){
                    allTokens.add(elem);
                    if (splitedByComa.length - 1 != currentLength){
                        allTokens.add(",");
                    }
                    currentLength++;
                }
                allTokens.add("]");
                allTokens.add(";");
            }
        }

        if (separator.equals("?")) {
            allTokens.add(separator);
            specialCase = true;
        }

        if (separator.equals("+")) {
            allTokens.add(separator);
            specialCase = true;
        }

        if (separator.equals(".")) {
            splitList = word.split("\\.");
            allTokens.add(splitList[0]);
            allTokens.add(separator);
            specialCase = true;
        }

        if (separator.equals("{")){
            allTokens.add(separator);
            specialCase = true;
        }
        if (separator.equals("}")){
            allTokens.add(separator);
            specialCase = true;
        }

        if (!specialCase) {
            splitList = word.split(separator);
            if (splitList.length == 0) {
                allTokens.add(separator);
            }
            if (splitList.length == 1) {
                allTokens.add(splitList[0]);
                allTokens.add(separator);
            }
            if (splitList.length == 2) {
                if (!splitList[0].equals("")) {
                    allTokens.add(splitList[0]);
                }

                allTokens.add(separator);
                allTokens.add(splitList[1]);
            }
        }
        return 0;
    }


    private void identify(String part) throws IllegalIdentifierNameException {
        if (codification.contains(part)){
            pif.add(new Pair<>(part, -1));
        } else {
            if (isStCandidate(part)){
                if (symbolTable.get(part) == -1){
                    int index = addToSt(part);
                    addToPif(new Pair<>(part, index));
                } else {
                    int index = symbolTable.get(part);
                    addToPif(new Pair<>(part, index));
                }
            }
        }
    }


    private void addToPif(Pair<String, Integer> pair) {
        pif.add(pair);
    }

    private int addToSt(String part) {
        return symbolTable.add(part);
    }


    private boolean isStCandidate(String part) throws
            IllegalIdentifierNameException {
        if (isConstant(part)) {
            return true;
        } else {
            if (isIdentifier(part)) {
                return true;
            } else {
                throw new IllegalIdentifierNameException(part);
            }
        }
    }

    private boolean isIdentifier(String part) {
        return part.matches("(^[a-zA-Z]+[_0-9a-zA-Z]*)");
    }

    private boolean isConstant(String part) {
        return part.matches("\\-?[1-9]+[0-9]*|0")
                || part.matches("'[1-9a-zA-Z]'")
                || part.matches("\"[1-9a-zA-Z]+\"");
    }

    private void getCodif() {
        try {
            codification.addAll(Files.readAllLines(Paths.get(CODIFICATION_FILE_PATH)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Language codification: " + codification + '\n');
    }


    public void writePif(){
        try{
            FileWriter writePifFile = new FileWriter("D:\\Facultate\\Anul 3\\Semestrul 1\\LFTC\\Laboratoare\\FLCD-lab\\SymbolTable\\src\\data\\PIF.out");
            for (Pair<String, Integer> pair : pif){
                writePifFile.append(pair.getKey()).append(" : ").append(String.valueOf(pair.getValue())).append("\n");
            }
            writePifFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeST(){
        try{
            FileWriter writePifFile = new FileWriter("D:\\Facultate\\Anul 3\\Semestrul 1\\LFTC\\Laboratoare\\FLCD-lab\\SymbolTable\\src\\data\\St.out");
//            for (HashNode<Integer, String> pair : symbolTable.getSymbolTable()){
////                writePifFile.append((char) pair.getKey()).append(" : ").append(String.valueOf(pair.getValue())).append("\n");
////            }
            writePifFile.write(symbolTable.tostring());
            writePifFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
