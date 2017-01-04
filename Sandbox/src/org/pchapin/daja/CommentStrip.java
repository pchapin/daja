package org.pchapin.daja;

import java.io.FileReader;
import java.io.IOException;

/**
 * The main class of the CommentStrip utility.
 *
 * This program implements the following finite state machine:
 *
 * <pre>
 * NORMAL -- '/' --> MAYBE_COMMENT
 * NORMAL -- '"' --> DOUBLE_QUOTE (print character)
 * NORMAL -- '\'' --> SINGLE_QUOTE (print character)
 * NORMAL -- others --> NORMAL (print character)
 *
 * MAYBE_COMMENT -- '/' --> SLASH_SLASH_COMMENT
 * MAYBE_COMMENT -- '*' --> BLOCK_COMMENT (print a space)
 * MAYBE_COMMENT -- '"' --> DOUBLE_QUOTE (print slash; print character)
 * MAYBE_COMMENT -- '\'' --> SINGLE_QUOTE (print slash; print character)
 * MAYBE_COMMENT -- others --> NORMAL (print slash; print character)
 *
 * SLASH_SLASH_COMMENT -- '\n'--> NORMAL (print '\n')
 * SLASH_SLASH_COMMENT -- others --> SLASH_SLASH_COMMENT
 *
 * BLOCK_COMMENT -- '*' --> MAYBE_UNCOMMENT
 * BLOCK_COMMENT -- '\n' --> BLOCK_COMMENT (print character)
 * BLOCK_COMMENT -- others --> BLOCK_COMMENT
 *
 * MAYBE_UNCOMMENT -- '/' --> NORMAL
 * MAYBE_UNCOMMENT -- '*' --> MAYBE_UNCOMMENT
 * MAYBE_UNCOMMENT -- others --> BLOCK_COMMENT
 *
 * DOUBLE_QUOTE -- '\\' --> ESCAPE_ONE_DOUBLE (print character)
 * DOUBLE_QUOTE -- '"' --> NORMAL (print character)
 * DOUBLE_QUOTE -- others --> DOUBLE_QUOTE (print character)
 *
 * SINGLE_QUOTE -- '\\' --> ESCAPE_ONE_SINGLE (print character)
 * SINGLE_QUOTE -- '\'' --> NORMAL (print character)
 * SINGLE_QUOTE -- others --> SINGLE_QUOTE (print character)
 *
 * ESCAPE_ONE_DOUBLE -- others --> DOUBLE_QUOTE (print character)
 * ESCAPE_ONE_SINGLE -- others --> SINGLE_QUOTE (print character)
 * </pre>
 */
public class CommentStrip {

    private enum StateType {
        NORMAL,
        MAYBE_COMMENT,
        MAYBE_UNCOMMENT,
        SLASH_SLASH_COMMENT,
        BLOCK_COMMENT,
        DOUBLE_QUOTE,
        SINGLE_QUOTE,
        ESCAPE_ONE_DOUBLE,
        ESCAPE_ONE_SINGLE
    }


    private static void print(int ch)
    {
        System.out.print((char)ch);
    }


    public static void main(String[] args) throws IOException
    {
        StateType state = StateType.NORMAL;
        FileReader input = new FileReader(args[0]);
        int ch;

        while ((ch = input.read()) != -1) {
            switch (state) {
                case NORMAL:
                    switch (ch) {
                        case '/' :            state = StateType.MAYBE_COMMENT; break;
                        case '"' : print(ch); state = StateType.DOUBLE_QUOTE;  break;
                        case '\'': print(ch); state = StateType.SINGLE_QUOTE;  break;
                        default  : print(ch); break;
                    }
                    break;

                case MAYBE_COMMENT:
                    switch (ch) {
                        case '/' :                        state = StateType.SLASH_SLASH_COMMENT; break;
                        case '*' : print(' ');            state = StateType.BLOCK_COMMENT;       break;
                        case '"' : print('/'); print(ch); state = StateType.DOUBLE_QUOTE;        break;
                        case '\'': print('/'); print(ch); state = StateType.SINGLE_QUOTE;        break;
                        default  : print('/'); print(ch); state = StateType.NORMAL;              break;
                    }
                    break;

                case MAYBE_UNCOMMENT:
                    switch (ch) {
                        case '/': state = StateType.NORMAL; break;
                        case '*': break;
                        default : state = StateType.BLOCK_COMMENT; break;
                    }
                    break;

                case SLASH_SLASH_COMMENT:
                    if (ch == '\n') {
                        print('\n');
                        state = StateType.NORMAL;
                    }
                    break;

                case BLOCK_COMMENT:
                    switch (ch) {
                        case '*' :            state = StateType.MAYBE_UNCOMMENT; break;
                        case '\n': print(ch);                                    break;
                    }
                    break;

                case DOUBLE_QUOTE:
                    switch (ch) {
                        case '\\': print(ch); state = StateType.ESCAPE_ONE_DOUBLE; break;
                        case '"' : print(ch); state = StateType.NORMAL;            break;
                        default  : print(ch);                                      break;
                    }
                    break;

                case SINGLE_QUOTE:
                    switch (ch) {
                        case '\\': print(ch); state = StateType.ESCAPE_ONE_SINGLE; break;
                        case '\'': print(ch); state = StateType.NORMAL;            break;
                        default  : print(ch);                                      break;
                    }
                    break;

                case ESCAPE_ONE_DOUBLE:
                    print(ch);
                    state = StateType.DOUBLE_QUOTE;
                    break;

                case ESCAPE_ONE_SINGLE:
                    print(ch);
                    state = StateType.SINGLE_QUOTE;
                    break;
            }
        }
    }

}
