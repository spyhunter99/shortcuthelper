/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.shortcuthelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StreamUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.exceptions.NativeException;
import org.netbeans.installer.utils.system.shortcut.FileShortcut;
import org.netbeans.installer.utils.system.shortcut.LocationType;
import org.netbeans.installer.utils.system.shortcut.Shortcut;

public class Main {

    public static final String SHORTCUT_FILENAME
            = ResourceUtils.getString(Main.class, "CL.app.name") + ".desktop"; // NOI18N

    public static final String[] SHORTCUT_CATEGORIES = new String[]{
        "Application"
    };

    public static final String BIN_SUBDIR
            = "bin/";

    public static final String EXECUTABLE_WINDOWS
            = BIN_SUBDIR
            + ResourceUtils.getString(Main.class, "CL.app.name") + ".exe"; // NOI18N

    public static final String EXECUTABLE_UNIX
            = BIN_SUBDIR
            + ResourceUtils.getString(Main.class, "CL.app.name"); // NOI18N

    public static final String ICON_WINDOWS
            = EXECUTABLE_WINDOWS;

    public static final String ICON_UNIX
            = ResourceUtils.getString(Main.class,
                    "CL.unix.icon.name"); // NOI18N

    public static final String ICON_UNIX_RESOURCE
            = ResourceUtils.getString(Main.class,
                    "CL.unix.icon.resource"); // NOI18N

    public static final String ICON_MACOSX
            = ResourceUtils.getString(Main.class, "CL.app.name") + ".icns"; // NOI18N

    public static final String ICON_MACOSX_RESOURCE
            = "org/mycompany/" + ResourceUtils.getString(Main.class, "CL.app.name") + ".icns"; // NOI18N

    static Map<Locale, String> productName = new HashMap<Locale, String>();
    static Map<Locale, String> productDescription = new HashMap<Locale, String>();
    static String relativePath = "Shortcut Helper";
    static {
        productName.put(Locale.getDefault(), "ShortcutHelper");
        productDescription.put(Locale.getDefault(), "A product description");
    }
    
    
    public static void main(String[] args) throws NativeException {

        File installLocation = new File("C:\\projects\\ShortCutHelper\\target\\");
        String executable = "ShortCutHelper-8.2.0-jar-with-dependencies.jar";
        new Main().install(installLocation, executable);
    }
    public void install(File installLocation, String  executable) {
        //desktop shortcut
        if (!SystemUtils.isMacOS()) {
            try {
                if (SystemUtils.isCurrentUserAdmin()) {
                    SystemUtils.createShortcut(
                            getDesktopShortcut(installLocation, executable),
                            LocationType.ALL_USERS_DESKTOP);

                } else {
                    SystemUtils.createShortcut(
                            getDesktopShortcut(installLocation, executable),
                            LocationType.CURRENT_USER_DESKTOP);
                }
            } catch (NativeException e) {
                e.printStackTrace();
            }
        } else {
            //"... skipping this step as we're on Mac OS"); // NOI18N
        }

        /////////////////////////////////////////////
        // create start menu shortcut
        try {
            if (SystemUtils.isCurrentUserAdmin()) {

                SystemUtils.createShortcut(
                        getStartMenuShortcut(installLocation,executable),
                        LocationType.ALL_USERS_START_MENU);
            } else {
                SystemUtils.createShortcut(
                        getStartMenuShortcut(installLocation,executable),
                        LocationType.CURRENT_USER_START_MENU);
            }
        } catch (NativeException e) {
            e.printStackTrace();
        }

        //Shortcut sc = new FileShortcut("Shortcut title", new File("path/to/executable"));
        //SystemUtils.createShortcut(sc, LocationType.CURRENT_USER_DESKTOP);
    }

    private Shortcut getDesktopShortcut(final File directory, String  executable) {
        
        
        return getShortcut(
                productName,
                productDescription,
                "",
                directory,executable);
    }

    private Shortcut getStartMenuShortcut(final File directory, String  executable) {
        if (SystemUtils.isMacOS()) {
              return getShortcut(
                productName,
                productDescription,
                relativePath,     //relative path within the start menu
                directory,executable);
        } else {
              return getShortcut(
                productName,
                productDescription,
               relativePath,     //relative path within the start menu
                directory,executable);
        }
    }

    private Shortcut getShortcut(
            final Map<Locale, String> names,
            final Map<Locale, String> descriptions,
            final String relativePath,
            final File workingDirectory, 
            final String executableName) {
        final File icon;
        final File executable;

        if (SystemUtils.isWindows()) {
            icon = new File(workingDirectory, ICON_WINDOWS);
        } else if (SystemUtils.isMacOS()) {
            icon = new File(workingDirectory, ICON_MACOSX);
        } else {
            icon = new File(workingDirectory, ICON_UNIX);
            //LogManager.log("... icon file: " + icon);
            if (!FileUtils.exists(icon)) {
                //LogManager.log("... icon file does not exist: " + icon);
                InputStream is = null;
                is = ResourceUtils.getResource(ICON_UNIX_RESOURCE, this.getClass().getClassLoader());
                if (is != null) {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(icon);
                        StreamUtils.transferData(is, fos);
                        is.close();
                        fos.close();

                    } catch (IOException e) {

                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            }
        }

        if (SystemUtils.isWindows()) {
            executable = new File(workingDirectory, executableName);
        } else {
            executable = new File(workingDirectory, executableName);
        }
        final String name = names.get(new Locale(StringUtils.EMPTY_STRING));
        final FileShortcut shortcut = new FileShortcut(name, executable);
        shortcut.setNames(names);
        shortcut.setDescriptions(descriptions);
        shortcut.setCategories(SHORTCUT_CATEGORIES);
        shortcut.setFileName(SHORTCUT_FILENAME);
        shortcut.setIcon(icon);
        shortcut.setRelativePath(relativePath);
        shortcut.setWorkingDirectory(workingDirectory);
        shortcut.setModifyPath(true);

        return shortcut;
    }
}
