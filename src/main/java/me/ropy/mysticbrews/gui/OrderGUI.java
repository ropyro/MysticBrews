package me.ropy.mysticbrews.gui;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.item.BrewItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

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
                        "# # # # # # # # #")
                .addIngredient('#', filler())
                .addIngredient('1', getPotionOption(PotionType.HEALING, "Health Pot", "heal your self darling"))
                .addIngredient('2', getPotionOption(PotionType.STRENGTH, "Muscle Pot", "grow strong with this magic elixer"))
                .addIngredient('3', getPotionOption(PotionType.LEAPING, "Buckets", "dayum u can jump higher than lebron!"))
                .build();


        currentWindow = Window.single()
                .setViewer(opener)
                .setTitle("§8§lAdmin Items")
                .setGui(gui)
                .build();

        currentWindow.addCloseHandler(new Runnable() {
            @Override
            public void run() {
                if(MysticBrews.getInstance().getBrewsManager().getActiveSession().getOrder() == null){
                    new OrderGUI(opener.getPlayer()).openWindow();
                }
            }
        });

        currentWindow.open();
    }

    private SimpleItem filler() {
        return new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" "));
    }

    private AbstractItem getPotionOption(PotionType potionType, String displayName, String description) {
        return new AbstractItem() {
            @Override
            public ItemProvider getItemProvider() {
                ItemStack potionItem = new ItemStack(Material.POTION);
                PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
                if(potionMeta != null){
                    potionMeta.setBasePotionType(potionType);
                    potionItem.setItemMeta(potionMeta);
                }

                return new ItemBuilder(potionItem).setDisplayName(displayName).addLoreLines(description);
            }

            @Override
            public void handleClick(@NotNull ClickType click, @NotNull Player p, @NotNull InventoryClickEvent e) {
                BrewItem brewItem = new BrewItem(displayName, potionType);
                MysticBrews.getInstance().getBrewsManager().getActiveSession().proceedToBrewing(brewItem);
                p.closeInventory();
                p.sendMessage("Brewce is now making your " + displayName);
            }
        };
    }
}
