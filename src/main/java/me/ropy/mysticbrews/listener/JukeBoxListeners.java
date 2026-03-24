package me.ropy.mysticbrews.listener;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.components.BrewsJukeBox;
import me.ropy.mysticbrews.gui.JukeBoxGUI;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class JukeBoxListeners implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        BrewsJukeBox brewsJukeBox = MysticBrews.getInstance().getComponentManager().getJukeBox();
        if(brewsJukeBox != null && brewsJukeBox.getLocation().equals(block.getLocation())){
            new JukeBoxGUI(event.getPlayer()).openWindow();
        }
    }
}
