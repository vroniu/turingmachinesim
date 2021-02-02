package src;

import java.util.Arrays;

public class Tape {

    private int tapeLen, negativeTapeLen;
    private char[] tapeData, tapeDataNegative;

    public Tape(String data){
        tapeLen = data.length();
        tapeData = data.toCharArray();
        //Initialize the first element in the "negative" (infinite - left tape) array
        negativeTapeLen = 0;
        tapeDataNegative = new char[negativeTapeLen];
    }

    public int getTapeLen() {
        return tapeLen;
    }

    public int getNegativeTapeLen() {
        return negativeTapeLen;
    }

    public void setSymbol(int index, char symbol) {
        if (index >= 0) tapeData[index] = symbol;
        else tapeDataNegative[(index * -1) - 1] = symbol;
    }


    public char getSymbol(int index){
        if(index>=tapeLen){
            //Increase the tape length
            char[] newTapeData = Arrays.copyOf(tapeData, index + 1);
            while(tapeLen <= index)
                newTapeData[tapeLen++] = Main.userSettings.getBlankSymbol();
            tapeData = newTapeData;
        } else if(index < 0){
            //subtract 1 here so it writes/reads the array correctly
            index = (index * -1) - 1;
            if(index >= negativeTapeLen) {
                //Increase the tape length
                char[] newTapeData = Arrays.copyOf(tapeDataNegative, index + 1);
                while(negativeTapeLen <= index)
                    newTapeData[negativeTapeLen++] = Main.userSettings.getBlankSymbol();
                tapeDataNegative = newTapeData;
            }
            return tapeDataNegative[index];
        }
        return tapeData[index];
    }

    //Strip the tape of trailing and leading blanks
    public void strip(){
        //Count the chars to strip on the infinite-left tape
        int i = -negativeTapeLen;
        int charsToStrip = 0;
        while(i < 0){
            if(this.getSymbol(i) == Main.userSettings.getBlankSymbol()){
                i++; charsToStrip++;
            } else break;
        }
        //Shrink the array
        char[] newTapeData = Arrays.copyOf(tapeDataNegative, negativeTapeLen - charsToStrip);
        tapeDataNegative = newTapeData;
        negativeTapeLen -= charsToStrip;

        //Same but for the infinite-right side
        charsToStrip = 0;
        i = tapeLen - 1;
        while(i >= 0){
            if(this.getSymbol(i) == Main.userSettings.getBlankSymbol()){
                i--; charsToStrip++;
            } else break;
        }
        newTapeData = Arrays.copyOf(tapeData, tapeLen - charsToStrip);
        tapeData = newTapeData;
        tapeLen -= charsToStrip;
    }
}
