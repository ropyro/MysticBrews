package me.ropy.mysticbrews.npc;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.components.Chair;
import me.ropy.mysticbrews.customer.NPCCustomer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SitTrait;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NPCManager {

    private Location spawnLoc;

    private List<NPC> activeNPCs;
    private BrewtenderNPC brewtenderNPC;

    private static final String[] NAMES = {"Judith", "Mark", "Vorkor", "Jebadiah", "Horus", "Keeki", "Merigda"};
    private static final String[] SKIN_NAMES = {"bunny_bunny_1", "Rain47", "Calvael", "Cainan_Bolacha", "Yatyx", "Tw_PoloThePanda"};

    public NPCManager() {
        this.activeNPCs = new ArrayList<>();
        this.brewtenderNPC = new BrewtenderNPC();
    }

    public void setSpawnLoc(Location location) {
        this.spawnLoc = location;
    }

    public boolean spawnCustomerNPC() {
        if (spawnLoc == null) return false;

        Chair targetChair = MysticBrews.getInstance().getComponentManager().getRandomOpenChair();
        if (targetChair == null) return false;

        NPC npc = getRandomCustomer();
        npc.spawn(spawnLoc);

        activeNPCs.add(npc);

        Location npcloc = npc.getStoredLocation();
        npcloc.getWorld().spawnParticle(Particle.POOF, npcloc, 1);

        NPCCustomer customer = new NPCCustomer(npc, targetChair);
        targetChair.setActiveCustomer(customer);

        npc.getNavigator().setTarget(targetChair.getNPCSitLoc());

        new BukkitRunnable() {
            int tickCount = 0;

            @Override
            public void run() {
                if (!npc.getNavigator().isNavigating()) {
                    this.cancel();
                    npc.teleport(targetChair.getNPCSitLoc(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    npc.getOrAddTrait(SitTrait.class).setSitting(targetChair.getNPCSitLoc());
                    MysticBrews.getInstance().getBrewsManager().seatCustomer(npc.getUniqueId(), targetChair);
                    return;
                }
                if (tickCount++ > 300) {
                    npc.destroy();
                    targetChair.setActiveCustomer(null);
                    this.cancel();
                }
            }
        }.runTaskTimer(MysticBrews.getInstance(), 20l, 5l);
        return true;
    }

    public void returnToSpawnLoc(NPC npc) {
        if (spawnLoc == null || npc == null) return;
        npc.getOrAddTrait(SitTrait.class).setSitting(null);

        npc.getNavigator().setTarget(spawnLoc);
        new BukkitRunnable() {
            int tickCount = 0;

            @Override
            public void run() {
                if (!npc.getNavigator().isNavigating()) {
                    this.cancel();
                    npc.despawn();
                    npc.destroy();
                    spawnLoc.getWorld().spawnParticle(Particle.POOF, spawnLoc, 1);
                    activeNPCs.remove(npc);
                    return;
                }
                if (tickCount++ > 300) {
                    npc.destroy();
                    this.cancel();
                }
            }
        }.runTaskTimer(MysticBrews.getInstance(), 40l, 5l);
    }

    private NPC getRandomCustomer() {
        NPC customer = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Customer");
        customer.setName("&a" + NAMES[(int) (Math.random() * NAMES.length)]);
        customer.getOrAddTrait(SkinTrait.class).setSkinName(SKIN_NAMES[(int) (Math.random() * SKIN_NAMES.length)]);
        return customer;
    }

    public void removeActiveNPCs(){
        activeNPCs.forEach(npc -> {
            npc.despawn();
            npc.destroy();
        });
    }
    public List<NPC> getActiveNPCs() {
        return activeNPCs;
    }

    public BrewtenderNPC getBrewceNPC() {
        return brewtenderNPC;
    }

    public void setBrewceNPC(BrewtenderNPC brewtenderNPC) {
        this.brewtenderNPC = brewtenderNPC;
        activeNPCs.add(brewtenderNPC.getNpc());
    }

    public Location getSpawnLoc() {
        return spawnLoc;
    }
}
