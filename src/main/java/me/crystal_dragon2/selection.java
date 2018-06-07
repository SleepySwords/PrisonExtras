package me.crystal_dragon2;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class selection {

    public ArrayList<Block> getBlocks(Location loc1, Location loc2){
        ArrayList<Block> block = new ArrayList<Block>();

        int minX;
        int minY;
        int minZ;
        int maxX;
        int maxY;
        int maxZ;

        if(loc1.getBlockX() > loc2.getBlockX()){
            maxX = loc1.getBlockX();
            minX = loc2.getBlockX();
        }else{
            minX = loc1.getBlockX();
            maxX = loc2.getBlockX();
        }

        if(loc1.getBlockY() > loc2.getBlockY()){
            maxY = loc1.getBlockY();
            minY = loc2.getBlockY();
        }else{
            minY = loc1.getBlockY();
            maxY = loc2.getBlockY();
        }

        if(loc1.getBlockZ() > loc2.getBlockZ()){
            maxZ = loc1.getBlockZ();
            minZ = loc2.getBlockZ();
        }else{
            minZ = loc1.getBlockZ();
            maxZ = loc2.getBlockZ();
        }

        for (int y = minY; y < maxY + 1; y++) {
            for (int x = minX; x < maxX + 1 ; x++) {
                for (int z = minZ; z < maxZ + 1; z++) {
                    if(!loc1.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR) || loc1.getWorld().getBlockAt(x,y,z).getType().equals(Material.BEDROCK)) {
                        for(String st : prsison.getInstance().getConfig().getStringList("destroyBlock")){
                            if(loc1.getWorld().getBlockAt(x,y,z).getType().equals(Material.getMaterial(st))){
                                block.add(loc1.getWorld().getBlockAt(x, y, z));
                            }
                        }
                    }
                }
            }
        }
        return block;
    }

}
