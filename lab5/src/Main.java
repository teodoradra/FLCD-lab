import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        File file = new File("D:\\Facultate\\Anul 3\\Semestrul 1\\LFTC\\Laboratoare\\FLCD-lab\\lab5\\src\\data\\g3.json");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String str = new String(data, "UTF-8");

        Grammar grammar =  new ObjectMapper().readValue(str, Grammar.class);

//        System.out.println("Non-terminals: \n" + grammar.nonTerminals);
//        System.out.println("Terminals: \n" + grammar.terminals);
//        System.out.println("Productions: \n" + grammar.productions);

        Parser parser1 = new Parser(grammar);

//        System.out.println("Parse table: ");
//        System.out.println(parser.parseTable);

        System.out.println("Verifying:\n--- int a <- 5 ; int r <- 1 ; int t <- 2 ; for ( int m : a ) { if ( n % 2 = 0 ) { r <- r + m ; } ; } --- ... "
                + parser1.parse("int a <- 5 ; int r <- 1 ; int t <- 2 ; for ( int m : a ) { if ( n % 2 = 0 ) { r <- r + m ; } ; }") + "\n");
        parser1.toTree();

        Parser parser2 = new Parser(grammar);
        System.out.println("Verifying: \n--- int a <- 5 ; int r <- 1 ; int t <- 2 ; for { int m : a ) { if ( n % 2 = 0 ) { r <- r + m ; } ; } --- ..."
                + parser2.parse("int a <- 5 ; int r <- 1 ; int t <- 2 ; for { int m : a ) { if ( n % 2 = 0 ) { r <- r + m ; } ; }"));
        parser2.toTree();


//        System.out.println("Is -1 0 1 1 1 1- accepted?  " + parser.parse("1 0 1 1 1 1"));
//        parser.toTree();
//
//        System.out.println("Is -0 1- accepted?  " + parser.parse("0 1"));
//        parser.toTree();
//        System.out.println("Is -0 0 1 1- accepted?  " + parser.parse("0 0 1 1"));
//        parser.toTree();
//        System.out.println("Is -0 0 0 1 1 1- accepted?  " + parser.parse("0 0 0 1 1 1"));
//        parser.toTree();
//        System.out.println("Is -1 1 0 0- accepted?  " + parser.parse("1 1 0 0"));
//        System.out.println("Is -0 0 0 0- accepted?  " + parser.parse("0 0 0 0"));

    }
}
