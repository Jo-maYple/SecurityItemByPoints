package com.sixarea.securityitembypoints;

import com.sixarea.securityitembypoints.commands.Commands;
import com.sixarea.securityitembypoints.listeners.Listeners;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class SecurityItemByPoints extends JavaPlugin {

    public static Plugin plugin = null;
    public static PlayerPointsAPI ppAPI;

    @Override
    public void onEnable() {
        try{
            getLogger().info("SecurityItemByPoints-V1.0 正在启动中");
            plugin = this;
            if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
                ppAPI = PlayerPoints.getInstance().getAPI();
            }
            Objects.requireNonNull(Bukkit.getPluginCommand("securityitembypoints")).setExecutor(new Commands());
            Objects.requireNonNull(Bukkit.getPluginCommand("securityitembypoints")).setTabCompleter(new Commands());
            getServer().getPluginManager().registerEvents((Listener) new Listeners(), (Plugin)this);
            if (!(new File(getDataFolder(), "config.yml")).exists()){
                saveResource("config.yml", false);
            }
            if (!(new File(getDataFolder(), "data.yml")).exists()){
                saveResource("data.yml", false);
            }
            getLogger().info("SecurityItemByPoints-V1.0 启动完成");
        }
        catch (Exception e){
            getLogger().warning("SecurityItemByPoints 插件启动失败 请检查配置文件!!!!!!!!!!");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("SecurityItemByPoints 插件卸载完成\nBye");
        // Plugin shutdown logic
    }
}
