package me.ropy.mysticbrews.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class LocationUtil {

    public static Location getNearestOpenSpace(Block block){
        Location blockLoc = block.getLocation();
        if(block.getWorld().getBlockAt(blockLoc.clone().add(1, 0 ,0)).getType() == Material.AIR){
            return blockLoc.clone().add(1, 0 ,0);
        }
        if(block.getWorld().getBlockAt(blockLoc.clone().add(-1, 0 ,0)).getType() == Material.AIR){
            return blockLoc.clone().add(-1, 0 ,0);
        }
        if(block.getWorld().getBlockAt(blockLoc.clone().add(0, 0 ,1)).getType() == Material.AIR){
            return blockLoc.clone().add(0, 0 ,1);
        }
        if(block.getWorld().getBlockAt(blockLoc.clone().add(0, 0 ,-1)).getType() == Material.AIR){
            return blockLoc.clone().add(0, 0 ,-1);
        }
        return null;
    }
}
