package src;

import com.diogonunes.jcolor.Attribute;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;

public class Main {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static Scanner userInput;

    public static void main(String[] args) {

        System.out.print(colorize("---", Attribute.YELLOW_TEXT()));
        System.out.print(colorize("TURING MACHINE SIM", Attribute.BRIGHT_RED_TEXT()));
        System.out.print(colorize("---\n", Attribute.YELLOW_TEXT()));
        userInput = new Scanner(System.in);
        mainMenu();

    }

    public static void mainMenu(){
        System.out.println("Type "+ colorize("new", Attribute.BRIGHT_GREEN_TEXT()) +" to create a new machine.\n" +
                "Type " + colorize("load", Attribute.BRIGHT_GREEN_TEXT()) + " to load a machine from a file.\n" +
                "Type " + colorize("exit", Attribute.BRIGHT_GREEN_TEXT()) + " to close the app.");
        String action = userInput.nextLine();
        if(action.strip().compareToIgnoreCase("new") == 0){
            System.out.println("Lets create a new machine!");
            Machine newMachine = createNewMachine();
            machineMainMenu(newMachine);
        } else if(action.strip().compareToIgnoreCase("load") == 0){
            System.out.println("Lets load a machine!\nSelect a machine by entering the name or the index.");
            //print files from directory
            File dir = new File("machines");
            //nice lambda usage :DDDD
            String[] machinesList = dir.list((file, s) -> s.endsWith(".json"));
            for (int i = 0; i < machinesList.length; i++){
                System.out.println("["+i+"] "+machinesList[i] + " opis maszyny: " + Machine.getMachineDescriptionFromJson("machines/"+machinesList[i]));
            }

            //parse by index or by name
            String selMachine = "";
            boolean validMachineSelected = false;
            while(!validMachineSelected) {
                action = userInput.nextLine();
                if (action.matches("-?\\d+")) {
                    //is integer
                    if (Integer.parseInt(action) < machinesList.length && Integer.parseInt(action) > -1){
                        selMachine = machinesList[Integer.parseInt(action)];
                        validMachineSelected = true;
                    }
                }
                else {
                    if(Arrays.asList(machinesList).contains(action)){
                        selMachine = action;
                        validMachineSelected = true;
                    } else if(Arrays.asList(machinesList).contains(action+".json")){
                        selMachine = action+".json";
                        validMachineSelected = true;
                    }
                }
                if(!validMachineSelected){
                    System.out.println("Sorry, this machine doesnt seem to exist!\nRetry selecting the machine.");
                }
            }

            Machine newMachine = Machine.fromJson("machines/"+selMachine.strip());
            machineMainMenu(newMachine);
        } else if(action.strip().compareToIgnoreCase("exit") == 0){
            System.exit(0);
        } {
            System.out.println("Invalid command.");
            mainMenu();
        }
    }

    public static void machineMainMenu(Machine MT){
        System.out.println("Type run to enter a tape and run it on the machine.\nType save to save the machine.\nType menu to exit to main menu.");
        String action = userInput.nextLine();
        if (action.strip().compareToIgnoreCase("run") == 0) {
            boolean running = true;
            while(running){
                System.out.println("Enter the tape.\n");
                String tapeInput = userInput.nextLine();
                Tape tape = new Tape(tapeInput);
                MT.setTape(tape);
                MT.run();
                System.out.println("Would you like to enter another tape? [Y/n]");
                action = userInput.nextLine();
                if (action.strip().equalsIgnoreCase("N")) {
                    running = false;
                    machineMainMenu(MT);
                }
            }

        } else if (action.strip().compareToIgnoreCase("save") == 0) {
            System.out.println("Enter the name of the machine.");
            String machineName = userInput.nextLine();
            System.out.println("You can also create a short description for the machine.\nIf you want to skip this step, just dont enter anything an press Enter.");
            String machineDescription = userInput.nextLine();
            MT.setName(machineName, machineDescription);
            MT.saveMachine("machines/"+machineName.strip()+".json");
            System.out.println("Machine saved!\n");
            machineMainMenu(MT);

        } else if (action.strip().compareToIgnoreCase("menu") == 0) {
            System.out.println("Exiting to main menu.");
            mainMenu();
        } else {
            System.out.println("Invalid command.");
            machineMainMenu(MT);
        }
    }

