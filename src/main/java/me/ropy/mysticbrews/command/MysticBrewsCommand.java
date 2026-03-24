package me.ropy.mysticbrews.command;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.components.BrewsJukeBox;
import me.ropy.mysticbrews.components.Chair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
            case "report" -> player.sendMessage("TODO: daily report...");
            case "set" -> handleSet(player, args);
            case "add" -> handleAdd(player, args);
        }
        return true;
    }

    private void handleSpawnNPC(Player player) {
        if (MysticBrews.getInstance().getNpcManager().getSpawnLoc() == null) {
            player.sendMessage("§cError: you must set the NPC spawn location first");
            return;
        }
        MysticBrews.getInstance().getNpcManager().spawnCustomerNPC();
        player.sendMessage("§aCustomer NPC spawned!");
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
            case "npcspawn" -> {
                Location loc = player.getLocation();
                MysticBrews.getInstance().getNpcManager().setSpawnLoc(loc);
                player.sendMessage(String.format("§aNPC spawn set to: %.1f, %.1f, %.1f", loc.x(), loc.y(), loc.z()));
            }
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /brew add <chair>");
            return;
        }

        if (args[1].equalsIgnoreCase("chair")) {
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
    }
}
