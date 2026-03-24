package me.ropy.mysticbrews.customer;

import me.ropy.mysticbrews.components.Chair;
import org.bukkit.entity.Player;

public class PlayerCustomer extends AbstractCustomer {

    private Player player;

    public PlayerCustomer(Player player, Chair chair){
        super(chair);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String getName() {
        return player.getName();
    }
}
