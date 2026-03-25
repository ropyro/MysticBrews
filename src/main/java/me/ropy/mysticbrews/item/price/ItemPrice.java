package me.ropy.mysticbrews.item.price;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemPrice implements BrewPrice {

    private Material material;
    private int amount;

    public ItemPrice(Material material, int amount){
        this.material = material;
        this.amount = amount;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    //TODO: linear search
    @Override
    public boolean canAfford(Player player) {
        if(player.getInventory().contains(material)){
            int count = 0;
            for(ItemStack itemStack : player.getInventory().getContents()){
                if(itemStack == null) continue;;
                if(itemStack.getType().equals(material)){
                    count += itemStack.getAmount();
                }
            }
            return count >= amount;
        }else{
            return false;
        }
    }

    @Override
    public void charge(Player player) {
        if(player.getInventory().contains(material)){
            int remaining = amount;
            ItemStack[] contents = player.getInventory().getContents();
            for(int i = 0; i < contents.length; i++){
                ItemStack itemStack = contents[i];
                if(remaining == 0) break;
                if(itemStack == null) continue;
                if(!itemStack.getType().equals(material)) continue;
                if(itemStack.getAmount() <= remaining){
                    remaining -= itemStack.getAmount();
                    player.getInventory().setItem(i, null);
                }else{
                    player.getInventory().setItem(i, itemStack.subtract(remaining));
                }
            }
        }
    }
}
