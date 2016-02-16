package edu.vtc.daja;

public class Literals {

    private enum StateType {
        START,
        LEADING_ZERO,
        GET_DIGITS,
        GET_SUFFIX,
        AT_END
    }

    private enum BaseType {
        DECIMAL,
        BINARY,
        HEX
    }

    public static class InvalidLiteralException extends Exception {
        public InvalidLiteralException(String message) {
            super(message);
        }
    }

    /**
     * Uses a finite state machine to find the integer value of an integer literal. Note that
     * the lexical analyzer has already verified the format so certain simplifying assumptions
     * can potentially be made in the implementation of this method.
     *
     * This program implements the following finite state machine:
     *
     * <pre>
     * START -- '0' --> LEADING_ZERO
     * START -- non-zero digit --> GET_DIGITS (update value)
     *
     * LEADING_ZERO -- 'b' or 'B' --> GET_DIGITS (set binary flag)
     * LEADING_ZERO -- 'x' or 'X' --> GET_DIGITS (set hex flag)
     *
     * GET_DIGITS -- digit or '_' --> GET_DIGITS (update value)
     * GET_DIGITS -- 'L' or 'u' or 'U' --> GET_SUFFIX
     *
     * GET_SUFFIX -- 'L' or 'u' or 'U' --> AT_END
     * </pre>
     *
     * @param text The text of the literal (e.g., "1_234U")
     * @return The converted value (e.g., 1234)
     */
    public static int convertIntegerLiteral(String text) throws InvalidLiteralException
    {
        StateType state = StateType.START;
        BaseType  base = BaseType.DECIMAL;
        int  value = 0;
        char ch;

        for (int i = 0; i < text.length(); ++i) {
            ch = Character.toUpperCase(text.charAt(i));
            switch (state) {
                case START:
                    if (ch == '0') {
                        state = StateType.LEADING_ZERO;
                    }
                    else if (ch >= '1' && ch <= '9') {
                        value = 10 * value + (ch - '0');
                        state = StateType.GET_DIGITS;
                    }
                    else {
                        throw new InvalidLiteralException("Invalid start of literal.");
                    }
                    break;

                case LEADING_ZERO:
                    switch (ch) {
                        case 'b':
                        case 'B':
                            base = BaseType.BINARY;
                            state = StateType.GET_DIGITS;
                            break;

                        case 'x':
                        case 'X':
                            base = BaseType.HEX;
                            state = StateType.GET_DIGITS;
                            break;

                        default:
                            throw new InvalidLiteralException("Invalid number prefix.");
                    }
                    break;

                case GET_DIGITS:
                    if (ch == 'L' || ch == 'U') {
                        state = StateType.GET_SUFFIX;
                    }
                    else {
                        switch (base) {
                            case DECIMAL:
                                if ((ch >= '0' && ch <= '9') || ch == '_') {
                                    if (ch != '_') {
                                        value = 10 * value + (ch - '0');
                                    }
                                }
                                else {
                                    throw new InvalidLiteralException("Invalid decimal digit.");
                                }
                                break;

                            case BINARY:
                                if ((ch >= '0' && ch <= '1') || ch == '_') {
                                    if (ch != '_') {
                                        value = 2 * value + (ch - '0');
                                    }
                                }
                                else {
                                    throw new InvalidLiteralException("Invalid binary digit.");
                                }
                                break;

                            case HEX:
                                if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || ch == '_') {
                                    if (ch != '_') {
                                        final String hexConversion = "0123456789ABCDEF";
                                        int digitValue = hexConversion.indexOf(ch);
                                        value = 16 * value + digitValue;
                                    }
                                }
                                else {
                                    throw new InvalidLiteralException("Invalid hex digit.");
                                }
                                break;
                        }
                    }
                    break;

                case GET_SUFFIX:
                    if (ch == 'L' || ch == 'U') {
                        state = StateType.AT_END;
                    }
                    else {
                        throw new InvalidLiteralException("Invalid suffix character.");
                    }
                    break;

                case AT_END:
                    throw new InvalidLiteralException("Extraneous characters after literal");
            }
        }

        // Now check the state at the end looking for errors.
        switch (state) {
            case START:
                throw new InvalidLiteralException("Error: Empty literal");

            case LEADING_ZERO:
                // No error. A single leading zero is allowed (and has the valuel zero).
                break;

            case GET_DIGITS:
                // No error. This means there was no suffix.
                break;

            case GET_SUFFIX:
                // No error. This means there was only one suffix character.
                break;

            case AT_END:
                // No error. This means there were two suffix characters.
                break;
        }

        return value;
    }

}
