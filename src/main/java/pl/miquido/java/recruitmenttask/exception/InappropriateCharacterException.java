package pl.miquido.java.recruitmenttask.exception;

public class InappropriateCharacterException extends RuntimeException {

    /**
     * Constructs an {@code InappropriateCharacterException} with the
     * specified reason.
     *
     * @param reason the reason behind a character being inappropriate.
     */
    public InappropriateCharacterException(String reason) {
        super("This character is inappropriate because: " + reason);
    }
}
