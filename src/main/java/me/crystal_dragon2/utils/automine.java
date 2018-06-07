package me.crystal_dragon2.utils;

import me.crystal_dragon2.prsison;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class automine implements Listener, CommandExecutor {
    Map<Player, chest> playerchestMap = new HashMap<Player, chest>();
    Map<Player, CountDown> counts = new HashMap<Player, CountDown>();



    public automine(prsison plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void onTimeUp(Player p){
        playerchestMap.remove(p);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(cmd.getName().equalsIgnoreCase("miner")){
                if(args.length > 0){
                    if(args[0].equalsIgnoreCase("leave")){
                        removePlayer(p);
                    }
                }else{
                    if(playerInMine(p)){
                        p.sendMessage(ChatColor.RED+ "You are already in a mine!");
                        return true;
                    }
                    openGui(p);
                }
            }
        }
        return true;
    }

    public void openGui(Player p){
        Inventory myInventory = Bukkit.createInventory(null, 9, "Server Selector!");
        int items = 0;
        for(String st : prsison.tpmg.getConfig().getConfigurationSection("Auto").getKeys(false)) {
            ItemStack s1 = new ItemStack(Material.CHEST);
            ItemMeta m1 = s1.getItemMeta();
            m1.setDisplayName(ChatColor.GREEN + st);
            s1.setItemMeta(m1);
            myInventory.setItem(items, s1);
            items++;
        }
        p.openInventory(myInventory);
    }

    @EventHandler
    public void onInv(InventoryClickEvent e){

        Player player = (Player) e.getWhoClicked();

        ClickType click = e.getClick();
        if(e.getCurrentItem().equals(null) || e.getCurrentItem().getType().equals(Material.AIR)){
            return;
        }
        Inventory open = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();

        if (open.getName().equalsIgnoreCase("Server Selector!")) {
            if(item == null || !item.hasItemMeta()){
                return;
            }
            for (String st : prsison.tpmg.getConfig().getConfigurationSection("Auto").getKeys(false)) {
                if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(st))) {

                    addPlayer(player, st);
                    e.getWhoClicked().closeInventory();
                }
            }
            e.setCancelled(true);
        }
    }

    public boolean playerInMine(Player p){
        if(playerchestMap.containsKey(p)){
            return true;
        }else {
            return false;
        }
    }

    public void addPlayer(Player p, String st){
        Location loc = new Location(Bukkit.getWorld(prsison.tpmg.getConfig().getString("Auto." + st + ".world")),
                prsison.tpmg.getConfig().getInt("Auto." + st + ".x"),
                prsison.tpmg.getConfig().getInt("Auto." + st + ".y"),
                prsison.tpmg.getConfig().getInt("Auto." + st + ".z"));
        int time = 0;
        if(playerchestMap.containsKey(p)){
            p.sendMessage(ChatColor.RED  + "You are already in a mine!");
            return;
        }
        try {
            String table = prsison.getInstance().getConfig().getString("table");
            Connection con = prsison.getInstance().establishConnection();
            PreparedStatement statement = con.prepareStatement("SELECT * FROM " +table +" WHERE UUID = ?");
            statement.setString(1, p.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                time = resultSet.getInt("time");
            }else{
                time = 0;
            }
            statement.close();
            con.close();
        }catch (SQLException e){
            time = 0;
            e.printStackTrace();
        }
        if(time == 0 ){
            p.sendMessage(ChatColor.RED + "You have no time!");
            return;
        }

        openGui(p);
        chest ch = new chest();
        CountDown count1 = new CountDown(Bukkit.getPlayer(p.getUniqueId()));
        count1.count(p, time, ch);
        counts.put(p, count1);
        ch.setup(p.getLocation(), time, p);
        ch.startMine(p);
        playerchestMap.put(p, ch);
        p.teleport(loc);
    }
    public void removePlayer(Player p){
        if(playerchestMap.containsKey(p)) {
            Bukkit.getScheduler().cancelTask(counts.get(p).task);
            playerchestMap.get(p).cancel(counts.get(p).time);
            playerchestMap.remove(p);
            counts.remove(p);
            p.sendMessage(ChatColor.GREEN + "You have been removed!");
        }else {
            p.sendMessage(ChatColor.RED + "You are not in a mine!");
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event){
        if(playerchestMap.containsKey(event.getPlayer())){
            removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if(playerchestMap.containsKey(event.getPlayer())){
            removePlayer(event.getPlayer());
        }
    }
}
