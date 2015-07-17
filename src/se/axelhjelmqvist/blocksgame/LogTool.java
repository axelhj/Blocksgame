package se.axelhjelmqvist.blocksgame;

import android.content.Context;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Axel
 * Date: 2013-10-15
 * Time: 18:59
 * To change this template use File | Settings | File Templates.
 */
public class LogTool {
    private static String PACKAGE_NAME;
    public static void init(Context context) {
        if (BuildConfig.DEBUG)
            PACKAGE_NAME =
                context.getPackageName()
                + "."
                + context.getApplicationInfo().className;
    }

    public static void printVerbose(String message) {
        if (BuildConfig.DEBUG) {
            Log.v(PACKAGE_NAME, message, null);
        }
    }
}
