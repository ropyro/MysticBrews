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

    public BrewSession(AbstractCustomer customer, BrewSessionState brewSessionState) {
        this.customer = customer;
        this.state = brewSessionState;
        updateCounter = 0;
        this.order = null;
        openedGui = false;
    }

    public void update() {
        //Bukkit.broadcastMessage(customer.getName() + "'s session is in state: " + state.name());
        switch (state) {
            case ORDERING -> {
                if (customer instanceof PlayerCustomer playerCustomer) {
                    if(!openedGui){
                        Player player = playerCustomer.getPlayer();
                        new OrderGUI(player).openWindow();
                        openedGui = true;
                    }
                } else
                if (++updateCounter >= 2) {
                    updateCounter = 0;
                    this.state = BrewSessionState.BREWING;
                    order = new Order(customer, new BrewItem("test", PotionType.HARMING, new EcoPrice(10.0d)));
                }
            }
            case BREWING -> {
                if (this.order == null) {
                    this.state = BrewSessionState.ORDERING;
                    openedGui = false;
                }
            }
            case SERVING -> {
                if (++updateCounter >= 2) {
                    updateCounter = 0;
                    this.state = BrewSessionState.FINISHED;
                }
            }
            case FINISHED -> {
                if (customer instanceof PlayerCustomer playerCustomer) {
                    Player player = playerCustomer.getPlayer();
                    order.getBrewItems().stream().forEach(bi -> player.getInventory().addItem(bi.createItemStack()));
                    playerCustomer.getPlayer().sendMessage("§aHere is your order! To reenter the queue, right click Brewce!");
                } else if (customer instanceof NPCCustomer npcCustomer) {
                    if (npcCustomer.getNpc() != null && npcCustomer.getChair() != null) {
                        npcCustomer.getChair().setActiveCustomer(null);
                        MysticBrews.getInstance().getNpcManager().returnToSpawnLoc(npcCustomer.getNpc());
                        npcCustomer.getNpc().getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, order.getBrewItems().getFirst().createItemStack());
                    }
                }
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

    public void proceedToServing(){
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
