package me.ropy.mysticbrews;

import me.ropy.mysticbrews.components.Chair;
import me.ropy.mysticbrews.customer.AbstractCustomer;
import me.ropy.mysticbrews.customer.NPCCustomer;
import me.ropy.mysticbrews.customer.Order;
import me.ropy.mysticbrews.customer.PlayerCustomer;
import me.ropy.mysticbrews.dsa.HashTable;
import me.ropy.mysticbrews.dsa.LinkedQueue;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BrewsManager {

    //TODO: Hash Table
    private HashTable<UUID, AbstractCustomer> customers;
    //TODO: Linked Lists & Queue
    private LinkedQueue<AbstractCustomer> barQueue;
    private List<Order> completedOrders;
    private BrewSession activeSession;

    public BrewsManager(){
        customers = new HashTable<>(10);
        barQueue = new LinkedQueue<>();
        completedOrders = new ArrayList<>();
        this.activeSession = null;
    }

    public void seatCustomer(UUID customerUUID, Chair chair){
        AbstractCustomer customer = getCustomer(customerUUID);
        addCustomerToQueue(customer);
        customer.setChair(chair);
        chair.setActiveCustomer(customer);
    }

    public void dismounCustomer(UUID customerUUID, Chair chair){
        chair.setSittingStand(null);
        chair.setActiveCustomer(null);
        removeFromQueue(customerUUID);
    }

    public void startGameLoop(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(MysticBrews.getInstance().getComponentManager().getOpenChairCount() >= 2 && Math.random() > 0.9){
                    MysticBrews.getInstance().getNpcManager().spawnCustomerNPC();
                }
                if(activeSession == null){
                    AbstractCustomer nextCustomer = getNextCustomer();
                    if(nextCustomer != null){
                        activeSession = new BrewSession(nextCustomer);
                    }
                }else{
                    activeSession.update();
                }
            }
        }.runTaskTimer(MysticBrews.getInstance(), 0, 20l);
    }

    public AbstractCustomer getCustomer(UUID uuid){
        AbstractCustomer customer = customers.get(uuid);
        if(customer == null){
            Player player = Bukkit.getPlayer(uuid);
            if(player != null){
                customer = new PlayerCustomer(player, null);
                customers.put(player.getUniqueId(), customer);
            }else{
                NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
                if(npc != null){
                    customer = new NPCCustomer(npc, null);
                    customers.put(npc.getUniqueId(), customer);
                }
            }
        }
        return customer;
    }


    //TODO: SEARCHING
    private boolean chargeCustomer(Order order){
        return true;
    }


    public BrewSession getActiveSession() {
        return activeSession;
    }

    public void setActiveSession(BrewSession brewSession){
        this.activeSession = brewSession;
    }

    public void addCustomerToQueue(AbstractCustomer customer){
        this.barQueue.enqueue(customer);
    }

    public AbstractCustomer getNextCustomer(){
        return barQueue.dequeue();
    }

    public void removeFromQueue(UUID uuid){
        barQueue.remove(getCustomer(uuid));
    }
}
