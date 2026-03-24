package me.ropy.mysticbrews.customer;

import me.ropy.mysticbrews.components.Chair;
import net.citizensnpcs.api.npc.NPC;

public class NPCCustomer extends AbstractCustomer{

    private NPC npc;

    public NPCCustomer(NPC citizensNPC, Chair chair){
        super(chair);
        this.npc = citizensNPC;
    }

    public NPC getNpc() {
        return npc;
    }

    @Override
    public String getName() {
        return npc.getName();
    }

}
