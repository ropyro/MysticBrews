package me.ropy.mysticbrews.npc;

import me.ropy.mysticbrews.BrewSession;
import me.ropy.mysticbrews.BrewsManager;
import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.components.Workstation;
import me.ropy.mysticbrews.customer.NPCCustomer;
import me.ropy.mysticbrews.customer.PlayerCustomer;
import me.ropy.mysticbrews.util.LocationUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BrewceNPC {

    private NPC npc;
    private Location spawnLoc;
    private Block cauldron;

    private BrewSession.BrewSessionState previousState;
    private Workstation currentWorkstation;
    private boolean taskStarted;

    public BrewceNPC() {
        this(null, null);
    }

    public BrewceNPC(Location spawnLoc, Block cauldron) {
        this.spawnLoc = spawnLoc;
        this.cauldron = cauldron;
        this.npc = null;

        previousState = BrewSession.BrewSessionState.FINISHED;
        currentWorkstation = null;
        taskStarted = false;
    }

    public void updateNavigation() {
        BrewsManager brewsManager = MysticBrews.getInstance().getBrewsManager();
        if (npc == null) return;
        BrewSession brewSession = brewsManager.getActiveSession();
        if (brewSession == null) return;

        BrewSession.BrewSessionState currentState = brewSession.getState();
        boolean stateChanged = currentState != previousState;

        switch (currentState) {
            case ORDERING -> {
                if(stateChanged){
                    Player target = getTargetPlayer(brewSession);
                    if (target != null){
                        lookAtTarget(target);
                    }
                }
            }
            case BREWING -> {
                if(stateChanged){
                    Location location = LocationUtil.getNearestOpenSpace(cauldron);
                    npc.getNavigator().setTarget(location);
                    taskStarted = false;
                    currentWorkstation = null;
                }

                if(!taskStarted && !npc.getNavigator().isNavigating() && currentWorkstation == null){
                    npc.faceLocation(cauldron.getLocation().add(0, -1,0));
                    taskStarted = true;
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.GLASS_BOTTLE));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            currentWorkstation = MysticBrews.getInstance().getComponentManager().getNextOpenWorkStation();
                            if(currentWorkstation != null){
                                Location location = LocationUtil.getNearestOpenSpace(currentWorkstation.getLocation().getBlock());
                                npc.getNavigator().setTarget(location);
                                taskStarted = false;
                                npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.POTION));
                            }
                        }
                    }.runTaskLater(MysticBrews.getInstance(), 60l);
                }

                if(!taskStarted && currentWorkstation != null){
                    if(!npc.getNavigator().isNavigating()){
                        npc.faceLocation(currentWorkstation.getLocation().clone().add(0, -1, 0));
                        currentWorkstation.setBrewing(true);
                        taskStarted = true;
                        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.AIR));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                currentWorkstation.setBrewing(false);
                                currentWorkstation = null;
                                brewSession.proceedToServing();
                                taskStarted = false;
                            }
                        }.runTaskLater(MysticBrews.getInstance(), 60l);
                    }
                }
            }
            case SERVING -> {
                if(stateChanged){
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, brewSession.getOrder().getBrewItems().getFirst().createItemStack());
                    npc.faceLocation(getTargetPlayer(brewSession).getLocation());
                }
            }
            case FINISHED -> {
                if(stateChanged){
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.AIR));
                }
            }
        }
        previousState = currentState;
    }

    private Player getTargetPlayer(BrewSession brewSession){
        Player target = null;
        if (brewSession.getCustomer() instanceof PlayerCustomer playerCustomer) {
            target = playerCustomer.getPlayer();
        } else if (brewSession.getCustomer() instanceof NPCCustomer npcCustomer
                && npcCustomer.getNpc().getEntity() instanceof Player npcPlayer) {
            target = npcPlayer;
        }
        return target;
    }

    public void lookAtTarget(Player player) {
        npc.faceLocation(player.getLocation());
    }

    public NPC createBrewceNPC() {
        NPC npc = null;
        if (spawnLoc != null && cauldron != null && !MysticBrews.getInstance().getComponentManager().getWorkStations().isEmpty()) {
            npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "&5&lBrewce");
            npc.getOrAddTrait(SkinTrait.class).setSkinName("Kalkulater");
            npc.spawn(spawnLoc);
            MysticBrews.getInstance().getNpcManager().getActiveNPCs().add(npc);
        }
        this.npc = npc;
        return npc;
    }

    public NPC getNpc() {
        return npc;
    }

    public Location getSpawnLoc() {
        return spawnLoc;
    }

    public void setSpawnLoc(Location spawnLoc) {
        this.spawnLoc = spawnLoc;
    }

    public Block getCauldron() {
        return cauldron;
    }

    public void setCauldron(Block cauldron) {
        this.cauldron = cauldron;
    }
}
