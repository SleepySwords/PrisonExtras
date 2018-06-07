package me.crystal_dragon2.utils;

import me.crystal_dragon2.prsison;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class chest{

    public ArrayList<Block> blocks =  new ArrayList<Block>();
    miningAndBlocks mine = new miningAndBlocks();
    public UUID playerId;
    int task;
    int times;
    public Location p1 = new Location(Bukkit.getWorld("world") , 0, 0, 0);
    public Location p2 = new Location(Bukkit.getWorld("world") , 0, 0, 0);

    public Location old;


    public void setup(Location location, int time, Player p){
        old = location;
        this.playerId = p.getUniqueId();
    }
    /**if(time <= 0){
     *  give.sendMessage(ChatColor.RED + "You're autominer time is up!");
     *  cancel();
     *  //todo Telaport to spawn!
     * }
     * time--;
     **/

    public void startMine(final Player give){
        final miningAndBlocks check = new miningAndBlocks();
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(prsison.getInstance(), new Runnable() {
            @Override
            public void run() {
                Block b = Bukkit.getWorld("world").getBlockAt(500, 1, 1000);
                b.setType(Material.CONCRETE);
                b.setData((byte) 1);
                if(b.getDrops(Bukkit.getPlayer(playerId).getInventory().getItemInMainHand()).size() < 1){
                    Bukkit.getPlayer(playerId).sendMessage(ChatColor.RED + "You need to be holding a pickaxe");
                }else {
                    if (give.getInventory().getItemInMainHand().getType() != null || give.getInventory().getItemInMainHand().getType().getMaxDurability() != 0) {
                        int pe;
                        int max = give.getInventory().getItemInMainHand().getType().getMaxDurability();
                        int now = give.getInventory().getItemInMainHand().getDurability();
                        if (give.getInventory().getItemInMainHand().containsEnchantment(Enchantment.DURABILITY)) {
                            pe = 100 / (give.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DURABILITY) + 1);
                        } else {
                            pe = 100;
                        }
                        Random random = new Random();
                        int rand = random.nextInt(99) - 1;
                        if ((rand < pe) || (rand == pe)) {
                            give.getInventory().getItemInMainHand().setDurability((short) (give.getInventory().getItemInMainHand().getDurability() + 1));
                        }

                        if (max - now < 1 || give.getInventory().getItemInMainHand().getDurability() == 0) {
                            give.getInventory().getItemInMainHand().setType(Material.AIR);
                        }
                    }
                    if (check.checkInventory(give, new ItemStack(Material.CONCRETE))) {
                        for (ItemStack st : b.getDrops(Bukkit.getPlayer(playerId).getInventory().getItemInMainHand())) {
                            Bukkit.getPlayer(playerId).getInventory().addItem(st);

                        }
                    }else {
                        Player p = Bukkit.getPlayer(playerId);
//                        int amountItems = 0;
//                        if (Bukkit.getPlayer(playerId).getInventory() != null) {
//                            for (ItemStack item : Bukkit.getPlayer(playerId).getInventory()) {
//                                if(item != null) {
//                                    if (item.getType().equals(Material.CONCRETE)) {
//                                        amountItems = amountItems + item.getAmount();
//                                        Bukkit.getPlayer(playerId).getInventory().remove(item);
//                                    }
//                                }
//                            }
//                        }
//                        int amount = amountItems * 5;
//                        if(!(amountItems <= 0)) {
//                            prsison.getInstance().onGive(Bukkit.getPlayer(playerId), amount);
//                            Bukkit.getPlayer(playerId).sendMessage(ChatColor.GREEN + "You sold " + amountItems + " block and earned $" + amount);
//                        }else{
//                            Bukkit.getPlayer(playerId).sendMessage(ChatColor.RED + "Please remove some items from your inventory");
//                        }
                        Bukkit.getPlayer(playerId).sendMessage(ChatColor.RED + "Please remove some items from your inventory");
                        ItemStack st = new ItemStack(Material.CONCRETE);
                        st.setDurability((short) 1);
                        Bukkit.getPlayer(playerId).getWorld().dropItemNaturally(Bukkit.getPlayer(playerId).getLocation(), st);
                    }
                }
            }
        }, 0L, 40L);
    }

    public void cancel(int time){
        try{
            String table = prsison.getPlugin(prsison.class).getConfig().getString("table");
            Connection con = prsison.getInstance().establishConnection();
            PreparedStatement statement = con.prepareStatement("UPDATE "+ table +" SET time = ? WHERE UUID = ?");
            statement.setInt(1, time);
            statement.setString(2, String.valueOf(playerId));
            statement.executeUpdate();
            statement.close();
            con.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        Bukkit.getScheduler().cancelTask(task);
        if(!Bukkit.getPlayer(playerId).isOnline()) {
            Bukkit.getOfflinePlayer(playerId).getPlayer().teleport(old);
        }
        Bukkit.getPlayer(playerId).teleport(old);
    }
}