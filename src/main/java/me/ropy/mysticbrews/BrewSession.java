package me.ropy.mysticbrews;

import me.ropy.mysticbrews.components.Chair;
import me.ropy.mysticbrews.customer.AbstractCustomer;
import me.ropy.mysticbrews.customer.NPCCustomer;
import me.ropy.mysticbrews.customer.Order;
import me.ropy.mysticbrews.customer.PlayerCustomer;
import me.ropy.mysticbrews.gui.OrderGUI;
import me.ropy.mysticbrews.item.BrewItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

public class BrewSession {

    private BrewSessionState state;
    private AbstractCustomer customer;
    private Chair chair;

    private Order order;

    private int updateCounter;
    private boolean guiOpened;
    private boolean startedBrewing;

    public BrewSession(AbstractCustomer customer) {
        this(customer, BrewSessionState.ORDERING);
    }

    public BrewSession(AbstractCustomer customer, BrewSessionState brewSessionState) {
        this.customer = customer;
        this.chair = customer.getChair();
        this.state = brewSessionState;
        updateCounter = 0;
        guiOpened = false;
        startedBrewing = false;
    }

    public void update() {
        Bukkit.broadcastMessage(customer.getName() + "'s session is in state: " + state.name());
        switch (state) {
            case ORDERING -> {
                if (customer instanceof PlayerCustomer playerCustomer) {
                    Player player = playerCustomer.getPlayer();
                    if (!guiOpened) {
                        new OrderGUI(player).openWindow();
                        guiOpened = true;
                    }
                    return;
                }
                if (++updateCounter >= 2) {
                    updateCounter = 0;
                    this.state = BrewSessionState.BREWING;
                    order = new Order(customer, new BrewItem("test", PotionType.HARMING));
                }
            }
            case BREWING -> {
                if (this.order == null) {
                    this.state = BrewSessionState.ORDERING;
                    guiOpened = false;
                    return;
                }
                if (!startedBrewing && customer instanceof PlayerCustomer playerCustomer) {
                    playerCustomer.getPlayer().sendMessage("Now brewing your, " + order.getBrewItems().size() + " potion order!");
                    startedBrewing = true;
                }
                if (++updateCounter >= 2) {
                    updateCounter = 0;
                    this.state = BrewSessionState.SERVING;
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
                    playerCustomer.getPlayer().sendMessage("Here is your order! To reenter the queue, right click Brewce!");
                } else if (customer instanceof NPCCustomer npcCustomer) {
                    Bukkit.broadcastMessage("Debug: Processing NPC Finish for " + npcCustomer.getName());
                    if (npcCustomer.getNpc() != null && npcCustomer.getChair() != null) {
                        npcCustomer.getChair().setActiveCustomer(null);
                        MysticBrews.getInstance().getNpcManager().returnToSpawnLoc(npcCustomer.getNpc());
                    } else {
                        Bukkit.broadcastMessage("Debug: NPC or Chair was null! NPC: " + (npcCustomer.getNpc() != null));
                    }
                }
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

    public Order getOrder() {
        return order;
    }

    public BrewSessionState getState() {
        return state;
    }

    public enum BrewSessionState {
        ORDERING, BREWING, SERVING, FINISHED
    }
}
