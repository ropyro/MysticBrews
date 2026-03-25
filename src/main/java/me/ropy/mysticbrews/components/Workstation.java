package me.ropy.mysticbrews.components;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.ropy.mysticbrews.customer.Order;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.BrewingStand;

import java.util.List;

public class Workstation implements BrewsComponent{

    private Block brewingStand;
    private final String holoId;
    private boolean brewing;
    private Order currentOrder;

    public Workstation(Block brewingStand){
        this.brewingStand = brewingStand;
        this.holoId = "BREWSTATION_" + Math.abs(brewingStand.getLocation().hashCode());
        brewing = false;
        currentOrder = null;
    }

    @Override
    public void tick() {
        updateHologram();
    }

    @Override
    public void init() {
        buildHologram();
    }

    public void setBrewing(boolean brewing){
        this.brewing = brewing;
        if(brewingStand.getType() != Material.BREWING_STAND) return;

        BrewingStand data = (BrewingStand) brewingStand.getBlockData();
        data.setBottle(0, brewing);
        data.setBottle(1, brewing);
        data.setBottle(2, brewing);

        brewingStand.setBlockData(data);
    }

    private void updateHologram() {
        try {
            Hologram holo = DHAPI.getHologram(holoId);
            if (holo != null) {
                if(brewing){
                    DHAPI.setHologramLines(holo, List.of("&a&lBrewing..."));
                }else{
                    DHAPI.setHologramLines(holo, List.of());
                }
            } else {
                buildHologram();
            }
        } catch (IllegalArgumentException e) {
        }
    }

    private void buildHologram() {
        try {
            if (DHAPI.getHologram(holoId) != null)
                DHAPI.getHologram(holoId).delete();
        } catch (IllegalArgumentException e) {
        }
        DHAPI.createHologram(holoId, getLocation().clone().add(0.5, 1.2, 0.5),
                true, List.of());
    }

    public boolean isBrewing() {
        return brewing;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public Location getLocation() {
        return brewingStand.getLocation();
    }
}
