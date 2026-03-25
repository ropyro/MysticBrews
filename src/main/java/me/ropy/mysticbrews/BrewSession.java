package me.ropy.mysticbrews;

import me.ropy.mysticbrews.customer.AbstractCustomer;
import me.ropy.mysticbrews.customer.NPCCustomer;
import me.ropy.mysticbrews.customer.Order;
import me.ropy.mysticbrews.customer.PlayerCustomer;
import me.ropy.mysticbrews.gui.OrderGUI;
import me.ropy.mysticbrews.item.BrewItem;
import me.ropy.mysticbrews.item.price.EcoPrice;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

public class BrewSession {

    private BrewSessionState state;
    private AbstractCustomer customer;
    private Order order;

    private int updateCounter;
    private boolean openedGui;

    public BrewSession(AbstractCustomer customer) {
        this(customer, BrewSessionState.ORDERING);
    }

    //overloaded constructor planned for persistence across restarts not currently used
    public BrewSession(AbstractCustomer customer, BrewSessionState brewSessionState) {
        this.customer = customer;
        this.state = brewSessionState;
        updateCounter = 0;
        this.order = null;
        openedGui = false;
    }

    //called once per second (20 mc ticks) while session is active
    public void update() {
        //Bukkit.broadcastMessage(customer.getName() + "'s session is in state: " + state.name());
        switch (state) {
            case ORDERING -> {
                if (customer instanceof PlayerCustomer playerCustomer) {
                    //open order gui if player has not yet
                    if (!openedGui) {
                        Player player = playerCustomer.getPlayer();
                        new OrderGUI(player).openWindow();
                        openedGui = true;
                    }
                    //NPC customers just delay 2 seconds before proceeding to brewing
                } else if (++updateCounter >= 2) {
                    proceedToBrewing(new BrewItem("NPCPotion", PotionType.HARMING, new EcoPrice(10.0d)));
//                    updateCounter = 0;
//                    this.state = BrewSessionState.BREWING;
//                    order = new Order(customer, new BrewItem("NPCPotion", PotionType.HARMING, new EcoPrice(10.0d)));
                }
            }
            case BREWING -> {
                //Go back to order state if player closed the gui without ordering or exiting queue
                if (this.order == null) {
                    this.state = BrewSessionState.ORDERING;
                    openedGui = false;
                }
            }
            case SERVING -> {
                //2 second delay for Brewce to look at customer
                if (++updateCounter >= 2) {
                    updateCounter = 0;
                    this.state = BrewSessionState.FINISHED;
                }
            }
            case FINISHED -> {
                //Give player customer the physical items from their order
                if (customer instanceof PlayerCustomer playerCustomer) {
                    Player player = playerCustomer.getPlayer();
                    //This stream method loops through all items in order, and adds them to the player's inventory
                    order.getBrewItems().stream().forEach(bi -> player.getInventory().addItem(bi.createItemStack()));
                    playerCustomer.getPlayer().sendMessage("§aHere is your order! To reenter the queue, right click Brewce!");
                //Update the customer NPC's path finding and held item
                } else if (customer instanceof NPCCustomer npcCustomer) {
                    if (npcCustomer.getNpc() != null && npcCustomer.getChair() != null) {
                        //dismount from chair
                        npcCustomer.getChair().setActiveCustomer(null);
                        //update path finding
                        MysticBrews.getInstance().getNpcManager().returnToSpawnLoc(npcCustomer.getNpc());
                        //set item in hand
                        npcCustomer.getNpc().getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, order.getBrewItems().getFirst().createItemStack());
                    }
                }
                //Update brews manager with status
                MysticBrews.getInstance().getBrewsManager().addCompletedOrder(order);
                MysticBrews.getInstance().getBrewsManager().setActiveSession(null);
            }
        }
    }

    public void proceedToBrewing(BrewItem... brewItems) {
        if (brewItems == null) {
            this.order = null;
        } else {
            this.order = new Order(customer, brewItems);
        }
        this.state = BrewSessionState.BREWING;
        updateCounter = 0;
    }

    public void proceedToServing() {
        this.state = BrewSessionState.SERVING;
        updateCounter = 0;
    }

    public Order getOrder() {
        return order;
    }

    public AbstractCustomer getCustomer() {
        return customer;
    }

    public BrewSessionState getState() {
        return state;
    }

    public enum BrewSessionState {
        ORDERING, BREWING, SERVING, FINISHED
    }
}
