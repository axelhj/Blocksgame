package se.axelhjelmqvist.blocksgame;

import android.content.Context;
import android.util.Log;

/**
 * Log tool that can be used for debugging purposes and
 * tracking certain conditions and program execution.
 */
public class LogTool {
    private static String PACKAGE_NAME;

    /**
     * Init logic of the log tool. The logtool uses the
     * Context object to access application info and package name.
     */
    public static void init(Context context) {
        if (BuildConfig.DEBUG)
            PACKAGE_NAME =
                context.getPackageName()
                + "."
                + context.getApplicationInfo().className;
    }

    /**
     * Method that is used to write a verbose message to the
     * application log output if the build is a DEBUG-build.
     */
    public static void printVerbose(String message) {
        if (BuildConfig.DEBUG) {
            Log.v(PACKAGE_NAME, message, null);
        }
    }
    
    /**
     * Method that is used to write an error message to the
     * application log output if the build is a DEBUG-build.
     */
    public static void printError(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(PACKAGE_NAME, message, null);
        }
    }
}
