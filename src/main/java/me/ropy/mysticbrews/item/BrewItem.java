package me.ropy.mysticbrews.item;

import me.ropy.mysticbrews.MysticBrews;
import me.ropy.mysticbrews.item.price.BrewPrice;
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

    private BrewPrice price;

    public BrewItem(String displayName, PotionType potionType, BrewPrice price) {
        this.displayName = displayName;
        this.potionType = potionType;
        this.price = price;
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

    public String getDisplayName() {
        return displayName;
    }

    public PotionType getPotionType() {
        return potionType;
    }

    public BrewPrice getPrice() {
        return price;
    }
}
