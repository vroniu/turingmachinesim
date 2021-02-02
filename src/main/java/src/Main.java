package src;

import com.diogonunes.jcolor.Attribute;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;

public class Main {

    public static Scanner userInput;
    protected static Settings userSettings;
    public static String line = new String(new char[51]).replace('\0', '-');

    public static void main(String[] args) throws IOException {

        System.out.println(line);
        System.out.printf("%s\n%s\n", StringUtils.center( colorize("TURING MACHINE SIMULATOR",Attribute.YELLOW_TEXT()), 62), StringUtils.center("version 0.1", 51));
        System.out.println(line);
        userInput = new Scanner(System.in);

        //Load settings
        FileReader settings = null;
        Gson gson = new Gson();
        try{
            settings = new FileReader("settings.json");
            userSettings = gson.fromJson(settings, Settings.class);
            System.out.println("Loaded previous user settings.");
        } catch (FileNotFoundException e) {
            userSettings = new Settings();
            FileWriter defaultSettings = new FileWriter("settings.json");
            String defaultSettingsJson = gson.toJson(userSettings);
            defaultSettings.write(defaultSettingsJson);
            System.out.println("Cant find user settings. Reverting to default.");
            defaultSettings.close();
        }
        if(settings !=null)settings.close();

        //Check if folder with machines exists
        File dir = new File(userSettings.getMachinesFolder());
        if(!dir.exists()){
            dir.mkdirs();
        }

        mainMenu();

    }

    public static void mainMenu(){
        System.out.println("Type "+ colorize("new", Attribute.BRIGHT_GREEN_TEXT()) +" to create a new machine.\n" +
                "Type " + colorize("load", Attribute.BRIGHT_GREEN_TEXT()) + " to load a machine from a file.\n" +
                "Type " + colorize("settings", Attribute.BRIGHT_GREEN_TEXT()) + " to view and change settings.\n" +
                "Type " + colorize("exit", Attribute.BRIGHT_GREEN_TEXT()) + " to close the app.");

        String action = userInput.nextLine();
        if(action.strip().compareToIgnoreCase("new") == 0){
            System.out.println("Lets create a new machine!");
            Machine newMachine = createNewMachine();
            machineMainMenu(newMachine);
        }
        else if(action.strip().compareToIgnoreCase("load") == 0){
            //TODO - better formatting

            System.out.println("Lets load a machine!\nSelect a machine by entering the name or the index.");
            File dir = new File(Main.userSettings.getMachinesFolder());
            String[] machinesList = dir.list((file, s) -> s.endsWith(".json")); //cool lambda here
            //Check if there are any machines
            if(machinesList.length == 0){
                System.out.println("Nie masz jeszcze zapisanych żadnych maszyn! Powrót do menu głównego.");
                mainMenu();
            }
            for (int i = 0; i < machinesList.length; i++){
                System.out.println("["+i+"] "+machinesList[i] + " opis maszyny: " + Machine.getMachineDescriptionFromJson(Main.userSettings.getMachinesFolder() + "/" + machinesList[i]));
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
                    //is string, with .json or not
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

            //Load the machine and go into menu
            Machine newMachine = Machine.fromJson(Main.userSettings.getMachinesFolder() + "/" +selMachine.strip());
            if(newMachine != null)
                machineMainMenu(newMachine);
            else mainMenu();
        } else if(action.strip().compareToIgnoreCase("exit") == 0){
            System.exit(0);
        } else if(action.strip().compareToIgnoreCase("settings") == 0){
            settingsMenu();
        }
        else {
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
            if(MT.saveMachine(Main.userSettings.getMachinesFolder() + "/" + machineName.strip()+".json") == 0)
                System.out.println("Machine saved!\n");
            else System.out.println("Unable to save a machine.\n");
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
        System.out.println("Define the alphabet - enter all allowed symbols, separated by a coma.\n");
        String alphabetString = userInput.nextLine();
        String[] alphabetChars = alphabetString.split(",");
        char[] alphabet = new char[alphabetChars.length];
        for (int i = 0; i < alphabetChars.length; i++) {
            alphabet[i] = alphabetChars[i].charAt(0);
        }

        //Add the default blank symbol if the user forgot to add one
        if(alphabetString.indexOf(Main.userSettings.getBlankSymbol()) < 0){
            char[] temp = Arrays.copyOf(alphabet, alphabet.length + 1);
            temp[alphabet.length] = Main.userSettings.getBlankSymbol();
            alphabet = temp;
        }

        DeltaFunc delta = null;
        while(delta == null) {
            System.out.println("Define the machine's moves.\nTemplate: current_state  ,  char_under_the_head  ,  new_state  ,  new_char  ,  direction\nWhen youre done, type 'end'\n");
            ArrayList<String> inputMoves = new ArrayList<>();
            String input;
            while (true) {
                input = userInput.nextLine();
                if (input.equalsIgnoreCase("end")) {
                    break;
                } else {
                    inputMoves.add(input);
                }
            }

            //TODO - maybe optimize this???
            String[] deltaString = new String[inputMoves.size()];
            for (int i = 0; i < deltaString.length; i++) {
                deltaString[i] = inputMoves.get(i);
            }

            try {
                delta = new DeltaFunc(deltaString);
            } catch (WrongMoveDefinition wrongMoveDefinition) {
                System.out.println("One of the moves is not defined correcty.\n" +
                        "The expected length is 5, but "+wrongMoveDefinition.getSize()+ " was provided.\n" +
                        "Move in question: " + wrongMoveDefinition.getMoveDefinition());
            }
        }

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

        return new Machine(null, alphabet, delta, endStatesInt, start);
    }

    public static void settingsMenu() {
        System.out.println("Your current settings are:\n" +
                "Symbols to display when running the machine: " + userSettings.getCharsToDisplay() +
                "\nDefault blank symbol: " + userSettings.getBlankSymbol() +
                "\nStored machines directory name: " + userSettings.getMachinesFolder());
        System.out.println("Type " + colorize("display ", Attribute.BRIGHT_GREEN_TEXT()) + "VALUE to set the number of symbols to display to VALUE.\n" +
                "Type blank CHAR to set the default blank character to CHAR.\n" +
                "Type directory PATH to set the machines directory to PATH.\n" +
                "Type default to restore the settings to default.\n" +
                "Type menu to exit to the menu.");
        String action = userInput.nextLine();
        String[] commandAndArgs = action.split(" ");
        boolean settingsChanged = false;
        if (commandAndArgs[0].strip().compareToIgnoreCase("display") == 0) {
            if(userSettings.setCharsToDisplay(Integer.parseInt(commandAndArgs[1]))==0){
                settingsChanged = true;
            } else {
                System.out.println("Invalid argument.");
            }
        } else if (commandAndArgs[0].strip().compareToIgnoreCase("blank") == 0) {
            userSettings.setBlankSymbol(commandAndArgs[1].charAt(0));
            settingsChanged = true;
        } else if (commandAndArgs[0].strip().compareToIgnoreCase("directory") == 0) {
            userSettings.setMachinesFolder(commandAndArgs[1]);
            settingsChanged = true;
        } else if (commandAndArgs[0].strip().compareToIgnoreCase("default") == 0) {
            userSettings = new Settings();
            settingsChanged = true;
        }
        else if (commandAndArgs[0].strip().compareToIgnoreCase("menu") == 0) {
            System.out.println("Exiting to main menu.");
            mainMenu();
        } else {
            System.out.println("Invalid command.");
        }
        if(settingsChanged){
            System.out.print("Settings changed.");
            userSettings.saveSettings();
        }
        settingsMenu();

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

