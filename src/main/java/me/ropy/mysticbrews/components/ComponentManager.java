package me.ropy.mysticbrews.components;

import me.ropy.mysticbrews.dsa.LinkedQueue;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ComponentManager {

    private BrewsJukeBox jukeBox;
    private LinkedQueue<BrewsJukeBox.MusicDisc> songQueue;
    private List<Chair> chairs;

    public ComponentManager(){
        songQueue = new LinkedQueue<>();
        chairs = new ArrayList<>();
    }

    public void tickComponents(){
        chairs.forEach(c -> c.tick());
        if(jukeBox != null)
            jukeBox.tick();
    }

    public List<Chair> getChairs(){
        return chairs;
    }

    public void addChair(Chair chair){
        this.chairs.add(chair);
    }

    public boolean isChair(Location location){
        for(Chair chair : chairs){
            if(chair.getLocation().equals(location)) return true;
        }
        return false;
    }

    public Chair getRandomOpenChair(){
        return chairs.stream().filter(chair -> chair.isOpen()).findAny().orElse(null);
    }

    public int getOpenChairCount(){
        int counter = 0;
        for(Chair chair : chairs)
            if(chair.isOpen()) counter++;
        return counter;
    }
    public BrewsJukeBox getJukeBox(){
        return jukeBox;
    }

    public void setJukeBox(BrewsJukeBox jukeBox) {
        this.jukeBox = jukeBox;
    }

    public BrewsJukeBox.MusicDisc getNextSong(){
        return songQueue.dequeue();
    }

    public void addSongToQueue(BrewsJukeBox.MusicDisc musicDisc){
        this.songQueue.enqueue(musicDisc);
    }
}
