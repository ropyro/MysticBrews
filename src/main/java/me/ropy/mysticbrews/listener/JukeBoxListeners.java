package me.ropy.mysticbrews.listener;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.components.BrewsJukeBox;
import me.ropy.mysticbrews.gui.JukeBoxGUI;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class JukeBoxListeners implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if(block == null) return;

        BrewsJukeBox brewsJukeBox = MysticBrews.getInstance().getComponentManager().getJukeBox();
        if(brewsJukeBox != null && brewsJukeBox.getLocation().equals(block.getLocation())){
            if(!MysticBrews.getInstance().getBrewsManager().isOpen()){
                event.getPlayer().sendMessage("§cMystic brews is closed! So is our jukebox lol..");
                return;
            }
            new JukeBoxGUI(event.getPlayer()).openWindow();
        }
    }
}
