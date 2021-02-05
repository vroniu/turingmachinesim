package src;

import com.diogonunes.jcolor.Attribute;
import com.google.gson.Gson;
import dnl.utils.text.table.TextTable;
import org.apache.commons.io.FileUtils;
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

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static boolean getUserInputYesNo(){
        System.out.print(colorize(">> ", Attribute.BRIGHT_GREEN_TEXT()));
        return userInput.nextLine().trim().equalsIgnoreCase("Y");
    }

    private static String getUserInput(){
        System.out.print(colorize(">> ", Attribute.BRIGHT_GREEN_TEXT()));
        return userInput.nextLine();
    }

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

        String action = getUserInput();
        if(action.trim().compareToIgnoreCase("new") == 0){
            System.out.println("Lets create a new machine!");
            Machine newMachine = createNewMachine();
            machineMainMenu(newMachine);
        }
        else if(action.trim().compareToIgnoreCase("load") == 0){
            //TODO - better formatting

            System.out.println("Lets load a machine!");
            File dir = new File(Main.userSettings.getMachinesFolder());
            String[] machinesList = dir.list((file, s) -> s.endsWith(".json")); //cool lambda here
            String[][] tableData = new String[machinesList.length][3];
            for (int i = 0; i < machinesList.length; i++){
                tableData[i] = new String[]{String.valueOf(i), machinesList[i], Machine.getMachineDescriptionFromJson(Main.userSettings.getMachinesFolder() + "/" + machinesList[i])};
            }
            //Check if there are any machines
            if(machinesList.length == 0){
                System.out.println("Nie masz jeszcze zapisanych żadnych maszyn! Powrót do menu głównego.");
                mainMenu();
            }

            TextTable machines = new TextTable(new String[]{"Index", "Name", "Description"}, tableData);
            machines.printTable();
            System.out.println("Select a machine by entering the name or the index.");

            //parse by index or by name
            String selMachine = "";
            boolean validMachineSelected = false;
            while(!validMachineSelected) {
                action = getUserInput();
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
            Machine newMachine = Machine.fromJson(Main.userSettings.getMachinesFolder() + "/" +selMachine.trim());
            if(newMachine != null)
                machineMainMenu(newMachine);
            else mainMenu();
        } else if(action.trim().compareToIgnoreCase("exit") == 0){
            System.exit(0);
        } else if(action.trim().compareToIgnoreCase("settings") == 0){
            settingsMenu();
        }
        else {
            System.out.println("Invalid command.");
            mainMenu();
        }
    }

    public static void machineMainMenu(Machine MT){
        System.out.println("Type " + colorize("run", Attribute.BRIGHT_GREEN_TEXT()) + " to enter a tape and run it on the machine.\n" +
                "Type " + colorize("save", Attribute.BRIGHT_GREEN_TEXT()) + " to save the machine.\n" +
                "Type " + colorize("menu", Attribute.BRIGHT_GREEN_TEXT()) + " to exit to main menu.");
        String action = getUserInput();
        if (action.trim().compareToIgnoreCase("run") == 0) {
            boolean running = true;
            while(running){
                System.out.println("Enter the tape.");
                Tape tape = new Tape(getUserInput());
                MT.setTape(tape);
                MT.run();
                System.out.println("Would you like to enter another tape? [Y/n]");
                if (!getUserInputYesNo()) {
                    running = false;
                    machineMainMenu(MT);
                }
            }

        } else if (action.trim().compareToIgnoreCase("save") == 0) {
            System.out.println("Enter the name of the machine.");
            String machineName = getUserInput();
            System.out.println("You can also create a short description for the machine.\nIf you want to skip this step, just dont enter anything an press Enter.");
            String machineDescription = getUserInput();
            MT.setName(machineName, machineDescription);
            if(MT.saveMachine(Main.userSettings.getMachinesFolder() + "/" + machineName.trim()+".json") == 0)
                System.out.println("Machine saved!");
            else System.out.println("Unable to save a machine.");
            machineMainMenu(MT);

        } else if (action.trim().compareToIgnoreCase("menu") == 0) {
            System.out.println("Exiting to main menu.");
            mainMenu();
        } else {
            System.out.println("Invalid command.");
            machineMainMenu(MT);
        }
    }

    public static Machine createNewMachine(){
        System.out.println("Define the alphabet - enter all allowed symbols, separated by a coma.");
        String alphabetString = getUserInput();
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
            System.out.println("Define the machine's moves.\nTemplate: current_state  ,  char_under_the_head  ,  new_state  ,  new_char  ,  direction\nWhen youre done, type " + colorize("end", Attribute.BRIGHT_GREEN_TEXT()) + ".");
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
            } catch (IllegalArgumentException illegalMoveException){
                System.out.println(illegalMoveException.getMessage());
            }
        }

        System.out.println("Define the end states - enter their indexes, separated by a coma.");
        String endStates = getUserInput();
        String[] endStatesArray = endStates.split(",");
        int[] endStatesInt = new int[endStatesArray.length];
        for (int i = 0; i < endStatesArray.length; i++) {
            endStatesInt[i] = Integer.parseInt(endStatesArray[i]);
        }

        System.out.println("Enter the starting state.");
        int start = Integer.parseInt(getUserInput());

        System.out.println("The machine is configured!");

        return new Machine(null, alphabet, delta, endStatesInt, start);
    }

    public static void settingsMenu() {
        System.out.println("Your current settings are:\n" +
                "Symbols to display when running the machine: " + userSettings.getCharsToDisplay() +
                "\nDefault blank symbol: " + userSettings.getBlankSymbol() +
                "\nStored machines directory name: " + userSettings.getMachinesFolder());
        System.out.println("Type " + colorize("display ", Attribute.BRIGHT_GREEN_TEXT()) + colorize("VALUE", Attribute.BRIGHT_YELLOW_TEXT()) + " to set the number of symbols to display to VALUE.\n" +
                "Type " + colorize("blank ", Attribute.BRIGHT_GREEN_TEXT()) + colorize("CHAR", Attribute.BRIGHT_YELLOW_TEXT()) + " to set the default blank character to CHAR.\n" +
                "Type " + colorize("directory ", Attribute.BRIGHT_GREEN_TEXT()) + colorize("PATH", Attribute.BRIGHT_YELLOW_TEXT()) + " to set the machines directory to PATH.\n" +
                "Type " + colorize("default", Attribute.BRIGHT_GREEN_TEXT()) + " to restore the settings to default.\n" +
                "Type " + colorize("menu", Attribute.BRIGHT_GREEN_TEXT()) + " to exit to the menu.");
        String[] commandAndArgs = getUserInput().split(" ");
        boolean settingsChanged = false;
        if (commandAndArgs[0].trim().compareToIgnoreCase("display") == 0) {
            if(userSettings.setCharsToDisplay(Integer.parseInt(commandAndArgs[1]))==0){
                settingsChanged = true;
            } else {
                System.out.println("Invalid argument.");
            }
        } else if (commandAndArgs[0].trim().compareToIgnoreCase("blank") == 0) {
            userSettings.setBlankSymbol(commandAndArgs[1].charAt(0));
            settingsChanged = true;
        } else if (commandAndArgs[0].trim().compareToIgnoreCase("directory") == 0) {
            File newDir = new File(commandAndArgs[1]);
            if(newDir.mkdirs()){
                System.out.println("Folder created! Do you wish to move all the machines to the new folder? [Y/n]");
                if(getUserInputYesNo()) {
                    try {
                        File dir = new File(userSettings.getMachinesFolder());
                        FileUtils.copyDirectory(dir, newDir);
                    } catch (IOException e){
                        System.out.println("Unable to copy files.");
                    }
                }
                userSettings.setMachinesFolder(commandAndArgs[1]);
                settingsChanged = true;
            } else {
                System.out.println("Unable to create a folder with that name.");
            }
        } else if (commandAndArgs[0].trim().compareToIgnoreCase("default") == 0) {
            userSettings = new Settings();
            settingsChanged = true;
        }
        else if (commandAndArgs[0].trim().compareToIgnoreCase("menu") == 0) {
            System.out.println("Exiting to main menu.");
            mainMenu();
        } else {
            System.out.println("Invalid command.");
        }
        if(settingsChanged){
            System.out.print("Settings changed. ");
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

