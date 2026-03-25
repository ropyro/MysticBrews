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
    //used to store all customers in one place accessible by their unique id
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
        //initialize gameloop runnable
        gameLoop = new BukkitRunnable(){
            @Override
            public void run() {
                MysticBrews mysticBrews = MysticBrews.getInstance();
                //Tick/update the components (chairs/jukebox/brewingstands)
                mysticBrews.getComponentManager().tickComponents();

                //randomly spawn customer npc's if chairs are available
                if(mysticBrews.getComponentManager().getOpenChairCount() >= 2 && Math.random() > 0.9){
                    mysticBrews.getNpcManager().spawnCustomerNPC();
                }

                //update the active session or move through the barQueue
                if(activeSession == null){
                    AbstractCustomer nextCustomer = getNextCustomer();
                    if(nextCustomer != null){
                        activeSession = new BrewSession(nextCustomer);
                    }
                }else{
                    activeSession.update();
                }

                //update Brewce npc
                mysticBrews.getNpcManager().getBrewceNPC().updateNavigation();
            }
        };
        //Starts the runnable with a 20 tick delay, and 20 tick interval
        gameLoop.runTaskTimer(MysticBrews.getInstance(), 20l, 20l);
    }

    //Main method of adding customer's to a queue, both NPC and player.
    public void seatCustomer(UUID customerUUID, Chair chair){
        //gets the Customer object
        AbstractCustomer customer = getCustomer(customerUUID);
        //Adds to the queue
        addCustomerToQueue(customer);
        //saves their chair object
        customer.setChair(chair);
        //Makes the chair hold the customer as an active sitter so no one else can sit there
        chair.setActiveCustomer(customer);
    }

    public boolean open(){
        //Checks if brewce was created properly (all his locations are set) and if the gameloop is not currently running (already open)
        if(MysticBrews.getInstance().getNpcManager().getBrewceNPC().createBrewceNPC() != null && gameLoop == null){
            //reset the data structures
            customers = new HashTable<>(10);
            barQueue = new LinkedQueue<>();
            completedOrders = new ArrayList<>();
            this.activeSession = null;
            //start the runnable
            startGameLoop();
            return true;
        }
        return false;
    }

    public void close(){
        //check if gameloop is actually running
        if(gameLoop != null){
            //cancel the task
            gameLoop.cancel();
            gameLoop = null;
            //despawn all npcs
            MysticBrews.getInstance().getNpcManager().getActiveNPCs().forEach(npc -> {
                npc.despawn();
                npc.destroy();
            });
            //reset the chairs/jukebox/workstations
            MysticBrews.getInstance().getComponentManager().resetComponents();
        }
    }

    public boolean isOpen(){
        return gameLoop != null;
    }

    public AbstractCustomer getCustomer(UUID uuid){
        //Attemp to get customer from the hashtable
        AbstractCustomer customer = customers.get(uuid);
        //if customer null add them to the table
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
        //ignore customers already in queue
        if(barQueue.search(customer)) return;
        //ignore customers actively ordering/brewing
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
