package com.skyrowl.multilanguageintegration;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class multiLanguageIntegration extends PlaceholderExpansion implements Listener {

    private File configFolder = new File("plugins/PlaceholderAPI/mli");
    private HashMap<String, FileConfiguration> fileList = new HashMap<>();
    private URLClassLoader urlClassLoader;
    private FileConfiguration tmpConfigFile;

    public multiLanguageIntegration() {
        loadFiles();
        System.out.println("§2[MLI Info] Multi Language Integration for PlaceholderAPI have been loaded.");
    }

    /**
     * This method should always return true unless we have a dependency we need to
     * make sure is on the server for our placeholders to work!
     *
     * @return always true since we do not have any dependencies.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return "skyrowl";
    }

    /**
     * The placeholder identifier should go here. <br>
     * This is what tells PlaceholderAPI to call our onRequest method to obtain a
     * value if a placeholder starts with our identifier. <br>
     * This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "mli";
    }

    /**
     * This is the version of this expansion. <br>
     * You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    /**
     * This is the method called when a placeholder with our identifier is found and
     * needs a value. <br>
     * We specify the value identifier in this method. <br>
     * Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param player     A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param identifier A String containing the identifier/value.
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        // %example_placeholder1%
        String[] fileCheck = identifier.split("_", 2);
        if (fileCheck.length > 1) {
            if (fileList.containsKey(fileCheck[0] + ".yml")) {
                FileConfiguration configFile = fileList.get(fileCheck[0] + ".yml");
                String placeholder = configFile.getString(fileCheck[1] + "." + player.getPlayer().getLocale());
                if (placeholder != null) {
                    return PlaceholderAPI.setPlaceholders(player, placeholder);
                } else {
                    String defaultPlaceholder = configFile.getString(fileCheck[1] + ".en_us");
                    if (defaultPlaceholder != null) {
                        return PlaceholderAPI.setPlaceholders(player, defaultPlaceholder);
                    } else {
                        System.out.println("§6[MLI Warn] Placeholder '" + fileCheck[1] + "' doesn't exist in " + fileCheck[0] + ".yml.");
                    }
                }
            } else {
                System.out.println("§6[MLI Warn] " + fileCheck[0] + ".yml" + " doesn't exist.");
            }
        } else {
            System.out.println("§6[MLI Warn] The placeholder must contain the name of the file and the name of the placeholder.");
        }

        // We return null if an invalid placeholder (f.e. %example_placeholder3%)
        // was provided
        return null;
    }

    public void generateDefautlConfig() {
        URL mliUrl = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        urlClassLoader = new URLClassLoader(new URL[] { mliUrl });
        InputStream customConfig = urlClassLoader.getResourceAsStream("default.yml");
        File customConfigFile = new File("plugins/PlaceholderAPI/mli", "default.yml");
        try {
            customConfigFile.getParentFile().mkdirs();
            customConfigFile.createNewFile();
            Files.copy(customConfig, Paths.get(customConfigFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFiles() {
        File[] filesList = configFolder.listFiles();
        if (filesList != null && filesList.length > 0) {
            for (File phFile : filesList)  {
                tmpConfigFile = YamlConfiguration.loadConfiguration(phFile);
                fileList.put(phFile.getName(), tmpConfigFile);
            }
            System.out.println("§2[MLI Info] " + configFolder.listFiles().length + " config files have been found.");
        } else {
            System.out.println("§2[MLI Info] No config files have been found, generating a default one.");
            generateDefautlConfig();
        }
    }

}
