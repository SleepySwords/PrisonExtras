package me.crystal_dragon2.utils;

import net.minecraft.server.v1_12_R1.Blocks;
import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.IBlockState;
import net.minecraft.server.v1_12_R1.Item;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class dropHandler {
    public dropHandler(Block b, Player player){
        ItemStack st = player.getInventory().getItemInMainHand();
        if(st.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)){
            Random random = new Random();
            int i = random.nextInt(st.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) + 2) - 1;

            if (i < 0){
                i = 0;
            }

            Collection<ItemStack> l = b.getDrops(st);
            for(ItemStack srt : l){
                srt.setAmount(srt.getAmount() + i);
                player.getInventory().addItem(srt);
            }

        }
    }
}
