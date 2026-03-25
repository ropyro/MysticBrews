package me.ropy.mysticbrews.customer;

import me.ropy.mysticbrews.components.Chair;

public abstract class AbstractCustomer {

    private Chair chair;

    public AbstractCustomer(Chair chair){
        this.chair = chair;
    }

    public abstract String getName();

    public void setChair(Chair chair) {
        this.chair = chair;
    }

    public Chair getChair(){
        return chair;
    }
}
