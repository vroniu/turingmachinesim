
package src;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class Machine {

    final static int symbolsToDisplay = 10;

    private Tape tape;

    private char[] alphabet;
    private DeltaFunc delta;

    private int startingState;
    private int currState;
    private int currHeadPos;

    private int[] endStates;

    private String name, description;

    public Machine(Tape tape, char[] alphabet, DeltaFunc delta, int[] endStates, int getStartingState) {
        this.tape = tape;
        this.alphabet = alphabet;
        this.delta = delta;

        this.startingState = getStartingState;
        this.endStates = endStates;
    }

    public static String getMachineDescriptionFromJson(String path){
        Machine machine = Machine.fromJson(path);
        if(machine == null){
            return "Opis niedostępny.";
        } else return machine.description;
    }

    public static Machine fromJson(String path){
        //TODO - ogarnac wyjatki
        FileReader readFrom;
        try {
            Gson gson = new Gson();
            readFrom = new FileReader(path);
            return gson.fromJson(readFrom, Machine.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setTape(Tape tape) {
        this.tape = tape;
    }

    public void setName(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void run(){
        //Reset the machine
        this.currState=this.startingState;
        this.currHeadPos=0;

        boolean running = true;
        while(running){
            try {
                System.out.printf("%-10s|%-10s| ", "State: "+ this.currState, "Head: " + this.currHeadPos);
                displayCurrentTape(true);
                System.out.print("\n");

                //Check if the symbol under the head is in the alphabet
                char symbolAtHeadPos = tape.getSymbol(currHeadPos);
                boolean valid = false;
                for(char c:alphabet){
                    if (c==symbolAtHeadPos){
                        valid=true;
                        break;
                    }
                }
                if(!valid)throw new IllegalArgumentException("Symbol isnt in the alphabet!");

                //Find a matching move
                Move matchingMove = delta.getMatchingMove(currState, symbolAtHeadPos);
                currState = matchingMove.getNewState();
                tape.setSymbol(currHeadPos, matchingMove.getNewChar());
                currHeadPos += matchingMove.getNewDir();

                //Check if machine is in final state
                for (int state : endStates) {
                    if (state == currState) {
                        running = false;
                    }
                }

            } catch (NullPointerException n){
                //TODO - customowe exception
//                n.printStackTrace();
                System.out.println("No defined move!");
                return;
            } catch (IllegalArgumentException i){
                System.out.println(i.getMessage());
                return;
            }

        }
        System.out.println("DONE!");
        System.out.printf("%-10s|%-10s| ", "State: "+ this.currState, "Head: " + this.currHeadPos);
        displayCurrentTape(true);
        this.tape.strip();
        System.out.println("\nTape after the operation:");
        displayAllTape(true);
        System.out.println(" ");
    }

    public void displayCurrentTape(boolean showHead) {
        for (int i = currHeadPos - Main.userSettings.getCharsToDisplay(); i <= currHeadPos + Main.userSettings.getCharsToDisplay(); i++) {
            if (i == currHeadPos && showHead) {
                System.out.print("[" + tape.getSymbol(i) + "]");
            } else System.out.print(tape.getSymbol(i));
        }
    }

    public void displayAllTape(boolean showHead){
        for (int i = -tape.getNegativeTapeLen(); i < tape.getTapeLen(); i++) {
            System.out.print(tape.getSymbol(i));
        }
    }

    //TODO - zrobic tak zeby to dzialalo :DDD
    public void displayConfig(int configurationNumber){
        System.out.print("  K:"+(configurationNumber+1)+"   ");
        if (currHeadPos < 0){
            System.out.print("q"+currState+" "+tape.getSymbol(currHeadPos));
        }
        for(int i =0; i<tape.getTapeLen(); i++){
            if(i==currHeadPos ){
                //Dont print the whitespace for the first symbol
                if(i==0)System.out.print("q"+currState+" "+tape.getSymbol(i));
                else System.out.print(" q"+currState+" "+tape.getSymbol(i));
            }
            else System.out.print(tape.getSymbol(i));
        }
        //If the head is in the "infinite blanks" after the tape
        if (currHeadPos >= tape.getTapeLen()){
            System.out.print(" q"+currState+" "+tape.getSymbol(currHeadPos));
        }
    }

    public int saveMachine(String path){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);

        FileWriter savedMachine;
        try {
            savedMachine = new FileWriter(path);
            savedMachine.write(json);
            savedMachine.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
