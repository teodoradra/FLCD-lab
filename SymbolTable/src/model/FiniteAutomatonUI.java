package model;
import java.util.Scanner;

public class FiniteAutomatonUI {
    public FiniteAutomatonUI(){
    }

    public static void printMenu(){
        System.out.println("1. Print the set of states \n");
        System.out.println("2. Print the alphabet \n");
        System.out.println("3. Print all the transitions \n");
        System.out.println("4. Print the set of final states \n");
        System.out.println("5. Is this FA deterministic? \n");
        System.out.println("0. Exit \n");
    }

    public static void main(String[] args) {
        FiniteAutomaton fa = new FiniteAutomaton();
        Scanner console = new Scanner(System.in);
        printMenu();

        while (true) {
            System.out.println("Write your option: ");
            switch (console.nextInt()) {
                case 1:
                    System.out.println("States : " + fa.getStates());
                    break;
                case 2:
                    System.out.println("Alphabet: " + fa.alphabet());
                    break;
                case 3:
                    System.out.println("Transitions: " + fa.transitions());
                    break;
                case 4:
                    System.out.println("Final states: " + fa.getFinalStates());
                    break;
                case 5:
                    System.out.println("FA is deterministic: " + fa.isDeterministic());
                    break;
                case 0:
                    System.exit(0);
                    printMenu();
                default:
                    System.out.println("Invalid!");
                    break;
            }
        }
    }
}
