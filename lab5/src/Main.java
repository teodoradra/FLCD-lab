import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        File file = new File("D:\\Facultate\\Anul 3\\Semestrul 1\\LFTC\\Laboratoare\\FLCD-lab\\lab5\\src\\data\\g1.json");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String str = new String(data, "UTF-8");

        Grammar grammar =  new ObjectMapper().readValue(str, Grammar.class);

        System.out.println("Non-terminals: \n" + grammar.nonTerminals);
        System.out.println("Terminals: \n" + grammar.terminals);
        System.out.println("Productions: \n" + grammar.productions);

        System.out.println("\nProductions for non-terminal identifier:");
//        System.out.println(grammar.getProduction("IDENTIFIER"));

        Parser parser = new Parser(grammar);

//                new ObjectMapper().writeValue(
//                new File("outputba.json"),
//                parser.parseInput("0 0 0 1 1 0 1 1 1 0")
//        );
//        System.out.println("FOLLOW: ");
//        System.out.println(parser.follow("S", "S"));
//        System.out.println(parser.follow("A", "A"));
//        System.out.println(parser.follow("B", "B"));
//        parser.createParseTable();
//        System.out.println("Parse table: ");
//        System.out.println(parser.parseTable);

//        System.out.println("Long bla vla" + parser.parse("program array a <- [ 5 , 6 , 7 , 8 ] int r <- 1 string test <- \"123\"  for int nas : a { if ( n % 2 = 0 ) { r += nas } }\n"));

//        System.out.println("Long bla vla" + parser.parse("int a"));



        System.out.println("Is -1 0 1 1 1 1- accepted?  " + parser.parse("1 0 1 1 1 1"));
        parser.toTree();

        System.out.println("Is -0 1- accepted?  " + parser.parse("0 1"));
        parser.toTree();
        System.out.println("Is -0 0 1 1- accepted?  " + parser.parse("0 0 1 1"));
        parser.toTree();
        System.out.println("Is -0 0 0 1 1 1- accepted?  " + parser.parse("0 0 0 1 1 1"));
        parser.toTree();
//        System.out.println("Is -1 1 0 0- accepted?  " + parser.parse("1 1 0 0"));
//        System.out.println("Is -0 0 0 0- accepted?  " + parser.parse("0 0 0 0"));

    }
}
