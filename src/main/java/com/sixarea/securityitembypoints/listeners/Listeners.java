package com.sixarea.securityitembypoints.listeners;

import com.sixarea.securityitembypoints.SecurityItemByPoints;
import com.sixarea.securityitembypoints.configUtil.ConfigUtil;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Listeners implements Listener {

    private static final Material AIR = Material.AIR;

    public static boolean isEmpty(ItemStack itemstack){
        return itemstack == null || itemstack.getType().equals(AIR);
    }

    public static String itemToString(ItemStack itemStack){
        NBTCompound itemData = NBTItem.convertItemtoNBT(itemStack);
        return itemData.toString();
    }
    public static ItemStack stringToItem(String st){
        return NBTItem.convertNBTtoItem(new NBTContainer(st));
    }
    public static boolean isSlashBlade(ItemStack itemstack){
        String itemMeta = itemstack.getType().toString().toUpperCase();
        List<String> itemMetaList = ConfigUtil.config.getStringList("ItemMetaWhiteList");
        return itemMetaList.contains(itemMeta);
    }
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
                    e.getWhoClicked().sendMessage("??9[??3SIBP??9] ??f-> ??e??l????????????");
                } else if (ConfigUtil.data.getString("Data." + e.getWhoClicked().getName() + ".count", "0").equals("1")){
                    e.getWhoClicked().sendMessage("??9[??3SIBP??9] ??f-> ??e??l???????????????????????????????????????????????????");
                } else if (isEmpty(e.getWhoClicked().getInventory().getItemInMainHand())){
                    e.getWhoClicked().sendMessage("??9[??3SIBP??9] ??f-> ??e??l???????????????");
                } else if (!isSlashBlade(e.getWhoClicked().getInventory().getItemInMainHand())){
                    e.getWhoClicked().sendMessage("??9[??3SIBP??9] ??f-> ??e??l??????????????????");
                }
                else {
                    e.getWhoClicked().closeInventory();
                    SecurityItemByPoints.ppAPI.take(e.getWhoClicked().getUniqueId(), 1000);
                    e.getWhoClicked().sendMessage("????????????");
                    ItemStack tempstack = e.getWhoClicked().getInventory().getItemInMainHand();
                    //ItemBuilder ib = new ItemBuilder(tempstack);
                    //ib.addLore("??0UUID:" + e.getWhoClicked().getUniqueId());
                    List<String> lore = new ArrayList<>();
                    if (tempstack.getItemMeta().hasLore())//?????????????????????
                        lore.addAll(tempstack.getItemMeta().getLore());
                    lore.add("??0UUID:" + e.getWhoClicked().getUniqueId());
                    ItemMeta tempMeta = tempstack.getItemMeta();
                    tempMeta.setLore(lore);
                    tempstack.setItemMeta(tempMeta);
                    e.getWhoClicked().getInventory().setItemInMainHand(tempstack);
                    ConfigUtil.data.set("Data." + e.getWhoClicked().getName() + ".data", itemToString(tempstack));
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
                    return;//????????????????????????????????????
                }
                if (!ConfigUtil.data.getString("Data." + e.getWhoClicked().getName() + ".count").equalsIgnoreCase("1")) {
                    return;//??????????????????????????????????????????
                }
                int round = 0;
                for (ItemStack s: e.getWhoClicked().getInventory().getContents()){
                    if (itemToString(s).contains("UUID:" + e.getWhoClicked().getUniqueId())) {
                        round += s.getAmount();
                        if(round >= 1) {
                            e.getWhoClicked().sendMessage("??9[??3SIBP??9] ??f-> ??e??l???????????????????????????????????????????????????");
                            break;
                        }
                    }
                }
                if (round != 0){
                    return;//???????????????????????????????????????
                }
                //giveItem((Player) e.getWhoClicked(), NBTItem.convertNBTtoItem(new NBTContainer(ConfigUtil.data.getString("Data." + e.getWhoClicked().getName() + ".data"))));
                e.getWhoClicked().getInventory().addItem(NBTItem.convertNBTtoItem(new NBTContainer(ConfigUtil.data.getString("Data." + e.getWhoClicked().getName() + ".data"))));
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
        if (!(f.getWhoClicked() instanceof Player) || f.getClickedInventory() == null){
            return;
        }
        if (isEmpty(f.getCurrentItem())){
            return;
        }
        String nbtOfHandItem1 = itemToString(f.getCurrentItem());
        if (nbtOfHandItem1.contains("UUID:") && !nbtOfHandItem1.contains("UUID:" + f.getWhoClicked().getUniqueId())) {
            f.setCancelled(true);
            f.getWhoClicked().sendMessage("??9[??3SIBP??9] ??f-> ??e??l?????????????????????????????????");
        }
    }

    @EventHandler
    public void SecurityPickup(EntityPickupItemEvent e){
        String nbtOfHandItem2 = itemToString(e.getItem().getItemStack());
        if (!(e.getEntity() instanceof Player)){
            return;
        }
        if (nbtOfHandItem2.contains("UUID:") && !nbtOfHandItem2.contains("UUID:" + e.getEntity().getUniqueId())) {
            e.setCancelled(true);
            e.getEntity().sendMessage("??9[??3SIBP??9] ??f-> ??e??l?????????????????????????????????");
        } else if(nbtOfHandItem2.contains("UUID:" + e.getEntity().getUniqueId())){
            int round = 0;
            for (ItemStack s:((Player) e.getEntity()).getInventory().getContents()){
                if (itemToString(s).contains("UUID:" + e.getEntity().getUniqueId())) {
                    round += s.getAmount();
                    if(round >= 1) {
                        e.getEntity().sendMessage("??9[??3SIBP??9] ??f-> ??e??l???????????????????????????????????????????????????");
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
        if (!itemToString(e.getPlayer().getInventory().getItem(e.getNewSlot())).contains("UUID:")){
            return;
        }
        if (!itemToString(e.getPlayer().getInventory().getItem(e.getNewSlot())).contains("UUID:" + e.getPlayer().getUniqueId())){
            e.setCancelled(true);
            e.getPlayer().sendMessage("??9[??3SIBP??9] ??f-> ??e??l?????????????????????????????????");
            return;
        }
        int round = 0;
        for (ItemStack s: e.getPlayer().getInventory().getContents()){
           if (itemToString(s).contains("UUID:" + e.getPlayer().getUniqueId())) {
               round += s.getAmount();
               if(round >= 2) {
                   e.getPlayer().sendMessage("??9[??3SIBP??9] ??f-> ??e??l???????????????????????????????????????????????????");
                   e.setCancelled(true);
                   e.getPlayer().getInventory().setItem(e.getNewSlot(), new ItemStack(AIR));
                   break;
               }
           }
        }
    }
}
