package me.ropy.mysticbrews.item.price;

import me.ropy.mysticbrews.MysticBrews;
import org.bukkit.entity.Player;

public class EcoPrice implements BrewPrice {

    private double amount;

    public EcoPrice(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    //vault api to manage in game economy
    @Override
    public boolean canAfford(Player player) {
        return MysticBrews.getEconomy().has(player, amount);
    }

    @Override
    public void charge(Player player) {
        MysticBrews.getEconomy().withdrawPlayer(player, amount);
    }
}
