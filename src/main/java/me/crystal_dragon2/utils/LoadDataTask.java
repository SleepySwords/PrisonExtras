package me.crystal_dragon2.utils;

import me.crystal_dragon2.prsison;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sun.jvm.hotspot.asm.Register;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoadDataTask extends BukkitRunnable{

    private Player player;

    public LoadDataTask(Player player){
        this.player = player;
    }
    public void run(){
        try {
            Connection con = prsison.getInstance().establishConnection();
            Statement statement = con.prepareStatement(
                    "SELECT * FROM 'PLAYER_DATA'"
            );
            ResultSet resultSet = statement.executeQuery("SELECT * FROM 'cryguin_testing'");
            prsison.getInstance().getLogger().info(String.valueOf(resultSet));

            statement.close();
            con.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
