package me.crystal_dragon2;

import me.crystal_dragon2.utils.instance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class EnchantmentExplosion extends Enchantment implements Listener {
    ArrayList<Player> explode = new ArrayList<Player>();
    prsison instance;
    public EnchantmentExplosion(int id, prsison plugin) {
        super(id);
        instance = plugin;
    }

    @EventHandler
    public void onMine(BlockBreakEvent e) {
        Random rand = new Random();
        int percent = rand.nextInt(100);
        if(percent > 75) {
            ItemStack mainHand = e.getPlayer().getInventory().getItemInHand();
            if (mainHand.containsEnchantment(this)) {
                if(!explode.contains(e.getPlayer())) {
                    new instance(e.getPlayer(), instance);
                    explode.add(e.getPlayer());
                }
                e.getBlock().getWorld().spawn(e.getBlock().getLocation(), TNTPrimed.class);
                e.getPlayer().getWorld().createExplosion(e.getBlock().getLocation(), 1 + mainHand.getEnchantmentLevel(this));
            }
        }
    }


    @Override
    public int getId(){
        return 101;
    }

    @Override
    public String getName() {
        return "Exploding Mining";
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return true;
    }
}
