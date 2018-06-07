//package me.crystal_dragon2.utils;
//
//import me.crystal_dragon2.prsison;
//import me.crystal_dragon2.selection;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.block.Block;
//import org.bukkit.entity.EntityType;
//import org.bukkit.entity.Player;
//import org.bukkit.event.Listener;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.UUID;
//
//public class Old implements Listener{
//
//    public ArrayList<Block> blocks =  new ArrayList<Block>();
//    miningAndBlocks mine = new miningAndBlocks();
//    public UUID playerId;
//    int task;
//    int times;
//    int time;
//    public Location p1 = new Location(Bukkit.getWorld("world") , 0, 0, 0);
//    public Location p2 = new Location(Bukkit.getWorld("world") , 0, 0, 0);
//
//    public Location old;
//
//
//    public void setup(Location location, int time, Player p){
//        old = location;
//        this.playerId = p.getUniqueId();
//        this.time = time;
//    }
//    /**if(time <= 0){
//     *  give.sendMessage(ChatColor.RED + "You're autominer time is up!");
//     *  cancel();
//     *  //todo Telaport to spawn!
//     * }
//     * time--;
//     **/
//    public void startMine(final Player give){
//        if(p1.equals(new Location(Bukkit.getWorld("world") , 0, 0, 0)) ||
//                p2.equals(new Location(Bukkit.getWorld("world") , 0, 0, 0))){
//            Location location = give.getLocation();
//            for(int x = (int) location.getX() - 10; x <= (int) location.getX() + 10; x++) {
//                for(int y = (int) location.getY() - 10; y <= (int) location.getY(); y++) {
//                    for(int z = (int) location.getZ() - 10; z <= (int) location.getZ() + 10; z++) {
//                        if(!location.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR)) {
//                            for(String st : prsison.getInstance().getConfig().getStringList("destroyBlock")){
//                                if(location.getWorld().getBlockAt(x,y,z).getType().equals(Material.getMaterial(st))){
//                                    blocks.add(location.getWorld().getBlockAt(x, y, z));
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }else{
//            selection sel = new selection();
//            blocks = sel.getBlocks(p1, p2);
//        }
//        give.sendMessage(String.valueOf(blocks.size()));
//        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(prsison.getInstance(), new Runnable() {
//            @Override
//            public void run() {
//                Title.sendActionBar(Bukkit.getPlayer(playerId), ChatColor.GOLD + "AutoMiner Time Remaining: " + time);
//                if(time <= 0){
//                    give.sendMessage(ChatColor.RED + "You're autominer time is up!");
//                    cancel();
//                    //todo Telaport to spawn!
//                }
//                for(Block b : blocks){
//                    if(b.getType().equals(Material.AIR)){
//                        blocks.remove(b);
//                    }
//                }
////                if(blocks.size() == 1){
////                    if(blocks.get(0).getType().equals(Material.AIR)){
////                        blocks.remove(0);
////                    }
////                }
//                if(blocks.size() <= 0){
//                    stop(give);
//                }
//
//                give.sendMessage("Blocks Left: " + blocks.size());
//
//                Block b = blocks.get(times).getWorld().getBlockAt(blocks.get(0).getLocation());
//                mine.onMine(give, b, 5);
//                give.sendMessage("Time: " + times);
//                time--;
//            }
//        }, 0L, 20L);
//    }
//
//
////    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
////        if(sender instanceof Player){
////            Player p = (Player) sender;
////            if(p.getUniqueId().equals(playerId)){
////                if(cmd.getName() == "select") {
////                    if (p1.equals(null) || p2.equals(null)) {
////                        p.sendMessage(ChatColor.RED + "Please select the selection!");
////                    } else {
////                        selection sel = new selection();
////                        ArrayList blocks1 = sel.getBlocks(p1, p2);
////                        p.sendMessage(String.valueOf(blocks1));
////                    }
////                }
////            }
////        }
////        return true;
////    }
//
//    public void stop(Player player){
//        try{
//            String table = prsison.getPlugin(prsison.class).getConfig().getString("table");
//            PreparedStatement statement = prsison.getInstance().getConnection().prepareStatement("UPDATE" +table+" SET time = ? WHERE UUID = ?");
//            statement.setInt(1, time);
//            statement.setString(2, player.getUniqueId().toString());
//            statement.executeUpdate();
//        }catch(SQLException e){
//            e.printStackTrace();
//        }
//        Bukkit.getScheduler().cancelTask(task);
//        p1 = new Location(Bukkit.getWorld("world"), 0, 0,0);
//        p2 = new Location(Bukkit.getWorld("world"), 0, 0,0);
//        player.sendMessage(ChatColor.GOLD + "Mining has been finished!");
//    }
//    public void cancel(){
//        try{
//            String table = prsison.getPlugin(prsison.class).getConfig().getString("table");
//            PreparedStatement statement = prsison.getInstance().getConnection().prepareStatement("UPDATE "+table+" SET time = ? WHERE UUID = ?");
//            statement.setInt(1, time);
//            statement.setString(2, String.valueOf(playerId));
//            statement.executeUpdate();
//        }catch(SQLException e){
//            e.printStackTrace();
//        }
//        Bukkit.getScheduler().cancelTask(task);
//        if(!Bukkit.getPlayer(playerId).isOnline()) {
//            Bukkit.getOfflinePlayer(playerId).getPlayer().teleport(old);
//        }
//        Bukkit.getPlayer(playerId).teleport(old);
//    }
//}