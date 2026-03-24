package me.ropy.mysticbrews.customer;

import me.ropy.mysticbrews.item.BrewItem;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private List<BrewItem> brewItems;

    private final AbstractCustomer customer;

    public Order(AbstractCustomer customer, BrewItem... items){
        this.customer = customer;
        brewItems = new ArrayList<>();
        for(BrewItem brewItem : items){
            brewItems.add(brewItem);
        }
    }

    public List<BrewItem> getBrewItems(){
        return brewItems;
    }

    public AbstractCustomer getCustomer(){
        return customer;
    }
}
