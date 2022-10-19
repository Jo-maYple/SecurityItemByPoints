package com.sixarea.securityitembypoints.configUtil;

import com.sixarea.securityitembypoints.SecurityItemByPoints;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigUtil {
    public static File file = new File(SecurityItemByPoints.plugin.getDataFolder(), "config.yml");

    public static FileConfiguration config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);

    public static File file2 = new File(SecurityItemByPoints.plugin.getDataFolder(), "data.yml");

    public static FileConfiguration data = (FileConfiguration) YamlConfiguration.loadConfiguration(file2);
}
