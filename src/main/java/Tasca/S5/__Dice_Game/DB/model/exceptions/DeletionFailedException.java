package Tasca.S5.__Dice_Game.DB.model.exceptions;

public class DeletionFailedException extends RuntimeException {
    public DeletionFailedException(String message) {
        super(message);
    }
}
