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

    private static boolean getUserInputYesNo() {
        System.out.print(colorize(">> ", Attribute.BRIGHT_GREEN_TEXT()));
        return userInput.nextLine().trim().equalsIgnoreCase("Y");
    }

    private static String getUserInput(){
        System.out.print(colorize(">> ", Attribute.BRIGHT_GREEN_TEXT()));
        return userInput.nextLine();
    }

    private static void printMenu(String[] commands, String[] descriptions) {
        for (int i = 0; i < commands.length; i++) {
            System.out.println("Type " + colorize(commands[i], Attribute.BRIGHT_GREEN_TEXT()) + " to " + descriptions[i] + ".");
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println(line);
        System.out.printf("%s\n%s\n", StringUtils.center(colorize("TURING MACHINE SIMULATOR", Attribute.YELLOW_TEXT()), 62), StringUtils.center("version 0.1", 51));
        System.out.println(line);
        userInput = new Scanner(System.in);

        //Load settings
        FileReader settings = null;
        Gson gson = new Gson();
        try {
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
        if (settings != null) settings.close();

        //Check if folder with machines exists
        File dir = new File(userSettings.getMachinesFolder());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mainMenu();
    }

    public static void mainMenu() {
        printMenu(new String[]{"new", "load", "settings", "exit"},
                new String[]{"create a new machine", "load a machine from a file", "view and change settings", "close the app"});

        String action = getUserInput();
        if (action.trim().compareToIgnoreCase("new") == 0) {
            System.out.println("Lets create a new machine!");
            Machine newMachine = createNewMachine();
            machineMainMenu(newMachine);
        } else if (action.trim().compareToIgnoreCase("load") == 0) {

            System.out.println("Lets load a machine!");
            File dir = new File(Main.userSettings.getMachinesFolder());
            String[] machinesList = dir.list((file, s) -> s.endsWith(".json")); //cool lambda here
            String[][] tableData = new String[machinesList.length][3];
            for (int i = 0; i < machinesList.length; i++) {
                tableData[i] = new String[]{String.valueOf(i), machinesList[i], Machine.getMachineDescriptionFromJson(Main.userSettings.getMachinesFolder() + "/" + machinesList[i])};
            }
            //Check if there are any machines
            if (machinesList.length == 0) {
                System.out.println(colorize("You don't have any saved machines!", Attribute.BRIGHT_RED_TEXT()) + "\nReturning to main menu.");
                mainMenu();
            }

            TextTable machines = new TextTable(new String[]{"Index", "Name", "Description"}, tableData);
            machines.printTable();
            System.out.println("Select a machine by entering the name or the index.");

            //parse by index or by name
            String selMachine = "";
            boolean validMachineSelected = false;
            while (!validMachineSelected) {
                action = getUserInput();
                if (action.matches("-?\\d+")) {
                    //is integer
                    if (Integer.parseInt(action) < machinesList.length && Integer.parseInt(action) > -1) {
                        selMachine = machinesList[Integer.parseInt(action)];
                        validMachineSelected = true;
                    }
                } else {
                    //is string, with .json or not
                    if (Arrays.asList(machinesList).contains(action)) {
                        selMachine = action;
                        validMachineSelected = true;
                    } else if (Arrays.asList(machinesList).contains(action + ".json")) {
                        selMachine = action + ".json";
                        validMachineSelected = true;
                    }
                }
                if (!validMachineSelected) {
                    System.out.println(colorize("Sorry, this machine doesn't seem to exist!", Attribute.BRIGHT_RED_TEXT()) + "\nRetry selecting the machine.");
                }
            }

            //Load the machine and go into menu
            Machine newMachine = Machine.fromJson(Main.userSettings.getMachinesFolder() + "/" + selMachine.trim());
            if (newMachine != null) {
                System.out.println("Loaded machine " + colorize(newMachine.getName(), Attribute.BRIGHT_GREEN_TEXT()));
                machineMainMenu(newMachine);
            } else mainMenu();
        } else if (action.trim().compareToIgnoreCase("exit") == 0) {
            System.exit(0);
        } else if (action.trim().compareToIgnoreCase("settings") == 0) {
            settingsMenu();
        } else {
            System.out.println(colorize("Invalid command.", Attribute.BRIGHT_RED_TEXT()));
            mainMenu();
        }
    }

    public static void machineMainMenu(Machine MT) {
        printMenu(new String[]{"run", "save", "menu"},
                new String[]{"enter a tape and run it on the machine", "save the machine", "exit to main menu"});

        String action = getUserInput();
        if (action.trim().compareToIgnoreCase("run") == 0) {
            boolean running = true;
        while (running) {

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
            if (MT.saveMachine(Main.userSettings.getMachinesFolder() + "/" + machineName.trim() + ".json") == 0)
                System.out.println("Machine saved!");
            else System.out.println(colorize("Unable to save the machine.", Attribute.BRIGHT_RED_TEXT()));
            machineMainMenu(MT);

        } else if (action.trim().compareToIgnoreCase("menu") == 0) {
            System.out.println("Exiting to main menu.");
            mainMenu();
        } else {
            System.out.println(colorize("Invalid command.", Attribute.BRIGHT_RED_TEXT()));
            machineMainMenu(MT);
        }
    }

    public static Machine createNewMachine() {
        System.out.println("Define the alphabet - enter all allowed symbols, separated by a coma.");
        String alphabetString = getUserInput();
        String[] alphabetChars = alphabetString.split(",");
        char[] alphabet = new char[alphabetChars.length];
        for (int i = 0; i < alphabetChars.length; i++) {
            alphabet[i] = alphabetChars[i].charAt(0);
        }

        //Add the default blank symbol if the user forgot to add one
        if (alphabetString.indexOf(Main.userSettings.getBlankSymbol()) < 0) {
            char[] temp = Arrays.copyOf(alphabet, alphabet.length + 1);
            temp[alphabet.length] = Main.userSettings.getBlankSymbol();
            alphabet = temp;
        }

        DeltaFunc delta = null;
        while (delta == null) {
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

            try {
                delta = new DeltaFunc(inputMoves);
            } catch (WrongMoveDefinition wrongMoveDefinition) {
                System.out.println(colorize("One of the moves is not defined correcty.\n", Attribute.BRIGHT_RED_TEXT()) +
                        "The expected length is 5, but " + wrongMoveDefinition.getSize() + " was provided.\n" +
                        "Move in question: " + wrongMoveDefinition.getMoveDefinition());
            } catch (IllegalArgumentException illegalMoveException) {
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
        System.out.println("Your current settings are:");

        System.out.printf("%-50s%s\n%-50s%s\n%-50s%s\n%s\n", "Symbols to display when running the machine", userSettings.getCharsToDisplay() * 2,
                "Default blank symbol", userSettings.getBlankSymbol(),
                "Stored machines directory name", userSettings.getMachinesFolder(), line);

        printMenu(new String[]{"display VALUE", "blank CHAR", "directory PATH", "default", "menu"},
                new String[]{"set the number of symbols to display to VALUE", "set the default blank character to CHAR", "set the machines directory to PATH", "restore the settings to default", "exit to main menu"});

        String[] commandAndArgs = getUserInput().split(" ");
        boolean settingsChanged = false;
        if (commandAndArgs[0].trim().compareToIgnoreCase("display") == 0) {
            try {
                userSettings.setCharsToDisplay(Integer.parseInt(commandAndArgs[1]));
                settingsChanged = true;
            } catch (NumberFormatException e) {
                System.out.println(colorize("Invalid argument.", Attribute.BRIGHT_RED_TEXT()));
            }

        } else if (commandAndArgs[0].trim().compareToIgnoreCase("blank") == 0) {
            userSettings.setBlankSymbol(commandAndArgs[1].charAt(0));
            settingsChanged = true;
        } else if (commandAndArgs[0].trim().compareToIgnoreCase("directory") == 0) {
            File newDir = new File(commandAndArgs[1]);
            if (newDir.mkdirs()) {
                System.out.println("Folder created! Do you wish to move all the machines to the new folder? [Y/n]");
                if (getUserInputYesNo()) {
                    try {
                        File dir = new File(userSettings.getMachinesFolder());
                        FileUtils.copyDirectory(dir, newDir);
                    } catch (IOException e) {
                        System.out.println(colorize("Unable to copy files.", Attribute.BRIGHT_RED_TEXT()));
                    }
                }
                userSettings.setMachinesFolder(commandAndArgs[1]);
                settingsChanged = true;
            } else {
                System.out.println(colorize("Unable to create a folder with that name.", Attribute.BRIGHT_RED_TEXT()));
            }
        } else if (commandAndArgs[0].trim().compareToIgnoreCase("default") == 0) {
            if (userSettings.getMachinesFolder() != new Settings().getMachinesFolder()) {
                File newDir = new File(new Settings().getMachinesFolder());
                if (newDir.mkdirs()) {
                    System.out.println("Do you wish to move all the machines to the new folder? [Y/n]");
                    if (getUserInputYesNo()) {
                        try {
                            File dir = new File(userSettings.getMachinesFolder());
                            FileUtils.copyDirectory(dir, newDir);
                        } catch (IOException e) {
                            System.out.println(colorize("Unable to copy files.", Attribute.BRIGHT_RED_TEXT()));
                        }
                    }
                }
            }
            userSettings = new Settings();
            settingsChanged = true;
        } else if (commandAndArgs[0].trim().compareToIgnoreCase("menu") == 0) {
            System.out.println("Exiting to main menu.");
            mainMenu();
        } else {
            System.out.println(colorize("Invalid command.", Attribute.BRIGHT_RED_TEXT()));
        }
        if (settingsChanged) {
            System.out.print("Settings changed.");
            userSettings.saveSettings();
        }
        settingsMenu();
    }

}

