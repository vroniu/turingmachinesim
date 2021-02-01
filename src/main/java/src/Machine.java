
package src;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;

public class Machine {

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
        FileReader readFrom = null;
        try {
            Gson gson = new Gson();
            readFrom = new FileReader(path);
            Machine MT = gson.fromJson(readFrom, Machine.class);
            return MT;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public void run(){
        this.currState=this.startingState;
        this.currHeadPos=0;

        boolean running = true;
        int configurationNumber = 0;
        while(running){
            try {
                displayTape(true);
                System.out.print(" ");
                displayConfig(configurationNumber++);
                System.out.print("\n");

                char symbolAtHeadPos = tape.getSymbol(currHeadPos);
                boolean valid = false;
                for(char c:alphabet){
                    if (c==symbolAtHeadPos){
                        valid=true;
                        break;
                    }
                }
                if(!valid)throw new IllegalArgumentException("Symbol isnt in the alphabet!");
                Move matchingMove = delta.getMatchingMove(currState, symbolAtHeadPos);
                //matchingMove.print();
                currState = matchingMove.getNewState();
                tape.setSymbol(currHeadPos, matchingMove.getNewChar());
                currHeadPos += matchingMove.getNewDir();
                for (int state :endStates) {
                    if (state == currState) {
                        running = false;
                    }
                }

            } catch (NullPointerException n){
                System.out.println("No defined move!");
                return;
            } catch (IllegalArgumentException i){
                System.out.println(i.getMessage());
                return;
            }

        }
        System.out.println("DONE!");
//        for(int i =0; i<tape.getTapeLen(); i++){
//            System.out.print(tape.getSymbol(i));
//        }
        displayTape(true);
        displayConfig(configurationNumber);
        System.out.println(" ");
    }

    public void displayTape(boolean showHead){
        //If the head is in the "infinite blanks" before the tape
        if (currHeadPos < 0 && showHead){
            System.out.print("["+tape.getSymbol(currHeadPos)+"]");
        }
        for(int i =0; i<tape.getTapeLen(); i++){
            if(i==currHeadPos && showHead){
                System.out.print("["+tape.getSymbol(i)+"]");
            }
            else System.out.print(tape.getSymbol(i));
        }
        //If the head is in the "infinite blanks" after the tape
        if (currHeadPos >= tape.getTapeLen() && showHead){
            System.out.print("["+tape.getSymbol(currHeadPos)+"]");
        }
    }

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

    public void saveMachine(String path){
//        JSONObject machine = new JSONObject();
//
//        JSONArray jAlphabet = new JSONArray();
//        for (char c : this.alphabet) {
//            jAlphabet.add(c);
//        }
//        machine.put("alphabet", jAlphabet);
//
//        JSONArray jFinalStates = new JSONArray();
//        for (int i : this.endStates) {
//            jFinalStates.add(i);
//        }
//        machine.put("starting state", this.currState);
//        machine.put("final states", jFinalStates);
//        machine.put("delta", this.delta.saveDeltaToJson());
//        machine.put("name", this.name);
//        machine.put("description", this.description);
//        //System.out.println(machine.toString());
//
//        FileWriter savedMachine = null;
//        try {
//            savedMachine = new FileWriter(path);
//            savedMachine.write(machine.toString());
//            savedMachine.close();
//        } catch (IOException e) {
//            e.printStackTrace();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);

        FileWriter savedMachine = null;
        try {
            savedMachine = new FileWriter(path);
            savedMachine.write(json);
            savedMachine.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
