package me.ropy.mysticbrews.item;

import me.ropy.mysticbrews.MysticBrews;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class BrewItem {

    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(MysticBrews.getInstance(), "brew_item_id");

    private String displayName;
    private PotionType potionType;

    public BrewItem(String displayName, PotionType potionType) {
        this.displayName = displayName;
        this.potionType = potionType;
    }

    public ItemStack createItemStack() {
        ItemStack potionItem = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
        if (potionMeta != null) {
            potionMeta.setBasePotionType(potionType);
            potionItem.setItemMeta(potionMeta);
        }
        return potionItem;
    }

    protected void consumeItem(Player player, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }

    public String getDisplayName() {
        return displayName;
    }
}
