/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.shortcuthelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.exceptions.NativeException;
import org.netbeans.installer.utils.system.shortcut.FileShortcut;
import org.netbeans.installer.utils.system.shortcut.LocationType;
import org.netbeans.installer.utils.system.shortcut.Shortcut;

public class Main {

    //prerequistes
    //you need an icon
    //windows must be bitmap with the .ico file extension
    public static final String[] SHORTCUT_CATEGORIES = new String[]{
        "Application"
    };

    /**
     *
     * @param desktop
     * @param startmenu
     * @param installLocation
     * @param executable
     * @param productName
     * @param productDescription
     * @param relativePathStartMenu
     * @param icon windows needs to be a bitmap .bmp with the extension .ico<br>
     * linux/unit based systems needs a TBD <br>
     * macos based systems needs a .icns or a png/jpeg
     * file
     * @return
     */
    public static List<File> createShortCuts(boolean desktop,
            boolean startmenu,
            File installLocation, //path to where it runs
            String executable, //your jar file
            String productName,
            String productDescription,
            String relativePathStartMenu,
            String icon) {
        List<File> filesCreated = new ArrayList<File>();
        if (desktop) {
            //desktop shortcut
            if (!SystemUtils.isMacOS()) {
                try {
                    if (SystemUtils.isCurrentUserAdmin()) {
                        filesCreated.add(SystemUtils.createShortcut(
                                getDesktopShortcut(installLocation, executable, productName, productDescription, icon),
                                LocationType.ALL_USERS_DESKTOP));

                    } else {
                        filesCreated.add(
                                SystemUtils.createShortcut(
                                        getDesktopShortcut(installLocation, executable, productName, productDescription, icon),
                                        LocationType.CURRENT_USER_DESKTOP));
                    }
                } catch (NativeException e) {
                    e.printStackTrace();
                }
            } else {
                //"... skipping this step as we're on Mac OS"); // NOI18N
            }

        }

        if (startmenu) {
            /////////////////////////////////////////////
            // create start menu shortcut
            try {
                if (SystemUtils.isCurrentUserAdmin()) {
                    filesCreated.add(
                            SystemUtils.createShortcut(
                                    getStartMenuShortcut(installLocation, executable, productName, productDescription, relativePathStartMenu, icon),
                                    LocationType.ALL_USERS_START_MENU));
                } else {
                    filesCreated.add(
                            SystemUtils.createShortcut(
                                    getStartMenuShortcut(installLocation, executable, productName, productDescription, relativePathStartMenu, icon),
                                    LocationType.CURRENT_USER_START_MENU));
                }
            } catch (NativeException e) {
                e.printStackTrace();
            }

        }
        return filesCreated;

    }

    public static void main(String[] args) throws NativeException {

        File installLocation = new File("C:\\projects\\ShortCutHelper\\target\\");

        List<File> createShortCuts = createShortCuts(true, true, installLocation, "ShortCutHelper-8.2.0-jar-with-dependencies.jar", "My Product Name", "This is a cool description", "Github", "icon");
        for (File f : createShortCuts) {
            System.out.println("Created " + f.getAbsolutePath());
        }
    }

    private static Shortcut getDesktopShortcut(final File directory, String executable, String productName, String productDescription, String icon) {

        return getShortcut(
                productName,
                productDescription,
                "", //put it right on the desktop
                directory, executable, icon);
    }

    private static Shortcut getStartMenuShortcut(final File directory, String executable, String productName, String productDescription, String relativePathStartMenu, String icon) {
        if (SystemUtils.isMacOS()) {
            return getShortcut(
                    productName,
                    productDescription,
                    relativePathStartMenu, //relative path within the start menu
                    directory, executable, icon);
        } else {
            return getShortcut(
                    productName,
                    productDescription,
                    relativePathStartMenu, //relative path within the start menu
                    directory, executable, icon);
        }
    }

    private static Shortcut getShortcut(
            final String productName,
            final String productDescription,
            final String relativePath,
            final File workingDirectory,
            final String executableName,
            final String icon) {

        File iconLocation = new File(workingDirectory, icon);
        if (!iconLocation.exists()) {
            //TODO log warning
        }

        File executable = new File(workingDirectory, executableName);

        final FileShortcut shortcut = new FileShortcut(productName, executable);
        Map<Locale, String> names = new Hashtable<Locale, String>();
        names.put(Locale.getDefault(), productName);
        shortcut.setNames(names);

        Map<Locale, String> descriptions = new Hashtable<Locale, String>();
        descriptions.put(Locale.getDefault(), productDescription);
        shortcut.setDescriptions(descriptions);
        shortcut.setCategories(SHORTCUT_CATEGORIES);
        shortcut.setFileName(productName);
        shortcut.setIcon(iconLocation);
        shortcut.setRelativePath(relativePath); //of the shortcut i.e. start menu/Organization/Application link
        shortcut.setWorkingDirectory(workingDirectory);
        shortcut.setModifyPath(true);

        return shortcut;
    }
}
