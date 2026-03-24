package me.ropy.mysticbrews.listener;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.components.Chair;
import me.ropy.mysticbrews.customer.AbstractCustomer;
import me.ropy.mysticbrews.util.SittingHelper;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChairListeners implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getPlayer().getVehicle() != null) return;
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if(block != null){
            for(Chair chair : MysticBrews.getInstance().getComponentManager().getChairs()){
                if(chair.getLocation().equals(block.getLocation()) && chair.isOpen()){
                    var bm = MysticBrews.getInstance().getBrewsManager();
                    bm.seatCustomer(player.getUniqueId(), chair);

                    event.getPlayer().teleport(chair.getNPCSitLoc());
                    ArmorStand armorStand = SittingHelper.spawnArmorStand(chair.getNPCSitLoc().clone().add(0, -1,0));
                    armorStand.addPassenger(player);
                    chair.setSittingStand(armorStand);

                    player.sendMessage("You have joined the bar queue! Brewce will serve you pronto..");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDismounted().getType().equals(EntityType.ARMOR_STAND))) return;
        for (Chair chair : MysticBrews.getInstance().getComponentManager().getChairs()) {
            if(chair.getSittingStand() != null && chair.getSittingStand().equals(event.getDismounted())){
                MysticBrews.getInstance().getBrewsManager().dismounCustomer(event.getEntity().getUniqueId(), chair);

                event.getEntity().sendMessage("You have left the bar queue");
                //run later so the player dismounts properly
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getDismounted().remove();
                    }
                }.runTaskLater(MysticBrews.getInstance(), 20l);
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(player.getVehicle() != null){
            Entity vehicle = player.getVehicle();
            for(Chair chair : MysticBrews.getInstance().getComponentManager().getChairs()){
                if(chair.getSittingStand().equals(vehicle)){
                    vehicle.remove();
                    MysticBrews.getInstance().getBrewsManager().dismounCustomer(player.getUniqueId(), chair);
                }
            }
        }
    }
}
