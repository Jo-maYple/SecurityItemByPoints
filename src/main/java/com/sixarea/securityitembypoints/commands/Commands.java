package com.sixarea.securityitembypoints.commands;

import com.sixarea.securityitembypoints.SecurityItemByPoints;
import com.sixarea.securityitembypoints.configUtil.ConfigUtil;
import com.sixarea.securityitembypoints.gui.GUI;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Commands implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (args.length == 0){
            if (sender.hasPermission("sibp.op")){
                sender.sendMessage("§9[§3SIBP§9] §f-> §a---------------------------------------");
                sender.sendMessage("§9[§3SIBP§9] §f->     §cSecurityItemByPoints§r————§e帮助菜单");
                sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp setnbt §7[§cnotice§r/§cbuy§r/§chistory§7] §e更改菜单物品§7[§c左§r/§c中§r/§c右§7]");
                sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp help §e打开帮助菜单");
                sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp open §e打开保险购买界面");
                sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp list §e展示保险购买列表");
                sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp get §7[§c玩家ID§7] §e获取玩家保险物品(无视保险次数限制)");
                sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp reload §e重载配置文件");
                sender.sendMessage("§9[§3SIBP§9] §f-> §a---------------------------------------");
            } else {
                sender.sendMessage("§9[§3SIBP§9] §f-> §a---------------------------------------");
                sender.sendMessage("§9[§3SIBP§9] §f->     §cSecurityItemByPoints§r————§e帮助菜单");
                sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp help §e打开帮助菜单");
                sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp open §e打开保险购买界面");
                sender.sendMessage("§9[§3SIBP§9] §f-> §a---------------------------------------");
            }
            return true;
        }
        if (args.length == 1){
            if (args[0].equalsIgnoreCase("help")){
                if (sender.hasPermission("sibp.op")){
                    sender.sendMessage("§9[§3SIBP§9] §f-> §a---------------------------------------");
                    sender.sendMessage("§9[§3SIBP§9] §f->     §cSecurityItemByPoints§r————§e帮助菜单");
                    sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp setnbt §7[§cnotice§r/§cbuy§r/§chistory§7] §e更改菜单物品§7[§c左§r/§c中§r/§c右§7]");
                    sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp help §e打开帮助菜单");
                    sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp open §e打开保险购买界面");
                    sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp list §e展示保险购买列表");
                    sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp get §7[§c玩家ID§7] §e获取玩家保险物品(无视保险次数限制)");
                    sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp reload §e重载配置文件");
                    sender.sendMessage("§9[§3SIBP§9] §f-> §a---------------------------------------");
                } else {
                    sender.sendMessage("§9[§3SIBP§9] §f-> §a---------------------------------------");
                    sender.sendMessage("§9[§3SIBP§9] §f->     §cSecurityItemByPoints§r————§e帮助菜单");
                    sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp help §e打开帮助菜单");
                    sender.sendMessage("§9[§3SIBP§9] §f-> §c/sibp open §e打开保险购买界面");
                    sender.sendMessage("§9[§3SIBP§9] §f-> §a---------------------------------------");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")){
                if (sender.hasPermission("sibp.op")) {
                    File file1 = new File(SecurityItemByPoints.plugin.getDataFolder(), "config.yml");
                    File file2 = new File(SecurityItemByPoints.plugin.getDataFolder(), "data.yml");
                    ConfigUtil.config = (FileConfiguration) YamlConfiguration.loadConfiguration(file1);
                    ConfigUtil.data = (FileConfiguration) YamlConfiguration.loadConfiguration(file2);
                    sender.sendMessage("§9[§3SIBP§9] §f-> §e§l重载配置文件成功");
                } else {
                    sender.sendMessage("§9[§3SIBP§9] §f-> §e§l您没有使用该命令的权限");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("open")){
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§9[§3SIBP§9] §f-> §e§l此命令仅限玩家使用");
                    return true;
                }
                ItemStack notice = NBTItem.convertNBTtoItem(new NBTContainer(ConfigUtil.config.getString("ChestInfo.notice")));
                ItemStack buy = NBTItem.convertNBTtoItem(new NBTContainer(ConfigUtil.config.getString("ChestInfo.buy")));
                ItemStack history = NBTItem.convertNBTtoItem(new NBTContainer(ConfigUtil.config.getString("ChestInfo.history")));
                //ItemBuilder ib = new ItemBuilder(history);
                List<String> lore = new ArrayList<>();
                if (history.getItemMeta().hasLore())
                     lore.addAll(history.getItemMeta().getLore());
                if (ConfigUtil.data.getString("Data." + sender.getName() + ".time") == null){
                    lore.add("§7§o§l保险未购买");
                } else {
                    lore.add("§6购买时间§7:§e§o" + ConfigUtil.data.getString("Data." + sender.getName() + ".time"));
                    if (!ConfigUtil.data.getString("Data." + sender.getName() + ".count").equals("1")){
                        lore.add("§7§o§l保险已失效");
                    } else {
                        lore.add("§b§o§l保险生效中");
                    }
                }
                ItemMeta tempMeta = history.getItemMeta();
                tempMeta.setLore(lore);
                history.setItemMeta(tempMeta);
                //history = ib.getItemStack();
                String title = ConfigUtil.config.getString("ChestInfo.title");
                GUI.getChestGui((Player) sender, title, notice, buy, history);
                return true;
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("sibp.op")) {
                    sender.sendMessage("§9[§3SIBP§9] §f-> §e§l您没有使用该命令的权限");
                    return true;
                }
                sender.sendMessage("§9[§3SIBP§9] §f-> §e§l已购买保险的玩家有:");
                StringBuilder sb = new StringBuilder();
                for (String s: ConfigUtil.data.getStringList("PlayerList")) {
                    if (ConfigUtil.data.getString("Data." + s + ".count").equals("1")){
                        sb.append(" §f-§e" + s + "§7[§a在保§7]");
                    } else {
                        sb.append(" §f-§e" + s + "§7[§c过保§7]");
                    }
                }
                if(sb.length() != 0){
                    sender.sendMessage(sb.toString());
                } else {
                    sender.sendMessage("§f - §c 无玩家");
                }
                return true;
            }
        }
        if (args.length == 2){
            if (args[0].equalsIgnoreCase("setnbt")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§9[§3SIBP§9] §f-> §e§l仅限玩家");
                }
                else if (!sender.hasPermission("sibp.op")) {
                    sender.sendMessage("§9[§3SIBP§9] §f-> §e§l您没有使用该命令的权限");
                } else {
                    if (((Player)sender).getEquipment().getItemInMainHand() == null || ((Player)sender).getEquipment().getItemInMainHand().getType().equals(Material.AIR)){
                        if (args[1].equalsIgnoreCase("notice")) {
                            NBTCompound itemData = NBTItem.convertItemtoNBT(((Player) sender).getInventory().getItemInMainHand());
                            ConfigUtil.config.set("ChestInfo.notice", itemData.toString());
                            try {
                                File file =new File(SecurityItemByPoints.plugin.getDataFolder(), "config.yml");
                                ConfigUtil.config.save(file);
                            } catch (IOException s){
                                s.printStackTrace();
                            }
                            sender.sendMessage("§9[§3SIBP§9] §f-> §e§l添加成功");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("buy")) {
                            NBTCompound itemData = NBTItem.convertItemtoNBT(((Player) sender).getInventory().getItemInMainHand());
                            ConfigUtil.config.set("ChestInfo.buy", itemData.toString());
                            try {
                                File file =new File(SecurityItemByPoints.plugin.getDataFolder(), "config.yml");
                                ConfigUtil.config.save(file);
                            } catch (IOException s){
                                s.printStackTrace();
                            }
                            sender.sendMessage("§9[§3SIBP§9] §f-> §e§l添加成功");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("history")) {
                            NBTCompound itemData = NBTItem.convertItemtoNBT(((Player) sender).getInventory().getItemInMainHand());
                            ConfigUtil.config.set("ChestInfo.history", itemData.toString());
                            try {
                                File file =new File(SecurityItemByPoints.plugin.getDataFolder(), "config.yml");
                                ConfigUtil.config.save(file);
                            } catch (IOException s){
                                s.printStackTrace();
                            }
                            sender.sendMessage("§9[§3SIBP§9] §f-> §e§l添加成功");
                            return true;
                        }
                        sender.sendMessage("§9[§3SIBP§9] §f-> §e§l指令输入有误");
                    } else {
                        sender.sendMessage("§9[§3SIBP§9] §f-> §e§l你不能空手");
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("get")){
                List<String> playerlist = ConfigUtil.data.getStringList("PlayerList");
                if (!sender.hasPermission("sibp.op")) {
                    sender.sendMessage("§9[§3SIBP§9] §f-> §e§l您没有使用该命令的权限");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§9[§3SIBP§9] §f-> §e§l仅限玩家");
                } else {
                    if (!playerlist.contains(args[1])){
                        sender.sendMessage("§9[§3SIBP§9] §f-> §e§l玩家不存在");
                        return true;
                    } else {
                        ((Player) sender).getInventory().addItem(NBTItem.convertNBTtoItem(new NBTContainer(ConfigUtil.data.getString("Data." + args[1] + ".data"))));
                        //giveItem((Player) sender, NBTItem.convertNBTtoItem(new NBTContainer(ConfigUtil.data.getString("Data." + args[1] + ".data"))));
                        sender.sendMessage("§9[§3SIBP§9] §f-> §e§l发送成功");
                    }
                }
            return true;
            }
        }
        sender.sendMessage("§9[§3SIBP§9] §f-> §e§l指令输入有误");
        return true;
    }

    private final List<String> ARGS1OP = Arrays.asList("setnbt", "help", "open", "list", "get", "reload");
    private final List<String> ARGS1 = Collections.singletonList("open");
    private final List<String> ARGS2 = Arrays.asList("notice", "buy", "history");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (!(sender instanceof Player)) {
            return null;
        }
        if (args.length == 1 && sender.hasPermission("sibp.op")){
            return ARGS1OP;
        } else if (args.length == 1 && !sender.hasPermission("sibp.op")){
            return ARGS1;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("setnbt") && sender.hasPermission("sibp.op")){
            return ARGS2;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("get") && sender.hasPermission("sibp.op")){
            return ConfigUtil.data.getStringList("PlayerList");
        }
        return null;
    }
}