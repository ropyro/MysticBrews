package me.ropy.mysticbrews.components;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.ropy.mysticbrews.MysticBrews;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class BrewsJukeBox implements BrewsComponent {

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
        if(songTicksLeft > 0)
            songTicksLeft--;

        if (songTicksLeft <= 0) {
            if(currentsong != null)
                stopCurrentMusic();
            playNextSong();
        }
        updateHologram();
    }

    @Override
    public void init() {
        buildHologram();
    }

    private void playNextSong() {
        MusicDisc next = MysticBrews.getInstance().getComponentManager().getNextSong();
        if (next != null) {
            stopCurrentMusic();
            //this.songTicksLeft = next.durationTicks / 20;
            this.songTicksLeft = 5;
            location.getWorld().playSound(location, next.sound, 10.0f, 1.0f);
            this.currentsong = next;
        } else {
            this.currentsong = null;
        }
    }

    private void stopCurrentMusic() {
        if (currentsong != null) {
            for (Player player : location.getWorld().getPlayers()) {
                if (player.getLocation().distance(location) < 32) {
                    player.stopSound(currentsong.sound);
                }
            }
        }
    }

    public void reset(){
        stopCurrentMusic();
        currentsong = null;
        songTicksLeft = 0;
    }

    private void updateHologram() {
        try {
            Hologram holo = DHAPI.getHologram(holoId);
            if (holo != null) {
                if (currentsong == null) {
                    DHAPI.setHologramLines(holo, List.of("&6&lJuke Box", "&7(Right Click to Queue!)"));
                } else {
                    DHAPI.setHologramLines(holo, List.of(
                            "&6&lNow Playing:",
                            "&f" + currentsong.title(),
                            "&7Queued by: &6" + currentsong.queuerName()
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
