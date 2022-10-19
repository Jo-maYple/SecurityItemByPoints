package com.sixarea.securityitembypoints.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI {
    public static void getChestGui(Player player, String title, ItemStack notice, ItemStack buy, ItemStack history){
        Inventory inv = Bukkit.createInventory(player, 3 * 9, title);
        inv.setItem(10, notice);
        inv.setItem(13, buy);
        inv.setItem(16, history);
        player.openInventory(inv);
    }
}
