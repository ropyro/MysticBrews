package me.ropy.mysticbrews.components;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.ropy.mysticbrews.customer.AbstractCustomer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;

import java.util.List;

/**
 * Stair based chair for RPG plugin spigot
 */
public class Chair implements BrewsComponent {

    private Location location;
    private BlockFace blockFace;
    private AbstractCustomer currentCustomer;
    private ArmorStand sittingStand;
    private String holoId;

    public Chair(Location location, BlockFace blockFace){
        this.location = location;
        this.blockFace = blockFace;
        this.currentCustomer = null;
        this.sittingStand = null;
        this.holoId = "BREWCHAIR_" + Math.abs(location.hashCode());
    }

    @Override
    public void tick(){
        updateHologram();
    }

    @Override
    public void init() {
        buildHologram();
    }

    private void updateHologram(){
        try{
            Hologram holo = DHAPI.getHologram(holoId);
            if(holo != null){
                if(currentCustomer == null){
                    DHAPI.setHologramLines(holo, List.of("&b&lOpen Seat", "&7(Right Click)"));
                }else{
                    DHAPI.setHologramLines(holo, List.of());
                }
            }else{
                buildHologram();
            }
        }catch (IllegalArgumentException e){}
    }

    private void buildHologram(){
        try{
            if(DHAPI.getHologram(holoId) != null)
                DHAPI.getHologram(holoId).delete();
        }catch (IllegalArgumentException e){}
        DHAPI.createHologram(holoId, location.clone().add(0.5, 2, 0.5),
                true, List.of("Open Chair", "(Right Click)"));
    }

    //Returns the exact location where a citizens NPC should stand before having their sit method called
    //This ensures they sit on the lower part of a stair versus the top
    //this was difficult to do took a lot of tinkering...
    public Location getNPCSitLoc(){
        switch (blockFace){
            case EAST -> {
                Location loc = location.clone().add(0.15, 0.5, 0.5);
                loc.setYaw(90);
                return loc;
            }
            case WEST -> {
                Location loc = location.clone().add(0.85, 0.5, 0.5);
                loc.setYaw(-90);
                return loc;
            }
            case NORTH -> {
                Location loc = location.clone().add(0.5, 0.5, 0.85);
                loc.setYaw(-180);
                return loc;
            }
            case SOUTH -> {
                Location loc = location.clone().add(0.5, 0.5, 0.15);
                loc.setYaw(180);
                return loc;
            }
        }
        return null;
    }

    public boolean isOpen(){
        return currentCustomer == null;
    }

    public void setActiveCustomer(AbstractCustomer customer){
        this.currentCustomer = customer;
    }

    public AbstractCustomer getCurrentCustomer() {
        return currentCustomer;
    }

    public Location getLocation() {
        return location;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public ArmorStand getSittingStand() {
        return sittingStand;
    }

    public void setSittingStand(ArmorStand sittingStand) {
        this.sittingStand = sittingStand;
    }

    //chair factory method
    public static Chair of(Block block){
        if(block != null && block.getBlockData() instanceof Stairs stair)
            return new Chair(block.getLocation(), stair.getFacing());
        return null;
    }
}
