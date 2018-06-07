package me.crystal_dragon2.utils;

import me.crystal_dragon2.prsison;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class TpManager {

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

        playersfile = new File(plugin.getDataFolder(), "tp.yml");

        if(!playersfile.exists()){
            try{
                playersfile.createNewFile();
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "File created tp.yml");
            }catch (IOException e){
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not create tp.yml");
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
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "saved tp.yml");
        }catch (IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save tp.yml");
        }
    }
    public void loadConfigs(){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "tp.yml Loaded");
        playerscfg = YamlConfiguration.loadConfiguration(playersfile);
    }
}
