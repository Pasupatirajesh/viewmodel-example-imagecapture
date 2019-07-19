package com.course.udacity.android.mytweaksdemo;

import android.content.pm.PackageManager;

/**
 * Utility class that wraps access to the runtime permissions API in M and provides helper methods
 */
public abstract class PermissionUtil {
    /**
     * Check that all given permissions have been granted by verifying that each entry in
     * the given array is of the value (@link PackageManager#PERMISSION_GRANTED}.
     */

    public static boolean verifyPermissions(int[] grantResults){
        // At least ine result must be checked
        if (grantResults.length <1){
            return false;
        }

        //Verify that each required permission has been granted, otherwise return false.
        for (int result: grantResults){
            if (result!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }

        return true;
    }
}
