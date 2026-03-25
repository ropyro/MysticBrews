package me.ropy.mysticbrews.gui;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.item.BrewItem;
import me.ropy.mysticbrews.item.BrewRegistry;
import me.ropy.mysticbrews.item.price.BrewPrice;
import me.ropy.mysticbrews.item.price.EcoPrice;
import me.ropy.mysticbrews.item.price.ItemPrice;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class OrderGUI {

    private final Player opener;
    private Window currentWindow;

    public OrderGUI(Player opener) {
        this.opener = opener;
    }

    public void openWindow() {
        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# 1 2 3 4 5 6 7 #",
                        "# # # # X # # # #")
                .addIngredient('#', filler())
                .addIngredient('1', getPotionOption(BrewRegistry.getById("health")))
                .addIngredient('2', getPotionOption(BrewRegistry.getById("strength")))
                .addIngredient('3', getPotionOption(BrewRegistry.getById("jumpboost")))
                .addIngredient('4', getPotionOption(BrewRegistry.getById("speed")))
                .addIngredient('5', getPotionOption(BrewRegistry.getById("regen")))
                .addIngredient('6', getPotionOption(BrewRegistry.getById("fireres")))
                .addIngredient('7', getPotionOption(BrewRegistry.getById("nightvision")))
                .addIngredient('X', getExitIngredient())
                .build();


        currentWindow = Window.single()
                .setViewer(opener)
                .setTitle("§8Order a mystical brew:")
                .setGui(gui)
                .build();

        currentWindow.addCloseHandler(new Runnable() {
            @Override
            public void run() {
                if(MysticBrews.getInstance().getBrewsManager().getActiveSession().getOrder() == null)
                    MysticBrews.getInstance().getBrewsManager().getActiveSession().proceedToBrewing(null);
            }
        });

        currentWindow.open();
    }

    private SimpleItem filler() {
        return new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" "));
    }

    private AbstractItem getExitIngredient(){
        return new AbstractItem() {
            @Override
            public ItemProvider getItemProvider() {
                return new ItemBuilder(new ItemStack(Material.BARRIER)).setDisplayName("§cExit Queue");
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
                MysticBrews.getInstance().getBrewsManager().setActiveSession(null);
                player.closeInventory();
            }
        };
    }

    private AbstractItem getPotionOption(BrewItem brewItem) {
        return new AbstractItem() {
            @Override
            public ItemProvider getItemProvider() {
                ItemStack potionItem = new ItemStack(Material.POTION);

                ItemMeta itemMeta1 = potionItem.getItemMeta();
                itemMeta1.lore(List.of());
                potionItem.setItemMeta(itemMeta1);

                PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
                if(potionMeta != null){
                    potionMeta.setBasePotionType(brewItem.getPotionType());
                    potionItem.setItemMeta(potionMeta);
                }
                ItemBuilder builder = new ItemBuilder(potionItem).setDisplayName("§f§l" + brewItem.getDisplayName()).addLoreLines(" ");

                BrewPrice brewPrice = brewItem.getPrice();
                if(brewPrice instanceof ItemPrice itemPrice){
                    builder.addLoreLines("§aPrice: " + itemPrice.getAmount() + " " + itemPrice.getMaterial());
                }
                if(brewPrice instanceof EcoPrice ecoPrice){
                    builder.addLoreLines("§aPrice: $" + ecoPrice.getAmount());
                }
                builder.addLoreLines(" ");
                if(brewPrice.canAfford(opener.getPlayer())){
                    builder.addLoreLines("§a§lClick to order!");
                }else{
                    builder.addLoreLines("§c§lYou cannot afford this item!");
                }
                return builder;
            }

            @Override
            public void handleClick(@NotNull ClickType click, @NotNull Player p, @NotNull InventoryClickEvent e) {
                if(brewItem.getPrice().canAfford(p)){
                    brewItem.getPrice().charge(p);
                    MysticBrews.getInstance().getBrewsManager().getActiveSession().proceedToBrewing(brewItem);
                    p.closeInventory();
                    p.sendMessage("§5§lBrewce §ais now making your §l" + brewItem.getDisplayName());
                }
            }
        };
    }
}
