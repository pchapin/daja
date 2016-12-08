package org.pchapin.daja;


/**
 * Interface to error/warning message handling classes. A class implementing this interface can
 * be used by the rest of the compiler for communicating error information to the user.
 */
public interface Reporter {

    /**
     * Exception class for internal compiler errors. Internal errors (faults in the compiler
     * itself) are reported by throwing an instance of this class. No such faults should exist,
     * of course. This is a different situation from errors in the program presented to the
     * compiler.
     */
    static class InternalErrorException extends Exception {
        InternalErrorException(String message)
        {
            super(message);
        }
    }

    /**
     * Report a compile-time error to the user. These errors prevent code generation.
     *
     * @param line The line number where the error occurs (1 based).
     * @param column The column number where the error occurs (1 based).
     * @param message A human readable message describing the error.
     */
    void reportError(int line, int column, String message);

    /**
     * Report a compile-time warning to the user. These warnings do not prevent code generation.
     *
     * @param line The line number where the warning occurs (1 based).
     * @param column The column number where the warning occurs (1 based).
     * @param message A human readable message describing the warning.
     */
    void reportWarning(int line, int column, String message);
}
