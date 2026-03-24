package me.ropy.mysticbrews.customer;

import me.ropy.mysticbrews.item.BrewItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Tab {

    private List<BrewItem> order;

    public Tab() {
        order = new ArrayList<>();
    }

    public void addItem(BrewItem brewType) {
        order.add(brewType);
    }

    public double getBalance() {
        return 0;
    }
}
