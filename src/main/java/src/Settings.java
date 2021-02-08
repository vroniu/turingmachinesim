package src;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;

public class Settings {
    private int charsToDisplay;
    private char blankSymbol;
    private String machinesFolder;

    //Default settings
    public Settings(){
        charsToDisplay = 10;
        blankSymbol = 'B';
        machinesFolder = "machines";
    }

    //It returns the value divided by two.
    public int getCharsToDisplay(){
        return charsToDisplay / 2;
    }

    public char getBlankSymbol(){
        return blankSymbol;
    }

    public String getMachinesFolder() {
        return machinesFolder;
    }

    public void setCharsToDisplay(int charsToDisplay) throws NumberFormatException{
        if (charsToDisplay > 0) {
            this.charsToDisplay = charsToDisplay;
        } else throw new NumberFormatException();
    }

    public void setBlankSymbol(char blankSymbol) {
        this.blankSymbol = blankSymbol;
    }

    public void setMachinesFolder(String machinesFolder) {
        this.machinesFolder = machinesFolder;
    }

    public void saveSettings(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);

        FileWriter savedMachine;
        try {
            savedMachine = new FileWriter("settings.json");
            savedMachine.write(json);
            savedMachine.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
   }
}
