package cc.candy.candymod.module.combat;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.InventoryUtil;
import cc.candy.candymod.utils.Timer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class AutoXP extends Module {
    public Setting<Boolean> packetThrow = register(new Setting("PacketThrow" , true));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , true));
    public Setting<Float> delay = register(new Setting("Delay" , 0.0F , 25.0F , 0.0F));

    public int oldSlot = -1;
    public EnumHand oldHand = null;
    public Timer timer = new Timer();

    public AutoXP()
    {
        super("AutoXP" , Categories.COMBAT , false , false);
    }

    @Override
    public void onEnable()
    {
        timer = new Timer();
    }

    @Override
    public void onTick()
    {
		if(nullCheck()) return;
        int xp = InventoryUtil.getItemHotbar(Items.EXPERIENCE_BOTTLE);
        if(xp == -1) {
            restoreItem();
            return;
        }

        if(timer.passedX(delay.getValue()))
        {
            timer.reset();
            setItem(xp);
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, 90, true));
            if (packetThrow.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            } else {
                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
            }
        }

        restoreItem();
    }

    public void setItem(int slot)
    {
        if(silentSwitch.getValue()) {
            oldHand = null;
            if(mc.player.isHandActive()) {
                oldHand = mc.player.getActiveHand();
            }
            oldSlot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        }
        else {
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public void restoreItem()
    {
        if(oldSlot != -1 && silentSwitch.getValue())
        {
            if(oldHand != null) {
                mc.player.setActiveHand(oldHand);
            }
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            oldSlot = -1;
            oldHand = null;
        }
    }
}
