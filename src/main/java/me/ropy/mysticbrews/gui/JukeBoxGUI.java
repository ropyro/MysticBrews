package me.ropy.mysticbrews.gui;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.components.BrewsJukeBox;
import me.ropy.mysticbrews.item.BrewItem;
import org.bukkit.Material;
import org.bukkit.Sound;
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

public class JukeBoxGUI {

    private final Player opener;
    private Window currentWindow;

    public JukeBoxGUI(Player opener) {
        this.opener = opener;
    }

    public void openWindow() {
        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# 1 2 3 4 5 6 7 #",
                        "# # # # # # # # #")
                .addIngredient('#', filler())
                .addIngredient('1', getMusicDiscOption(Material.MUSIC_DISC_13, "13", Sound.MUSIC_DISC_13, 178*20))
                .addIngredient('2', getMusicDiscOption(Material.MUSIC_DISC_CAT, "Cat", Sound.MUSIC_DISC_CAT, 185*20))
                .addIngredient('3', getMusicDiscOption(Material.MUSIC_DISC_BLOCKS, "Blocks", Sound.MUSIC_DISC_BLOCKS, 345*20))
                .addIngredient('4', getMusicDiscOption(Material.MUSIC_DISC_CHIRP, "Chirp", Sound.MUSIC_DISC_CHIRP, 185*20))
                .addIngredient('5', getMusicDiscOption(Material.MUSIC_DISC_FAR, "Far", Sound.MUSIC_DISC_FAR, 174*20))
                .addIngredient('6', getMusicDiscOption(Material.MUSIC_DISC_MALL, "Mall", Sound.MUSIC_DISC_MALL, 197*20))
                .addIngredient('7', getMusicDiscOption(Material.MUSIC_DISC_MELLOHI, "Mellohi", Sound.MUSIC_DISC_MELLOHI, 103*20))
                .build();


        currentWindow = Window.single()
                .setViewer(opener)
                .setTitle("§8§lJuke Box")
                .setGui(gui)
                .build();

        currentWindow.open();
    }

    private SimpleItem filler() {
        return new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" "));
    }

    private AbstractItem getMusicDiscOption(Material material, String title, Sound sound, int duration) {
        return new AbstractItem() {
            @Override
            public ItemProvider getItemProvider() {
                return new ItemBuilder(material)
                        .setDisplayName("§b§l" + title)
                        .addLoreLines("§7Duration: §f" + (duration / 60) + "m " + (duration % 60) + "s",
                                " ",
                                "§e§l» §fClick to Queue");
            }

            @Override
            public void handleClick(@NotNull ClickType click, @NotNull Player p, @NotNull InventoryClickEvent e) {
                BrewsJukeBox.MusicDisc disc = new BrewsJukeBox.MusicDisc(title, material, p.getName(), sound, duration);
                MysticBrews.getInstance().getComponentManager().addSongToQueue(disc);
                p.sendMessage("§6Jukebox: §fAdded §b" + title + " §fto the queue!");
                p.closeInventory();
            }
        };
    }
}
