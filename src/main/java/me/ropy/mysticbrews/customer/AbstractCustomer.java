package me.ropy.mysticbrews.customer;

import me.ropy.mysticbrews.components.Chair;

public abstract class AbstractCustomer {

    private Tab tab;
    private Chair chair;

    public AbstractCustomer(Chair chair){
        this.chair = chair;
        tab = new Tab();
    }

    public abstract String getName();

    public Tab getTab() {
        return tab;
    }

    public void setChair(Chair chair) {
        this.chair = chair;
    }

    public Chair getChair(){
        return chair;
    }
}
