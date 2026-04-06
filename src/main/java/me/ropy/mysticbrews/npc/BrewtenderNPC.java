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
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class BrewtenderNPC {

    private NPC npc;
    private Location spawnLoc;
    private Block cauldron;

    private BrewSession.BrewSessionState previousState;
    private Workstation currentWorkstation;
    private boolean taskStarted;

    public BrewtenderNPC() {
        this(null, null);
    }

    public BrewtenderNPC(Location spawnLoc, Block cauldron) {
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
                //navigate to cauldron location
                if(stateChanged){
                    Location location = LocationUtil.getNearestOpenSpace(cauldron);
                    npc.getNavigator().setTarget(location);
                    taskStarted = false;
                    currentWorkstation = null;
                }

                //Once brewce stops navigating
                if(!taskStarted && !npc.getNavigator().isNavigating() && currentWorkstation == null){
                    //look at cauldron & hold empty glass bottle
                    npc.faceLocation(cauldron.getLocation().add(0, -1,0));
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.GLASS_BOTTLE));

                    //Start task to navigate towards brewing station after 60 tick delay (3 seconds)
                    taskStarted = true;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            currentWorkstation = MysticBrews.getInstance().getComponentManager().getNextOpenWorkStation();
                            if(currentWorkstation != null){
                                //set navigation
                                Location location = LocationUtil.getNearestOpenSpace(currentWorkstation.getLocation().getBlock());
                                npc.getNavigator().setTarget(location);
                                //set brewce's item to bottle of water
                                npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.POTION));
                                taskStarted = false;
                            }
                        }
                    }.runTaskLater(MysticBrews.getInstance(), 60l);
                }

                if(!taskStarted && currentWorkstation != null){
                    //Once brewce stops navigating to brewing station
                    if(!npc.getNavigator().isNavigating()){
                        //loop at brewing station
                        npc.faceLocation(currentWorkstation.getLocation().clone().add(0, -1, 0));
                        //update work station component to be brewing
                        currentWorkstation.setBrewing(true);
                        //remove item from Brewce's hand as it is now in the brewing stand
                        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.AIR));
                        taskStarted = true;
                        //Start a task to run 3 seconds later to serve the potion
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                //update workstation to not brewing
                                currentWorkstation.setBrewing(false);
                                currentWorkstation = null;
                                //update brew session state
                                brewSession.proceedToServing();
                                taskStarted = false;
                            }
                        }.runTaskLater(MysticBrews.getInstance(), 60l);
                    }
                }
            }
            case SERVING -> {
                if(stateChanged){
                    //add potion to brewce's hand while serving
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, brewSession.getOrder().getBrewItems().getFirst().createItemStack());
                    //face the customer
                    npc.faceLocation(getTargetPlayer(brewSession).getLocation());
                }
            }
            case FINISHED -> {
                if(stateChanged){
                    //remove potion from brewce's hand
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
            npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "&5&l" + MysticBrews.getInstance().getBrewConfig().getBartenderName());
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
