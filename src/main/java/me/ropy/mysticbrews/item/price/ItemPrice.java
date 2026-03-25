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
            //loops through inventory checking each item stack if it matches desired material
            for(ItemStack itemStack : player.getInventory().getContents()){
                if(itemStack == null) continue;;
                if(itemStack.getType().equals(material)){
                    //adds the amount of desired material to counter
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
            //loops through player's inventory contents (order of the inventory slots)
            ItemStack[] contents = player.getInventory().getContents();
            for(int i = 0; i < contents.length; i++){
                ItemStack itemStack = contents[i];
                //stop loop if no more remaining
                if(remaining == 0) break;
                //continue loop if itemstack is null or not the desired material
                if(itemStack == null) continue;
                if(!itemStack.getType().equals(material)) continue;
                //if the current stack has less than or equal to remaining remove it (set to null)
                if(itemStack.getAmount() <= remaining){
                    remaining -= itemStack.getAmount();
                    player.getInventory().setItem(i, null);
                }else{
                    //set the item stack to the difference (kind of simulates the change)
                    player.getInventory().setItem(i, itemStack.subtract(remaining));
                }
            }
        }
    }
}
