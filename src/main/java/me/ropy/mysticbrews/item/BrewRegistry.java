package me.ropy.mysticbrews.item;

import me.ropy.mysticbrews.dsa.HashTable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BrewRegistry {

    private static HashTable<String, BrewItem> registeredBrews = new HashTable<>(10);

    public static void registerBrews(){

    }

    public static BrewItem getById(String id){
        return null;
    }

    public static BrewItem getFromItemStack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String itemId = pdc.get(BrewItem.ITEM_ID_KEY, PersistentDataType.STRING);
        if (itemId == null) return null;

        return registeredBrews.get(itemId);
    }

    public static boolean isMysticBrew(ItemStack itemStack){
        return getFromItemStack(itemStack) != null;
    }

    public static ItemStack createItemStack(String itemId, int amount) {
        BrewItem item = getById(itemId);
        if (item == null) return null;
        return item.createItemStack();
    }
}
