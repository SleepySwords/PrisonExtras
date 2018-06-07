package me.crystal_dragon2.utils;

import me.crystal_dragon2.prsison;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.sql.*;
import java.util.UUID;

public class CountDown implements Listener{

    public Player player2;
    private prsison instance;

    int task;
    int time;

    public CountDown(Player player){
        this.player2 = player;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        //e.getPlayer().sendMessage("Hi");
        //playerCreate(e.getPlayer().getUniqueId(), e.getPlayer());
        //Timer(e.getPlayer());
    }

    public void count(final Player p, int count, final chest ch){
        this.time = count;
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(prsison.getInstance(), new Runnable() {
            @Override
            public void run() {
                Title.sendActionBar(p, ChatColor.GOLD + "AutoMiner Time Remaining: " + time);
                if(time < 1){
                    p.sendMessage(ChatColor.RED + "Your autominer time is up!");
                    ch.cancel(0);
                    prsison.getInstance().giveAutoMine().onTimeUp(p);
                    Bukkit.getScheduler().cancelTask(task);
                }
                time--;
            }
        }, 0L, 20L);

    }


    public void delaying(final int delay){
        Bukkit.getServer().getScheduler().runTaskLater(prsison.getPlugin(prsison.class), new Runnable() {
            public void run() {
                return;
            }
        }, delay);
    }

}
