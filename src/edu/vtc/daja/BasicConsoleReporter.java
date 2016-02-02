package edu.vtc.daja;


public class BasicConsoleReporter implements Reporter {

    int errorCount = 0;
    int warningCount = 0;

    public void reportError(int line, int column, String message)
    {
        errorCount++;
        System.out.printf("ERR: [Line %3d, Column %3d] %s\n", line, column, message);
    }


    public void reportWarning(int line, int column, String message)
    {
        warningCount++;
        System.out.printf("WRN: [Line %3d, Column %3d] %s\n", line, column, message);
    }


    /**
     * Accessor to return a count of errors sent to this object so far.
     */
    public int getErrorCount()
    {
        return errorCount;
    }


    /**
     * Accessor to return a coune of warnings sent to this object so far.
     */
    public int getWarningCount()
    {
        return warningCount;
    }
}
