package me.ropy.mysticbrews.item;

import me.ropy.mysticbrews.dsa.HashTable;
import me.ropy.mysticbrews.item.price.EcoPrice;
import me.ropy.mysticbrews.item.price.ItemPrice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

public class BrewRegistry {

    //tODO: hashtable
    private static HashTable<String, BrewItem> registeredBrews = new HashTable<>(23);

    public static void registerBrews(){
        registeredBrews.put("health", new BrewItem("Spirulina Spectaular", PotionType.HEALING, new ItemPrice(Material.DIAMOND, 10)));
        registeredBrews.put("jumpboost", new BrewItem("Cloudy Liquid", PotionType.LEAPING, new ItemPrice(Material.DIAMOND, 5)));
        registeredBrews.put("strength", new BrewItem("Liquid Aminos", PotionType.STRENGTH, new ItemPrice(Material.DIAMOND, 12)));
        registeredBrews.put("speed", new BrewItem("Sugar Crunch Juice", PotionType.SWIFTNESS, new ItemPrice(Material.DIAMOND, 8)));
        registeredBrews.put("regen", new BrewItem("Mushroom Tonic", PotionType.REGENERATION, new ItemPrice(Material.NETHERITE_INGOT, 1)));
        registeredBrews.put("fireres", new BrewItem("Spicy Lemon Elixir", PotionType.FIRE_RESISTANCE, new ItemPrice(Material.DIAMOND, 10)));
        registeredBrews.put("nightvision", new BrewItem("Carrot Tonic", PotionType.NIGHT_VISION, new ItemPrice(Material.DIAMOND, 10)));
    }

    public static BrewItem getById(String id){
        BrewItem brewItem = registeredBrews.get(id);
        if(brewItem == null)
            brewItem = new BrewItem("Error", PotionType.AWKWARD, new EcoPrice(0));
        return brewItem;
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
