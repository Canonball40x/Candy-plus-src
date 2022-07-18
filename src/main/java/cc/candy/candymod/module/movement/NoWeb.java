package cc.candy.candymod.module.movement;

import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.module.Module;

public class NoWeb extends Module {
    public NoWeb(){
        super("NoWeb" , Categories.MOVEMENT , false , false);
    }

    @Override
    public void onUpdate(){
        mc.player.isInWeb = false;
    }

    @Override
    public void onTick(){
        if(nullCheck()) return;
        mc.player.isInWeb = false;
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event){
        mc.player.isInWeb = false;
    }
}
