package me.crystal_dragon2.utils;


import me.crystal_dragon2.prsison;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class instance implements Listener{
    Player player;

    public instance(Player p, prsison plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.player = p;
    }
    @EventHandler
    public void onExplosionEvent(BlockExplodeEvent e){
        if(e.getBlock().getType() != null || e.getBlock().getType() != Material.AIR) return;

        if(e.blockList().size() == 0){
            return;
        }
        for (Block b : e.blockList()) {
            onGive(player, b);
        }
        if(player != null) {
            for (Block b : e.blockList()) {
                b.setType(Material.AIR);
            }
        }
        e.getBlock().getWorld().createExplosion(e.getBlock().getLocation(), 0);
        e.setCancelled(true);
    }

    public void onGive(Player p, Block b){
        int multiply = 1;
        int fortune = 0;
        if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
            multiply = 1;
            if (p.getPlayer().getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 1) {
                Random rand = new Random();
                fortune = 1;
                int random = rand.nextInt(100);
                if(random > 67){
                    multiply = 2;
                }
            }
            if (p.getPlayer().getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 2) {
                Random rand = new Random();
                fortune = 2;
                int random = rand.nextInt(100);
                if(random < 25){
                    multiply = 2;
                }else if(random > 75){
                    multiply = 3;
                }
            }
            if (p.getPlayer().getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 3) {
                Random rand = new Random();
                fortune = 3;
                int random = rand.nextInt(100);
                if(random < 20){
                    multiply = 2;
                }else if(random > 80){
                    multiply = 3;
                }else if(random < 80 && random > 60){
                    multiply = 4;
                }
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
        Collection<ItemStack> drop = b.getDrops();
        if(!(p.hasPermission("prisonExtra.autoSmelt"))){
            return;
        }
        for (ItemStack stack : drop) {
            for (int i=0; i < multiply; i+=1) {
                ItemStack stackL = stack;
                if(p.hasPermission("prisonExtra.AutoPickup")){
                    if(p.hasPermission("prisonExtra.AutoSmelt")){
                        if(stack.getType().equals(Material.IRON_ORE)){
                            stackL = new ItemStack(Material.IRON_INGOT);
                        }else if(stack.getType().equals(Material.GOLD_ORE)) {
                            stackL = new ItemStack(Material.GOLD_INGOT);
                        }
                    }

                    if(checkInventory(player, stackL)){
                        p.getInventory().addItem(stackL);
                    }else{
                        p.getWorld().dropItemNaturally(p.getLocation(), stackL);
                    }
                }else{
                    if(p.hasPermission("prisonExtra.AutoSmelt")) {
                        if(stack.getType().equals(Material.IRON_ORE)){
                            stackL = new ItemStack(Material.IRON_INGOT);
                        }else if(stack.getType().equals(Material.GOLD_ORE)) {
                            stackL = new ItemStack(Material.GOLD_INGOT);
                        }
                    }
                    b.getWorld().dropItemNaturally(b.getLocation(), stackL);
                }
            }

        }
        return;
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
