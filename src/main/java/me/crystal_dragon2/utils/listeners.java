package me.crystal_dragon2.utils;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import me.crystal_dragon2.prsison;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class listeners implements Listener{

    Map<Player,Integer> mines;

    prsison instance;

    miningAndBlocks mine;
    public listeners(prsison plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        instance = plugin;
        mine = new miningAndBlocks();
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onBlockPickaxe(BlockBreakEvent e) {
        final Block b = e.getBlock();
        final Player p = e.getPlayer();
        if(e.isCancelled()){
            return;
        }


        for(String str : instance.getConfig().getStringList("destroyBlock")){
            if(e.getBlock().getType() == getMinecraftItemIDMaterial(str) || e.getBlock().getType() == Material.getMaterial(str)){
                mine.onMine(p, b, 1);
                return;
            }
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        prsison.getInstance().getConfig().set("Chat.ChatColor." + e.getPlayer().getName(), "WHITE");
        try {
            Connection con = prsison.getInstance().establishConnection();
            String table = prsison.getPlugin(prsison.class).getConfig().getString("table");
            PreparedStatement st = con.prepareStatement("SELECT * FROM "+table+" WHERE UUID = ?;");
            st.setString(1, e.getPlayer().getUniqueId().toString());
            ResultSet set = st.executeQuery();
            if(set.next()){
                return;
            }
            PreparedStatement statement = con.prepareStatement("INSERT INTO "+table+" (UUID, time) VALUES (?, 0);");
            statement.setString(1, e.getPlayer().getUniqueId().toString());
            statement.executeUpdate();
            statement.close();
            con.close();
        }catch (SQLException F){
            F.printStackTrace();
        }
    }

    @EventHandler
    public void death(EntityDeathEvent e){
        for(String str : instance.getConfig().getStringList("allowKillAnimals")){
            if(e.getEntity().getType().toString().equalsIgnoreCase(str)){
                if(e.getEntity().getKiller() != null) {
                    int xp = e.getDroppedExp();
                    e.setDroppedExp(0);
                    e.getEntity().getKiller().giveExp(xp);
                }
                if(e.getDrops() == null){
                    return;
                }
                Collection<ItemStack> drop = e.getDrops();
                for (ItemStack stack: drop) {
                    if(checkInventory(e.getEntity().getKiller(), stack)) {
                        e.getEntity().getKiller().getInventory().addItem(stack);
                    }else{
                        e.getEntity().getWorld().dropItemNaturally(e.getEntity().getKiller().getLocation(), stack);
                    }
                }
                e.getDrops().clear();
            }
        }

    }
    public Material getMinecraftItemIDMaterial(String minecraftID) {
        /**
         * @Author Pseudo
         */
        return new ItemStack(
                CraftItemStack.asNewCraftStack(
                        net.minecraft.server.v1_12_R1.Item.REGISTRY.get(
                                new MinecraftKey(minecraftID)))).getType();
    }
    private boolean checkInventory(Player p, ItemStack item) {
        if (p.getInventory().firstEmpty() >= 0 && item.getAmount() <= item.getMaxStackSize()) {
            return true;
        }
        Map<Integer, ? extends ItemStack> items = p.getInventory().all(item.getType());
        int amount = item.getAmount();
        for (ItemStack i : items.values()) {
            amount -= i.getMaxStackSize() - i.getAmount();
        }
        if (amount < 1) {
            return true;
        } else {
            return false; // more than 0 means there are items that can't be placed
        }
    }


//    AutoSelling
//    public void onSell(PlayerInteractEvent e){
//        if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
//            Player p = e.getPlayer();
//            if(e.getPlayer().isSneaking()){
//                Set<String> st = prsison.cfmg.getConfig().getConfigurationSection("Economy").getKeys(false);
//                for(String str : st){
//                    if(e.getPlayer().getInventory().contains(Material.getMaterial(str))){
//                        prsison.economy.withdrawPlayer(e.getPlayer(), prsison.cfmg.getConfig().getInt("Economy." + str));
//                    }
//                }
//                p.sendMessage("Your balance is " + prsison.economy.getBalance(p));
//            }
//        }
//    }
}
