package me.ropy.mysticbrews.util;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

//Inspired by https://github.com/AchyMake/Chairs/blob/main/src/main/java/org/achymake/chairs/handlers/EntityHandler.java
public class SittingUtil {
    public static ArmorStand spawnArmorStand(Location location) {
        var armorStand = location.getWorld().createEntity(location, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setSmall(true);
        location.getWorld().addEntity(armorStand);
        return armorStand;
    }
}
