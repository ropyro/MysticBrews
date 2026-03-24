package me.ropy.mysticbrews.command;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.components.Chair;
import org.bukkit.Location;
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
        } else if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("spawnnpc")) {
                if(MysticBrews.getInstance().getNpcManager().getSpawnLoc() == null){
                    player.sendMessage("Error: you must set the NPC spawn location first");
                }else{
                    MysticBrews.getInstance().getNpcManager().spawnCustomerNPC();
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                player.sendMessage("Reloading MythicBrews config...");
                MysticBrews.getInstance().getConfigLoader().reload();
                player.sendMessage("Config reloaded!");
            } else if (args[0].equalsIgnoreCase("report")) {
                player.sendMessage("Daily Report:");
                //TODO: gen daily report with ordered stats using merge sort/
            } else if (args[0].equalsIgnoreCase("set")) {
                if (args.length == 1) {
                    player.sendMessage("Invalid usage");
                } else if (args[1].equalsIgnoreCase("bartender")) {

                } else if (args[1].equalsIgnoreCase("jukebox")) {

                } else if (args[1].equalsIgnoreCase("npcspawn")) {
                    Location playerLoc = player.getLocation();
                    MysticBrews.getInstance().getNpcManager().setSpawnLoc(playerLoc);
                    player.sendMessage("Set npc spawn loc to: " + playerLoc.x() + " " + playerLoc.y() + " " + playerLoc.z());
                } else if (args[1].equalsIgnoreCase("leaderboard")) {

                }
            } else if (args[0].equalsIgnoreCase("add")) {
                if (args.length == 1) {
                    player.sendMessage("Invalid usage");
                } else if (args[1].equalsIgnoreCase("chair")) {
                    Block lookingBlock = player.getTargetBlockExact(5);
                    Chair chair = Chair.of(lookingBlock);
                    if (chair != null) {
                        if (MysticBrews.getInstance().getComponentManager().isChair(lookingBlock.getLocation())) {
                            player.sendMessage("That already is an active chair!");
                        } else {
                            chair.init();
                            MysticBrews.getInstance().getComponentManager().addChair(chair);
                            Location blockLoc = lookingBlock.getLocation();
                            player.sendMessage("Chair added facing: " + chair.getBlockFace().name() + "at: " + blockLoc.x() + " " + blockLoc.y() + " " + blockLoc.z());

                        }
                    } else {
                        player.sendMessage("You must be looking at a stair block to add as a chair.");
                    }
                } else if (args[1].equalsIgnoreCase("brewingstand")) {

                }
            }
        } else {
            player.sendMessage("invalid usage...");
        }
        return true;
    }
}
