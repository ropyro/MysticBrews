package me.ropy.mysticbrews.util;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class LocationUtil {

    public static Location getNearestOpenSpace(Block block) {
        int[][] directions = {{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
        for (int[] dir : directions) {
            Block targetBlock = block.getRelative(dir[0], 0, dir[2]);
            if (isPassable(targetBlock) && isPassable(targetBlock.getRelative(0, 1, 0))) {
                return targetBlock.getLocation().add(0.5, 0, 0.5);
            }
        }
        return block.getLocation();
    }

    private static boolean isPassable(Block b) {
        return b.getType().isAir() || b.isPassable();
    }
}
