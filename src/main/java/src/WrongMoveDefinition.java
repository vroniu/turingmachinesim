package src;

public class WrongMoveDefinition extends Exception {
    int size;
    String moveDefinition;

    public WrongMoveDefinition(int size, String moveDefinition) {
        this.size = size;
        this.moveDefinition = moveDefinition;
    }

    public int getSize() {
        return size;
    }

    public String getMoveDefinition() {
        return moveDefinition;
    }
}
