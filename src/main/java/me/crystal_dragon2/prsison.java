package me.crystal_dragon2;

import me.crystal_dragon2.utils.*;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.Bukkit.getScheduler;

public class prsison extends JavaPlugin implements Listener{

    private String host, database, username, password;
    private int port;

    ArrayList<Player> explode = new ArrayList<Player>();
    ArrayList<chest> ChestInstance = new ArrayList<chest>();
    blocks block = new blocks();
    public static ConfigManager cfmg;
    public static TpManager tpmg;

    public EnchantmentExplosion ench = new EnchantmentExplosion(101, this);
    automine mine;
    public static prsison instance;

    public void onGive(Player p, double amount){
        economy.depositPlayer(p, amount);
    }

    @Override
    public void onEnable() {
        mine = new automine(this);

        this.getCommand("miner").setExecutor(mine);
        instance = this;
        onDefaultSql();

        establishConnection();
        setupPermissions();
        setupEconomy();
        new LoadDataTask(Bukkit.getPlayer("steve"));

        LoadEnchantments();
        loadConfigManager();

        setConfigs();

        this.saveConfig();
        getLogger().info("Loading Config1");

        getLogger().info("Cryguin Prison Extras Plugin is running!");
        saveConfig();

        for(String strin: getConfig().getStringList("destroyBlock")){
            getLogger().info(strin);
        }

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(ench, this);
        this.getServer().getPluginManager().registerEvents(new CountDown(Bukkit.getPlayer("Steve")), this);
        new listeners(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDisable() {
        try{
            Field byIdField = Enchantment.class.getDeclaredField("byId");
            Field byNameField = Enchantment.class.getDeclaredField("byName");

            byIdField.setAccessible(true);
            byNameField.setAccessible(true);

            HashMap<Integer,  Enchantment> byId = (HashMap<Integer, Enchantment>) byIdField.get(null);
            HashMap<String,  Enchantment> byNamebyId = (HashMap<String, Enchantment>) byNameField.get(null);

            if(byId.containsKey(ench.getId())){
                byId.remove(ench.getId());
            }
            if(byId.containsKey(ench.getName())){
                byId.remove(ench.getName());
            }
        }catch (Exception ignored){

        }
    }


    public automine giveAutoMine(){
        return mine;
    }

    public void loadConfigManager(){
        cfmg = new ConfigManager();
        cfmg.setup();
        tpmg = new TpManager();
        tpmg.setup();
    }

    public static ConfigManager getConfigManager(){
        return cfmg;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//        if(cmd.getName().equalsIgnoreCase("mine")){
//            if(sender instanceof Player) {
//                if(args.length > 1){
//
//                }
//                Player p = (Player) sender;
//                chest ch = new chest(this);
//                ChestInstance.add(ch);
//                ch.setup(p.getLocation(), 100, p);
//            }
//        }
        if(cmd.getName().equalsIgnoreCase("giveTime")){
            if(sender.hasPermission("prisonExtras.give")){
                if(args.length > 1){
                    if(args[0] != null || args[1] != null){
                        if(Bukkit.getPlayer(args[0]) == null){
                            sender.sendMessage(ChatColor.RED +"You need to pick a legit player!");
                        }
                        try {
                            String table = prsison.getPlugin(prsison.class).getConfig().getString("table");

                            establishConnection();

                            PreparedStatement statement = establishConnection().prepareStatement("UPDATE " +table+" SET time = ? WHERE UUID = ?"
                            );
                            statement.setInt(1, Integer.valueOf(args[1]));
                            statement.setString(2, Bukkit.getPlayer(args[0]).getUniqueId().toString());
                            statement.executeUpdate();
                            statement.close();

                            sender.sendMessage(ChatColor.GREEN + "Player has been give " + args[1] + " of mining time");
                        }catch (Exception e){
                            e.printStackTrace();
                            sender.sendMessage(ChatColor.RED + "Something went wrong!");
                        }
                    }
                }else{
                    sender.sendMessage("Please specify args!");
                }
            }else {
                sender.sendMessage("You don't have permissions!");
            }
        }
        if(cmd.getName().equalsIgnoreCase("select")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                for(chest ch : ChestInstance){
                    if(p.getUniqueId().equals(ch.playerId)){
                        Location test = new Location(Bukkit.getWorld("No"), 0, 0,0 );
                        if (ch.p1.equals(test) || ch.p2.equals(test)) {
                            ch.startMine(p);
                        } else {
                            System.out.println(ch.playerId.toString());
                            System.out.println("p1: " + ch.p1.toString());
                            System.out.println("p2: " + ch.p2.toString());
                            selection sel = new selection();
                            ArrayList<Block> blocks1 = sel.getBlocks(ch.p1, ch.p2);
                            System.out.println("Blocks:    " + String.valueOf(blocks1));
                            ch.blocks = blocks1;
                            ch.startMine(p);
                        }
                    }
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("block")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                for (String str : getConfig().getConfigurationSection("items").getKeys(false)) {
                    block.onCreate(p,str);
                }
            }
        }
        if(cmd.getName().equalsIgnoreCase("EnchantExplode")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(p.hasPermission("prisonExtra.EnchantExplode")){
                    if(p.getInventory().getItemInMainHand().containsEnchantment(ench)){
                        p.sendMessage(ChatColor.RED+ "You can't enchant a tool twice");
                        return true;
                    }
                    if(p.getInventory().getItemInMainHand().getType() != null || p.getInventory().getItemInMainHand().getType() != Material.AIR){
                        int enchantLevel = 1;
                        if(args.length > 0) {
                            try {
                                enchantLevel = Integer.valueOf(args[0]);
                            } catch (Exception e) {
                                e.printStackTrace();
                                p.sendMessage(ChatColor.RED + "Args have to be numbers");
                                return false;
                            }
                        }
                        ItemStack itemStack = p.getInventory().getItemInMainHand();
                        ItemMeta meta = itemStack.getItemMeta();
                        List<String> lore = new ArrayList<String>();
                        if(meta.getLore() != null){
                            for (String str : meta.getLore())
                            lore.add(str);
                        }
                        lore.add(ChatColor.GRAY + ench.getName() + " " + RomanNumber.toRoman(enchantLevel));
                        meta.setLore(lore);
                        itemStack.setItemMeta(meta);
                        itemStack.addUnsafeEnchantment(ench, enchantLevel);
                        p.sendMessage(ChatColor.GREEN + "Enchantment has been successful");
                    }
                }else{
                    p.sendMessage(ChatColor.RED + "You do not have perms!");
                }
            }
        }

        return true;
    }

    private void LoadEnchantments(){
        if(ench != null){
            return;
        }
        try{
            try{
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);

            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                Enchantment.registerEnchantment(ench);

            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy()
    {
        Bukkit.getWorld("world").setStorm(true);
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public void setConfigs(){
        List<String> items = new ArrayList<String>();
        items.add("IRON_ORE");
        items.add("DIAMOND_ORE");
        items.add("STONE");
        items.add("QUARTZ_ORE");
        items.add("EMERALD_ORE");
        items.add("REDSTONE_ORE");
        items.add("COAL_ORE");
        items.add("GOLD_ORE");
        items.add("DIRT");
        items.add("LAPIS_ORE");
        getConfig().addDefault("destroyBlock", items);

        getConfig().addDefault("host", "127.0.0.1");
        getConfig().addDefault("database", "mining_time");
        getConfig().addDefault("port", 3306);
        getConfig().addDefault("username", "root");
        getConfig().addDefault("password", null);
        getConfig().addDefault("table", "mining_time");

        List<String> animals = new ArrayList<String>();
        animals.add("COW");
        animals.add("CHICKEN");
        animals.add("SHEEP");
        animals.add("PIG");
        getConfig().addDefault("allowKillAnimals", animals);

        getConfig().addDefault("items.redstone.to", "redstone_block");
        getConfig().addDefault("items.redstone.quantity", 9);

        getConfig().addDefault("items.gold_ingot.to", "gold_block");
        getConfig().addDefault("items.gold_ingot.quantity", 9);

        getConfig().addDefault("items.diamond.to", "diamond_block");
        getConfig().addDefault("items.diamond.quantity", 9);

        getConfig().addDefault("items.gold_ingot.to", "gold_block");
        getConfig().addDefault("items.gold_ingot.quantity", 9);

        getConfig().addDefault("items.iron_ingot.to", "iron_block");
        getConfig().addDefault("items.iron_ingot.quantity", 9);

        getConfig().addDefault("items.coal.to", "coal_block");
        getConfig().addDefault("items.coal.quantity", 9);

        getConfig().addDefault("items.lapis.to", "lapis_block");
        getConfig().addDefault("items.lapis.quantity", 9);

        getConfig().options().copyDefaults(true);
        saveConfig();


        cfmg.getConfig().addDefault("Economy.Redstone", "25.0");
        cfmg.getConfig().addDefault("Economy.Gold", "50.0");
        cfmg.getConfig().addDefault("Economy.Iron", "90.0");
        cfmg.getConfig().addDefault("Economy.Diamond", "1000.0");

        cfmg.getConfig().options().copyDefaults(true);
        cfmg.saveConfigs();
    }

    public void onDefaultSql(){
        if(getConfig().getString("host") != null){
            host = getConfig().getString("host");
            getLogger().info(host);
        }else{
            host = "127.0.0.1";
        }
        if(getConfig().getString("database") != null){
            database = getConfig().getString("database");
            getLogger().info(database);
        }else{
            database = "mining_time";
        }
        if(getConfig().getString("port") != null){
            port = getConfig().getInt("port");
        }else{
            port = 3306;
        }
        if(getConfig().getString("username") != null){
            username = getConfig().getString("username");
            getLogger().info(username);
        }else{
            username = "root";
        }
        if(getConfig().getString("password") != null){
            password = getConfig().getString("password");
            getLogger().info(password);
        }else{
            password = null;
        }
    }

    private String connectionStr;

    public Connection establishConnection(){
        connectionStr = "jdbc:mysql://"+ host +"/"+database+"?user="+username+"&password="+password+"&autoReconnect=true";
        getLogger().info(connectionStr);
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(connectionStr);
            getLogger().info("Installation was succcessfull");
            return connection;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static prsison getInstance(){
        return instance;
    }

    public void onJoin(PlayerJoinEvent e){
        ItemStack st = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta =  st.getItemMeta();
        st.setItemMeta(meta);
        meta.getLore().add(ChatColor.GRAY + "Explosive Mining I");
        st.addUnsafeEnchantment(ench, 1);
        e.getPlayer().getInventory().addItem(st);
        e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0 ,0,0));
    }





}
