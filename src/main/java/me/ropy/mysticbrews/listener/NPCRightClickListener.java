package me.ropy.mysticbrews.listener;

import me.ropy.mysticbrews.BrewsManager;
import me.ropy.mysticbrews.MysticBrews;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCRightClickListener implements Listener {

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event){
        if(event.getNPC().equals(MysticBrews.getInstance().getNpcManager().getBrewceNPC().getNpc())){
            BrewsManager bm =  MysticBrews.getInstance().getBrewsManager();
            bm.addCustomerToQueue(bm.getCustomer(event.getClicker().getUniqueId()));
        }
    }
}
