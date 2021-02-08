package src;

import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

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

        ArrayUtils.reverse(tapeDataNegative);
        String strippedTape = new String(tapeDataNegative) + new String(tapeData);

        int index;
        for (index = 0; index < strippedTape.length(); index++) {
            if (strippedTape.charAt(index) != Main.userSettings.getBlankSymbol()) {
                break;
            }
        }
        strippedTape = strippedTape.substring(index);

        for (index = strippedTape.length() - 1; index >= 0; index--) {
            if (strippedTape.charAt(index) != Main.userSettings.getBlankSymbol()) {
                break;
            }
        }

        strippedTape = strippedTape.substring(0, index + 1);

        tapeLen = strippedTape.length();
        tapeData = strippedTape.toCharArray();
        negativeTapeLen = 0;
        tapeDataNegative = new char[negativeTapeLen];

    }
}
