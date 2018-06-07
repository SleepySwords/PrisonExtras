package me.crystal_dragon2.utils;

import me.crystal_dragon2.prsison;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class blocks {
    public void onCreate(Player p, String str){
        int i = 0;
        int a = 0;
        for (ItemStack is : p.getInventory().getContents()){
            if (is != null && (is.getType() == getMinecraftItemIDMaterial(str) ||  is.getType() == Material.getMaterial(str))){
                a += is.getAmount();
            }
            if(is != null && str.equalsIgnoreCase("lapis") && is.getType().equals(Material.INK_SACK) && is.getData().getData() == 4) {
                a += is.getAmount();
            }
        }

        if(a != 0){
            i = a / prsison.getPlugin(prsison.class).getConfig().getInt("items." + str + ".quantity");
        }
        if(i != 0) {
            for (int amount = i; amount > 0; amount--) {
                ItemStack st1 = new ItemStack(Material.getMaterial(prsison.getPlugin(prsison.class).getConfig().getString("items." + str + ".to").toUpperCase()));

                if(checkInventory(p, st1)){
                    p.getInventory().addItem(st1);
                }else{
                    p.getWorld().dropItemNaturally(p.getLocation(), st1);
                }

            }
            int t = i * prsison.getPlugin(prsison.class).getConfig().getInt("items." + str + ".quantity");
            if(str.equalsIgnoreCase("lapis")){
                p.getInventory().removeItem(new ItemStack(Material.INK_SACK, t, (short) 4));
            }else {
                p.getInventory().removeItem(new ItemStack(Material.getMaterial(str.toUpperCase()), t));
                p.getInventory().removeItem(new ItemStack(getMinecraftItemIDMaterial(str.toUpperCase()), t));
            }
        }
        return;
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
}
