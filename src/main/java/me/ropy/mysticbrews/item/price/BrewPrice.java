package me.ropy.mysticbrews.item.price;

import org.bukkit.entity.Player;

public interface BrewPrice {

    boolean canAfford(Player player);
    void charge(Player player);
}
