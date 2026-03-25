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

    private BukkitRunnable gameLoop;

    public BrewsManager(){
        customers = new HashTable<>(10);
        barQueue = new LinkedQueue<>();
        completedOrders = new ArrayList<>();
        this.activeSession = null;
        gameLoop = null;
    }

    public void startGameLoop(){
        gameLoop = new BukkitRunnable(){
            @Override
            public void run() {
                MysticBrews mysticBrews = MysticBrews.getInstance();
                mysticBrews.getComponentManager().tickComponents();

                if(mysticBrews.getComponentManager().getOpenChairCount() >= 2 && Math.random() > 0.9){
                    mysticBrews.getNpcManager().spawnCustomerNPC();
                }
                if(activeSession == null){
                    AbstractCustomer nextCustomer = getNextCustomer();
                    if(nextCustomer != null){
                        activeSession = new BrewSession(nextCustomer);
                    }
                }else{
                    activeSession.update();
                }

                mysticBrews.getNpcManager().getBrewceNPC().updateNavigation();
            }
        };
        gameLoop.runTaskTimer(MysticBrews.getInstance(), 20l, 20l);
    }

    public void seatCustomer(UUID customerUUID, Chair chair){
        AbstractCustomer customer = getCustomer(customerUUID);
        addCustomerToQueue(customer);
        customer.setChair(chair);
        chair.setActiveCustomer(customer);
    }

    public boolean open(){
        if(MysticBrews.getInstance().getNpcManager().getBrewceNPC().createBrewceNPC() != null && gameLoop == null){
            customers = new HashTable<>(10);
            barQueue = new LinkedQueue<>();
            completedOrders = new ArrayList<>();
            this.activeSession = null;
            startGameLoop();
            return true;
        }
        return false;
    }

    public void close(){
        if(gameLoop != null){
            gameLoop.cancel();
            gameLoop = null;
            MysticBrews.getInstance().getNpcManager().getActiveNPCs().forEach(npc -> {
                npc.despawn();
                npc.destroy();
            });
            MysticBrews.getInstance().getComponentManager().resetComponents();
        }
    }

    public boolean isOpen(){
        return gameLoop != null;
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

    public BrewSession getActiveSession() {
        return activeSession;
    }

    public void setActiveSession(BrewSession brewSession){
        this.activeSession = brewSession;
    }
    public AbstractCustomer getNextCustomer(){
        return barQueue.dequeue();
    }

    public void addCustomerToQueue(AbstractCustomer customer){
        if(barQueue.search(customer)) return;
        if(activeSession != null && activeSession.getCustomer() == customer) return;
        if(customer instanceof PlayerCustomer playerCustomer){
            playerCustomer.getPlayer().sendMessage("§aYou have joined the order queue!");
        }
        this.barQueue.enqueue(customer);
    }

    public void addCompletedOrder(Order order){
        this.completedOrders.add(order);
    }

    public List<Order> getCompletedOrders() {
        return completedOrders;
    }

    public void removeFromQueue(AbstractCustomer abstractCustomer){
        barQueue.remove(abstractCustomer);
    }
}
