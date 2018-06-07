package me.crystal_dragon2.utils;

import me.crystal_dragon2.prsison;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class miningAndBlocks {



    public void onMine(Player p, Block b, int y){

        double max = p.getInventory().getItemInMainHand().getType().getMaxDurability();
        double now = p.getInventory().getItemInMainHand().getDurability();
        double percentage = 100 * ((max - now) / max);
        if(isTool(p.getInventory().getItemInMainHand())){

            if (y == 5) {
                if (percentage < 20) {
                    p.sendMessage(ChatColor.RED + "Your Pickaxe durability is " + (int) percentage);
                }
            }
        }
        int fortune = 0;
        if (p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
            if (p.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 1) {
                fortune = 1;
            }
            if (p.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 2) {
                fortune = 2;
            }
            if (p.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 3) {
                fortune = 3;
            }
        }
        if(b.getType().equals(Material.COAL_ORE)){
            Random ran = new Random();
            int xp = ran.nextInt(2);
            xp += 1 + fortune;
            p.giveExp(xp);
        }else if(b.getType().equals(Material.LAPIS_ORE) || b.getType().equals(Material.LAPIS_ORE)){
            Random ran = new Random();
            int xp = ran.nextInt(3);
            xp += 2 + fortune;
            p.giveExp(xp);
        }else if(b.getType().equals(Material.DIAMOND_ORE) || b.getType().equals(Material.EMERALD_ORE)){
            Random ran = new Random();
            int xp = ran.nextInt(4);
            xp += 3 + fortune;
            p.giveExp(xp);
        }else if(b.getType().equals(Material.REDSTONE_ORE)){
            Random ran = new Random();
            int xp = ran.nextInt(4);
            xp += 1 + fortune;
            p.giveExp(xp);
        }

        ItemStack st = p.getInventory().getItemInMainHand();
        if(st.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)){
            Random random = new Random();
            int i = random.nextInt(st.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) + 2) - 1;

            if (i < 0){
                i = 0;
            }
            Collection<ItemStack> l = b.getDrops(st);
            for(ItemStack srt : l){
                srt.setAmount(srt.getAmount() + i);
                if(p.hasPermission("prisonExtra.AutoPickup")){
                    if(p.hasPermission("prisonExtra.AutoSmelt")){
                        if(srt.getType().equals(Material.IRON_ORE)){
                            srt.setType(Material.IRON_INGOT);
                        }else if(srt.getType().equals(Material.GOLD_ORE)) {
                            srt.setType(Material.GOLD_INGOT);
                        }
                    }
                    if(checkInventory(p, srt)) {
                        p.getInventory().addItem(srt);
                    }else{
                        p.sendMessage(ChatColor.RED + "Your inventory is full please sell!");
                        p.getWorld().dropItemNaturally(p.getLocation(), srt);
                    }

                }else{
                    if(p.hasPermission("prisonExtra.AutoSmelt")) {
                        if(srt.getType().equals(Material.IRON_ORE)){
                            srt.setType(Material.IRON_INGOT);
                        }else if(srt.getType().equals(Material.GOLD_ORE)) {
                            srt.setType(Material.GOLD_INGOT);
                        }
                    }
                    b.getWorld().dropItemNaturally(b.getLocation(), srt);
                }
            }
        }else{
            Collection<ItemStack> l = b.getDrops(st);
            for(ItemStack srt : l){
                if(p.hasPermission("prisonExtra.AutoPickup")){
                    if(p.hasPermission("prisonExtra.AutoSmelt")){
                        if(srt.getType().equals(Material.IRON_ORE)){
                            srt.setType(Material.IRON_INGOT);
                        }else if(srt.getType().equals(Material.GOLD_ORE)) {
                            srt.setType(Material.GOLD_INGOT);
                        }
                    }
                    if(checkInventory(p, srt)) {
                        p.getInventory().addItem(srt);
                    }else{
                        p.sendMessage(ChatColor.RED + "You need to clear your inventory!");
                        p.getWorld().dropItemNaturally(p.getLocation(), srt);
                    }

                }else{
                    if(p.hasPermission("prisonExtra.AutoSmelt")) {
                        if(srt.getType().equals(Material.IRON_ORE)){
                            srt.setType(Material.IRON_INGOT);
                        }else if(srt.getType().equals(Material.GOLD_ORE)) {
                            srt.setType(Material.GOLD_INGOT);
                        }
                    }
                    b.getWorld().dropItemNaturally(b.getLocation(), srt);
                }
            }
        }



        if(isTool(p.getInventory().getItemInMainHand())) {
            if (p.getInventory().getItemInMainHand().getType() != null || p.getInventory().getItemInMainHand().getType().getMaxDurability() != 0) {
                int pe;
                if (p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.DURABILITY)) {
                    pe = 100 / (p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DURABILITY) + 1);
                } else {
                    pe = 100;
                }
                Random random = new Random();
                int rand = random.nextInt(99) - 1;
                if (y != 5) {
                    if ((rand < pe) || (rand == pe)) {
                        p.getInventory().getItemInMainHand().setDurability((short) (p.getInventory().getItemInMainHand().getDurability() + 1));
                    }
                }
                if (max - now < 1 || p.getInventory().getItemInMainHand().getDurability() == 0) {
                    p.getInventory().getItemInMainHand().setType(Material.AIR);
                }
            }
        }
        b.setType(Material.AIR);
        return;
    }

    public boolean checkInventory(Player p, ItemStack item) {
        if (p.getInventory().firstEmpty() >= 0 && item.getAmount() <= item.getMaxStackSize()) {
            return true;
        }
        if(p.getInventory() != null) {
            int itemR = 0;
            int maxItems = 0;

            for (ItemStack st : p.getInventory()) {
                if (st != null) {
                    if (st.getType().equals(item.getType())) {
                        itemR += st.getAmount();
                        maxItems += st.getMaxStackSize();
//                        if (item.getAmount() < st.getMaxStackSize()) {
//                            return true;
//                        }
                    }
                }
            }
            if(itemR !=0 || maxItems != 0){
                if(maxItems - itemR >= item.getAmount()){
                    return true;
                }else{
                    return false;
                }
            }
        }
//        Map<Integer, ? extends ItemStack> items = p.getInventory().all(item.getType());
//        int amount = item.getAmount();
//        for (ItemStack i : items.values()) {
//            amount -= i.getMaxStackSize() - i.getAmount();
//        }
//        if(amount < 1){
//            return true;
//        }else {
//            return false; // more than 0 means there are items that can't be placed
//        }
        return false;
    }

    public boolean isTool(ItemStack st){
        if(st.getType().equals(Material.DIAMOND_PICKAXE)){
            return true;
        }
        if(st.getType().equals(Material.IRON_PICKAXE)){
            return true;
        }
        if(st.getType().equals(Material.STONE_PICKAXE)){
            return true;
        }
        if(st.getType().equals(Material.WOOD_PICKAXE)){
            return true;
        }
        if(st.getType().equals(Material.GOLD_PICKAXE)){
            return true;
        }
        if(st.getType().equals(Material.DIAMOND_SPADE)){
            return true;
        }
        if(st.getType().equals(Material.GOLD_SPADE)){
            return true;
        }
        if(st.getType().equals(Material.IRON_SPADE)){
            return true;
        }
        if(st.getType().equals(Material.WOOD_SPADE)){
            return true;
        }

        if(st.getType().equals(Material.DIAMOND_AXE)){
            return true;
        }
        if(st.getType().equals(Material.GOLD_AXE)){
            return true;
        }
        if(st.getType().equals(Material.IRON_AXE)){
            return true;
        }
        if(st.getType().equals(Material.WOOD_AXE)){
            return true;
        }

        if(st.getType().equals(Material.DIAMOND_HOE)){
            return true;
        }
        if(st.getType().equals(Material.GOLD_HOE)){
            return true;
        }
        if(st.getType().equals(Material.IRON_HOE)){
            return true;
        }
        if(st.getType().equals(Material.WOOD_HOE)){
            return true;
        }

        if(st.getType().equals(Material.DIAMOND_SWORD)){
            return true;
        }
        if(st.getType().equals(Material.WOOD_SWORD)){
            return true;
        }
        if(st.getType().equals(Material.WOOD_SWORD)){
            return true;
        }
        if(st.getType().equals(Material.WOOD_SWORD)){
            return true;
        }
        return false;
    }
}
