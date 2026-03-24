package me.ropy.mysticbrews.components;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.ropy.mysticbrews.MysticBrews;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class BrewsJukeBox extends BrewsComponent {

    private Location location;
    private String holoId;
    private MusicDisc currentsong;
    private int songTicksLeft;

    public BrewsJukeBox(Location location) {
        this.location = location;
        this.holoId = "BREWJUKEBOX_" + Math.abs(location.hashCode());
        currentsong = null;
        songTicksLeft = 0;
    }

    @Override
    public void tick() {
        if (songTicksLeft <= 0) {
            playNextSong();
        }
        if(currentsong != null){
            songTicksLeft--;
        }
        updateHologram();
    }

    @Override
    public void init() {
        buildHologram();
    }

    private void playNextSong() {
        if (currentsong != null)
            for (Player player : location.getWorld().getPlayers()) {
                if (player.getLocation().distance(location) < 32) {
                    player.stopSound(currentsong.sound);
                }
            }
        MusicDisc next = MysticBrews.getInstance().getComponentManager().getNextSong();
        if (next != null) {
            //this.songTicksLeft = next.durationTicks / 20;
            this.songTicksLeft = 5*20;
            location.getWorld().playSound(location, next.sound, 4.0f, 1.0f);
            this.currentsong = next;
        } else {
            this.currentsong = null;
        }
    }

    private void updateHologram() {
        try {
            Hologram holo = DHAPI.getHologram(holoId);
            if (holo != null) {
                if (currentsong == null) {
                    DHAPI.setHologramLines(holo, List.of("&6&lJuke Box", "&7(Right Click to Queue!)"));
                } else {
                    DHAPI.setHologramLines(holo, List.of(
                            "&b&lNow Playing:",
                            "&f" + currentsong.title(),
                            "&7Queued by: &b" + currentsong.queuerName()
                    ));
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
        DHAPI.createHologram(holoId, location.clone().add(0.5, 2, 0.5),
                true, List.of("&6&lJuke Box", "&7(Right Click to Queue!)"));
    }

    public Location getLocation() {
        return location;
    }

    public record MusicDisc(String title, Material material, String queuerName, Sound sound, int durationTicks) {
    }
}
