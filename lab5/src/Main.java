import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        File file = new File("D:\\Facultate\\Anul 3\\Semestrul 1\\LFTC\\Laboratoare\\lab5\\src\\data\\g1.json");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String str = new String(data, "UTF-8");

        Grammar grammar =  new ObjectMapper().readValue(str, Grammar.class);

        System.out.println("Non-terminals: \n" + grammar.nonTerminals);
        System.out.println("Terminals: \n" + grammar.terminals);
        System.out.println("Productions: \n" + grammar.productions);

        System.out.println("\nProductions for non-terminal A:");
        System.out.println(grammar.getProduction("A"));

    }
}
