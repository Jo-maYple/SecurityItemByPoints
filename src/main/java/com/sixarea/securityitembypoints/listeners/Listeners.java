package com.sixarea.securityitembypoints.listeners;

import com.sixarea.securityitembypoints.SecurityItemByPoints;
import com.sixarea.securityitembypoints.configUtil.ConfigUtil;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import pers.tany.yukinoaapi.interfacepart.item.IItem;
import pers.tany.yukinoaapi.realizationpart.builder.ItemBuilder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.bukkit.Material.AIR;
import static pers.tany.yukinoaapi.interfacepart.item.IItem.*;
import static pers.tany.yukinoaapi.interfacepart.player.IPlayer.giveItem;


public class Listeners implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Player player = (Player) e.getWhoClicked();
        InventoryView inv = player.getOpenInventory();
        if (inv.getTitle().equals(ConfigUtil.config.getString("ChestInfo.title"))){
            e.setCancelled(true);
            if (e.getRawSlot() < 0 || e.getRawSlot() > e.getInventory().getSize()){
                return;
            }
            ItemStack clickeditem = e.getCurrentItem();
            if (clickeditem == null){
                return;
            }
            NBTCompound itemdata = NBTItem.convertItemtoNBT(clickeditem);
            if (itemdata.toString().equals(ConfigUtil.config.getString("ChestInfo.buy"))){
                if (SecurityItemByPoints.ppAPI.look(e.getWhoClicked().getUniqueId()) < 1000){
                    e.getWhoClicked().sendMessage("§9[§3SIBP§9] §f-> §e§l点券不足");
                } else if (ConfigUtil.data.getString("Data." + e.getWhoClicked().getName() + ".count", "0").equals("1")){
                    e.getWhoClicked().sendMessage("§9[§3SIBP§9] §f-> §e§l你已经购买过一份保险并且处于生效中");
                } else {
                    e.getWhoClicked().closeInventory();
                    SecurityItemByPoints.ppAPI.take(e.getWhoClicked().getUniqueId(), 1000);
                    e.getWhoClicked().sendMessage("购买成功");
                    ItemStack tempstack = e.getWhoClicked().getInventory().getItemInMainHand();
                    ItemBuilder ib = new ItemBuilder(tempstack);
                    ib.addLore("§0UUID:" + e.getWhoClicked().getUniqueId());
                    e.getWhoClicked().getInventory().setItemInMainHand(ib.getItemStack());
                    ConfigUtil.data.set("Data." + e.getWhoClicked().getName() + ".data", getItemNBT(ib.getItemStack()));
                    ConfigUtil.data.set("Data." + e.getWhoClicked().getName() + ".time", dateTime.format(formatter));
                    ConfigUtil.data.set("Data." + e.getWhoClicked().getName() + ".count", 1);
                    List<String> playerlist = ConfigUtil.data.getStringList("PlayerList");
                    playerlist.add(e.getWhoClicked().getName());
                    ConfigUtil.data.set("PlayerList", playerlist);
                    File file =new File(SecurityItemByPoints.plugin.getDataFolder(), "data.yml");
                    try {
                        ConfigUtil.data.save(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                e.getWhoClicked().closeInventory();
                return;
            }
            if (e.getSlot() == 16){
                e.getWhoClicked().closeInventory();
                List<String> playerName = ConfigUtil.data.getStringList("PlayerList");
                if (!playerName.contains(e.getWhoClicked().getName())) {
                    return;//如果玩家未购买保险则退出
                }
                if (!ConfigUtil.data.getString("Data." + e.getWhoClicked().getName() + ".count").equalsIgnoreCase("1")) {
                    return;//如果可用保险次数不为一则退出
                }
                int round = 0;
                for (ItemStack s:((Player) e.getWhoClicked()).getInventory().getContents()){
                    if (getItemNBT(s).contains("UUID:" + e.getWhoClicked().getUniqueId())) {
                        round += s.getAmount();
                        if(round >= 1) {
                            e.getWhoClicked().sendMessage("§9[§3SIBP§9] §f-> §e§l检测到背包已拥有保险物品，无法领取");
                            break;
                        }
                    }
                }
                if (round != 0){
                    return;//如果背包内有保险物品则退出
                }
                giveItem((Player) e.getWhoClicked(), NBTItem.convertNBTtoItem(new NBTContainer(ConfigUtil.data.getString("Data." + e.getWhoClicked().getName() + ".data"))));
                ConfigUtil.data.set("Data." + e.getWhoClicked().getName() + ".count", 0);
                List<String> playerlist = ConfigUtil.data.getStringList("PlayerList");
                playerlist.remove(e.getWhoClicked().getName());
                ConfigUtil.data.set("PlayerList", playerlist);
                File file = new File(SecurityItemByPoints.plugin.getDataFolder(), "data.yml");
                try {
                    ConfigUtil.data.save(file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void Security1(InventoryClickEvent f){
        if (!(f.getWhoClicked() instanceof Player) || f.getClickedInventory() == null || !f.getClickedInventory().getType().equals(InventoryType.PLAYER)){
            return;
        }
        if (IItem.isEmpty(f.getCurrentItem())){
            return;
        }
        String nbtOfHandItem1 = getItemNBT(f.getCurrentItem());
        if (nbtOfHandItem1.contains("UUID:") && !nbtOfHandItem1.contains("UUID:" + f.getWhoClicked().getUniqueId())) {
            f.setCancelled(true);
            f.getWhoClicked().sendMessage("§9[§3SIBP§9] §f-> §e§l物品已被绑定，无法点击");
        }
    }

    @EventHandler
    public void SecurityPickup(EntityPickupItemEvent e){
        String nbtOfHandItem2 = getItemNBT(e.getItem().getItemStack());
        if (!(e.getEntity() instanceof Player)){
            return;
        }
        if (nbtOfHandItem2.contains("UUID:") && !nbtOfHandItem2.contains("UUID:" + e.getEntity().getUniqueId())) {
            e.setCancelled(true);
            e.getEntity().sendMessage("§9[§3SIBP§9] §f-> §e§l物品已被绑定，无法拾取");
        } else if(nbtOfHandItem2.contains("UUID:" + e.getEntity().getUniqueId())){
            int round = 0;
            for (ItemStack s:((Player) e.getEntity()).getInventory().getContents()){
                if (getItemNBT(s).contains("UUID:" + e.getEntity().getUniqueId())) {
                    round += s.getAmount();
                    if(round >= 1) {
                        e.getEntity().sendMessage("§9[§3SIBP§9] §f-> §e§l检测到同时拥有两件保险物品，已删除");
                        e.setCancelled(true);
                        e.getItem().setItemStack(new ItemStack(AIR));
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void SecurityUse(PlayerItemHeldEvent e){
       if (!getItemNBT(e.getPlayer().getInventory().getItem(e.getNewSlot())).contains("UUID:" + e.getPlayer().getUniqueId())){
           return;
       }
        int round = 0;
        for (ItemStack s:((Player) e.getPlayer()).getInventory().getContents()){
            if (getItemNBT(s).contains("UUID:" + e.getPlayer().getUniqueId())) {
                round += s.getAmount();
                if(round >= 2) {
                    e.getPlayer().sendMessage("§9[§3SIBP§9] §f-> §e§l检测到同时拥有两件保险物品，已删除");
                    e.setCancelled(true);
                    e.getPlayer().getInventory().setItem(e.getNewSlot(), new ItemStack(AIR));
                    break;
                }
            }
        }
    }
}
