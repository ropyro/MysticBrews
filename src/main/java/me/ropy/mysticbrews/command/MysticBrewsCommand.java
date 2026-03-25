package me.ropy.mysticbrews.command;

import me.ropy.mysticbrews.BrewsManager;
import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.components.BrewsJukeBox;
import me.ropy.mysticbrews.components.Chair;
import me.ropy.mysticbrews.components.Workstation;
import me.ropy.mysticbrews.customer.Order;
import me.ropy.mysticbrews.dsa.MergeSort;
import me.ropy.mysticbrews.npc.BrewceNPC;
import me.ropy.mysticbrews.npc.NPCManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysticBrewsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (!player.hasPermission("mythicbrews.admin")) {
            player.sendMessage("You do not have permission for this command...");
            return true;
        }

        if(args.length == 0){
            player.sendMessage("Invalid usage: /mysticbrews <spawnnpc/reload/report/set/add>");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand){
            case "spawnnpc" -> handleSpawnNPC(player);
            case "reload" -> handleReload(player);
            case "report" -> handleSalesReport(player);
            case "set" -> handleSet(player, args);
            case "add" -> handleAdd(player, args);
            case "open" -> handleOpen(player);
            case "close" -> handleClose(player);
        }
        return true;
    }

    //TODO: merge sort (recursion + sorting)
    private void handleSalesReport(Player player){
        BrewsManager bm = MysticBrews.getInstance().getBrewsManager();
        List<Order> completed = bm.getCompletedOrders();

        //edge case: no sales data
        if(completed.isEmpty()){
            player.sendMessage("§cError: no sales data yet!");
            return;
        }

        //put customer name + order count into table (map)
        Map<String, Integer> customerCounts = new HashMap<>();
        for(Order order : completed){
            String name = order.getCustomer().getName();
            //checks if customerCounts already had data for specific name, adds order count to their current total
            customerCounts.put(name, customerCounts.getOrDefault(name, 0) + order.getBrewItems().size());
        }

        //Setup int array to merge sort
        int[] counts = new int[customerCounts.size()];
        int index = 0;
        //fill array with customer counts
        for(int count : customerCounts.values()){
            counts[index] = count;
            index++;
        }

        //Merge sort the counts (i made a sortHighLow because regular sort is least to greatest
        MergeSort.sortHighLow(counts);

        player.sendMessage("§aSales Report: ");
        player.sendMessage(" §7- Order Count: §a" + bm.getCompletedOrders().size());
        player.sendMessage(" §7- Unique Customers: §a" + customerCounts.size());
        player.sendMessage(" §7- Top Customers: §a");
        //Not effecient, but made it possible to use merge sort int[] for this task
        int displayLimit = Math.min(counts.length, 3);
        //loops through either how many counts we have, or the limit which i set to 3 (common for leaderboards)
        for(int i = 0; i < displayLimit; i++){
            int score = counts[i];
            String found = null;
            //O(N) linear search through all the names(keys) in the customerCounts table
            for(String name : customerCounts.keySet()){
                //Checks if current name's value is equal to the current rank's score
                if(customerCounts.get(name).equals(score)){
                    //saves the name to remove it before next search
                    found = name;
                    player.sendMessage("  §f#" + (i+1) + " " + name + " §a" + score);
                    break;
                }
            }
            //if a name was found, remove it from the table
            if(found != null){
                customerCounts.remove(found);
            }
        }
    }

    private void handleOpen(Player player){
        var mb = MysticBrews.getInstance();
        BrewceNPC brewceNPC = MysticBrews.getInstance().getNpcManager().getBrewceNPC();
        if(MysticBrews.getInstance().getComponentManager().getWorkStations().isEmpty()){
            player.sendMessage("§cError: could not open, no brewing stations set");
        } else if (brewceNPC.getCauldron() == null){
            player.sendMessage("§cError: could not open, no cauldron set");
        } else if (brewceNPC.getSpawnLoc() == null){
            player.sendMessage("§cError: could not open, brewce's spawnpoint not set");
        }else if(mb.getComponentManager().getChairs().isEmpty()){
            player.sendMessage("§cError: could not open, no chairs set");
        }else if(!mb.getBrewsManager().isOpen()){
            mb.getBrewsManager().open();
            player.sendMessage("§aThe brewery is now open!");
        }else{
            player.sendMessage("§cError: the brewery is already open silly!");
        }
    }

    private void handleClose(Player player){
        var bm = MysticBrews.getInstance().getBrewsManager();
        if(bm.isOpen()){
            bm.close();
            player.sendMessage("§aThe brewery is now closed! Do /brews report to see the daily report.");
        }else{
            player.sendMessage("§cError: brews is not currently open.");
        }
    }

    private void handleSpawnNPC(Player player) {
        NPCManager npcManager = MysticBrews.getInstance().getNpcManager();
        if (npcManager.getSpawnLoc() == null) {
            player.sendMessage("§cError: you must set the NPC spawn location first");
            return;
        }
        if(MysticBrews.getInstance().getNpcManager().spawnCustomerNPC())
            player.sendMessage("§aCustomer NPC spawned!");
        else
            player.sendMessage("§cCould not spawn npc, no chairs available");
    }

    private void handleReload(Player player) {
        player.sendMessage("§7Reloading MysticBrews config...");
        MysticBrews.getInstance().getConfigLoader().reload();
        player.sendMessage("§aConfig reloaded!");
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /brew set <jukebox|npcspawn>");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "jukebox" -> {
                Block block = player.getTargetBlockExact(5);
                if (block != null && block.getType() == Material.JUKEBOX) {
                    MysticBrews.getInstance().getComponentManager().setJukeBox(new BrewsJukeBox(block.getLocation()));
                    player.sendMessage("§aJukebox location set!");
                } else {
                    player.sendMessage("§cYou must be looking at a jukebox.");
                }
            }
            case "cauldron" -> {
                Block block = player.getTargetBlockExact(5);
                if (block != null && (block.getType() == Material.WATER_CAULDRON || block.getType() == Material.CAULDRON)) {
                    MysticBrews.getInstance().getNpcManager().getBrewceNPC().setCauldron(block);
                    player.sendMessage("§aCauldron location set!");
                } else {
                    player.sendMessage("§cYou must be looking at a cauldron.");
                }
            }
            case "brewcespawn" -> {
                Location loc = player.getLocation();
                MysticBrews.getInstance().getNpcManager().getBrewceNPC().setSpawnLoc(loc);
                player.sendMessage(String.format("§aBrewce spawn set to: %.1f, %.1f, %.1f", loc.x(), loc.y(), loc.z()));
            }
            case "npcspawn" -> {
                Location loc = player.getLocation();
                MysticBrews.getInstance().getNpcManager().setSpawnLoc(loc);
                player.sendMessage(String.format("§aNPC spawn set to: %.1f, %.1f, %.1f", loc.x(), loc.y(), loc.z()));
            }
            default -> player.sendMessage("§cInvalid usage: /mysticbrews set <jukebox/cauldron/brewcespawn/npcspawn>");
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /brew add <chair>");
            return;
        }
        switch (args[1].toLowerCase()){
            case "chair" -> {
                Block block = player.getTargetBlockExact(5);
                Chair chair = Chair.of(block);

                if (chair == null) {
                    player.sendMessage("§cYou must look at a stair block.");
                    return;
                }

                if (MysticBrews.getInstance().getComponentManager().isChair(block.getLocation())) {
                    player.sendMessage("§cThat is already an active chair!");
                    return;
                }

                chair.init();
                MysticBrews.getInstance().getComponentManager().addChair(chair);
                player.sendMessage("§aChair added successfully!");
            }
            case "brewingstand" -> {
                Block block = player.getTargetBlockExact(5);
                if (block != null && block.getType() == Material.BREWING_STAND) {
                    MysticBrews.getInstance().getComponentManager().addWorkstation(new Workstation(block));
                    player.sendMessage("§aBrewing stand added successfully!");
                }else{
                    player.sendMessage("§cYou must look at a brewing stand block.");
                }
            }
        }
    }
}