    public static Machine createNewMachine(){
        Scanner userInput = new Scanner(System.in);

        System.out.println("Define the alphabet - enter all allowed symbols, separated by a coma.\n");
        String alphabetString = userInput.nextLine();
        String[] alphabetChars = alphabetString.split(",");
        char[] alphabet = new char[alphabetChars.length];
        for (int i = 0; i < alphabetChars.length; i++) {
            alphabet[i] = alphabetChars[i].charAt(0);
        }

        System.out.println("Define the machine's moves.\nTemplate: current_state  ,  char_under_the_head  ,  new_state  ,  new_char  ,  direction\nWhen youre done, type 'end'\n");
        ArrayList<String> inputMoves = new ArrayList<String>();
        String input = "";
        while (true) {
            input = userInput.nextLine();
            if (input.equalsIgnoreCase("end")) {
                break;
            } else {
                inputMoves.add(input);
            }
        }
        String[] deltaString = new String[inputMoves.size()];
        for (int i = 0; i < deltaString.length; i++) {
            deltaString[i] = inputMoves.get(i);
        }
        DeltaFunc delta = new DeltaFunc(deltaString);

        System.out.println("Define the end states - enter their indexes, separated by a coma.\n");
        String endStates = userInput.nextLine();
        String[] endStatesArray = endStates.split(",");
        int[] endStatesInt = new int[endStatesArray.length];
        for (int i = 0; i < endStatesArray.length; i++) {
            endStatesInt[i] = Integer.parseInt(endStatesArray[i]);
        }

        System.out.println("Enter the starting state.\n");
        int start = Integer.parseInt(userInput.nextLine());

        System.out.println("The machine is configured!");

        Machine MT = new Machine(null, alphabet, delta, endStatesInt, start);
        return MT;
    }
}




//System.out.println("---TURING MACHINE SIMULATION---");
//        Scanner userInput = new Scanner(System.in);
//
//        System.out.println("Define the alphabet - enter all allowed symbols, separated by a coma.\n");
//        String alphabetString = userInput.nextLine();
//        String[] alphabetChars = alphabetString.split(",");
//        char[] alphabet = new char[alphabetChars.length];
//        for (int i = 0; i < alphabetChars.length; i++) {
//            alphabet[i] = alphabetChars[i].charAt(0);
//        }
//
//        System.out.println("Define the machine's moves.\nTemplate: current_state  ,  char_under_the_head  ,  new_state  ,  new_char  ,  direction\nWhen youre done, type 'end'\n");
//        ArrayList<String> inputMoves = new ArrayList<String>();
//        String input = "";
//        while (true) {
//            input = userInput.nextLine();
//            if (input.equalsIgnoreCase("end")) {
//                break;
//            } else {
//                inputMoves.add(input);
//            }
//        }
//        String[] deltaString = new String[inputMoves.size()];
//        for (int i = 0; i < deltaString.length; i++) {
//            deltaString[i] = inputMoves.get(i);
//        }
//        DeltaFunc delta = new DeltaFunc(deltaString);
//
//        System.out.println("Define the end states - enter their indexes, separated by a coma.\n");
//        String endStates = userInput.nextLine();
//        String[] endStatesArray = endStates.split(",");
//        int[] endStatesInt = new int[endStatesArray.length];
//        for (int i = 0; i < endStatesArray.length; i++) {
//            endStatesInt[i] = Integer.parseInt(endStatesArray[i]);
//        }
//
//        System.out.println("Enter the starting state.\n");
//        int start = Integer.parseInt(userInput.nextLine());
//
//        System.out.println("The machine is configured!");
//
//        boolean run = true;
//        while (run) {
//            System.out.println("Enter the tape.\n");
//            String tapeInput = userInput.nextLine();
//            Tape tape = new Tape(tapeInput);
//
//            Machine MT = new Machine(tape, alphabet, delta, endStatesInt, start);
//            MT.saveMachine("machines/a.txt");
//            //MT.run();
//
//
//            System.out.println("Would like to enter a new tape? [Y/N]");
//            String ans = userInput.nextLine();
//            if (ans.equalsIgnoreCase("N")) {
//                run = false;
//            }
//        }
//
////        File inputFile = new File("a.txt");
////        try {
////            Scanner fileReader = new Scanner(inputFile);
////            String jsonString = fileReader.nextLine();
////            //System.out.println(jsonString);
////            JSONObject machineData = new JSONObject(jsonString);
////            System.out.println(machineData.get("delta").getClass());
////        } catch (FileNotFoundException e) {
////            e.printStackTrace();
////        }
//    }
//

