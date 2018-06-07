package me.crystal_dragon2.utils;

import me.crystal_dragon2.prsison;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    public prsison plugin = prsison.getPlugin(prsison.class);

    //Files & File Configs here
    public FileConfiguration playerscfg;
    public File playersfile;
    public File playersdir;

    public void setup(){
        //Economy
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }

        playersfile = new File(plugin.getDataFolder(), "Economy.yml");

        if(!playersfile.exists()){
            try{
                playersfile.createNewFile();
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "File created Economy.yml");
            }catch (IOException e){
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not create Economy.yml");
            }
        }
        playerscfg = YamlConfiguration.loadConfiguration(playersfile);
    }

    public FileConfiguration getConfig(){
        return playerscfg;
    }

    public void saveConfigs(){
        try{
            playerscfg.save(playersfile);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "saved Economy.yml");
        }catch (IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save Economy.yml");
        }
    }
    public void loadConfigs(){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Economy.yml Loaded");
        playerscfg = YamlConfiguration.loadConfiguration(playersfile);
    }
}
