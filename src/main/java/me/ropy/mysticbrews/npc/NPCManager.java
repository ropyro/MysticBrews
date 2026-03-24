package me.ropy.mysticbrews.npc;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.components.Chair;
import me.ropy.mysticbrews.customer.NPCCustomer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SitTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCManager {

    private Location spawnLoc;

    public void setSpawnLoc(Location location){
        this.spawnLoc = location;
    }

    public void spawnCustomerNPC() {
        if(spawnLoc == null) return;

        Chair targetChair = MysticBrews.getInstance().getComponentManager().getRandomOpenChair();
        if (targetChair == null) return;

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Customer_" + (Math.random() * 1000));
        npc.spawn(spawnLoc);

        Location npcloc = npc.getStoredLocation();
        npcloc.getWorld().spawnParticle(Particle.POOF, npcloc, 1);

        NPCCustomer customer = new NPCCustomer(npc, targetChair);
        targetChair.setActiveCustomer(customer);

        npc.getNavigator().setTarget(targetChair.getNPCSitLoc());

        new BukkitRunnable() {
            int tickCount = 0;
            @Override
            public void run() {
                if(!npc.getNavigator().isNavigating()){
                    this.cancel();
                    npc.teleport(targetChair.getNPCSitLoc(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    npc.getOrAddTrait(SitTrait.class).setSitting(targetChair.getNPCSitLoc());
                    MysticBrews.getInstance().getBrewsManager().seatCustomer(npc.getUniqueId(), targetChair);
                    return;
                }
                if(tickCount++ > 300){
                    npc.destroy();
                    targetChair.setActiveCustomer(null);
                    this.cancel();
                }
            }
        }.runTaskTimer(MysticBrews.getInstance(), 20l, 5l);
    }

    public void returnToSpawnLoc(NPC npc){
        Bukkit.broadcastMessage(npc.getName() + " is Returning to spawn loc");
        if(spawnLoc == null || npc == null) return;
        Bukkit.broadcastMessage(npc.getName() + " is Returning to spawn loc1");
        npc.getOrAddTrait(SitTrait.class).setSitting(null);

        npc.getNavigator().setTarget(spawnLoc);
        new BukkitRunnable() {
            int tickCount = 0;
            @Override
            public void run() {
                if(!npc.getNavigator().isNavigating()){
                    this.cancel();
                    npc.despawn();
                    npc.destroy();
                    spawnLoc.getWorld().spawnParticle(Particle.POOF, spawnLoc, 1);
                    return;
                }
                if(tickCount++ > 300){
                    npc.destroy();
                    this.cancel();
                }
            }
        }.runTaskTimer(MysticBrews.getInstance(), 40l, 5l);
    }

    public Location getSpawnLoc() {
        return spawnLoc;
    }
}
