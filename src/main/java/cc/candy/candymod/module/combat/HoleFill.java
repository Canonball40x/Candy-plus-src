package cc.candy.candymod.module.combat;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.BlockUtil;
import cc.candy.candymod.utils.InventoryUtil;
import cc.candy.candymod.utils.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.stream.Collectors;

public class HoleFill extends Module {
    public Setting<Float> range = register(new Setting("Range" , 6.0F , 12.0F , 1.0F));
    public Setting<Integer> place = register(new Setting("Place" , 2 , 10 , 1));
    public Setting<Boolean> toggle = register(new Setting("Toggle" , false));
    public Setting<Boolean> packetPlace = register(new Setting("PacketPlace" , false));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , false));

    public HoleFill(){
        super("HoleFill" , Categories.COMBAT , false , false);
    }

    @Override
    public void onTick(){
        if(nullCheck()) return;
        int slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);

        int counter = 0;
        setItem(slot);
        List<BlockPos> holes = CandyMod.m_hole.getHoles().stream().filter(e -> PlayerUtil.getDistance(e) < range.getValue()).collect(Collectors.toList());
        for(BlockPos hole : holes){
            if(counter >= place.getValue()) break;
            counter++;
            BlockUtil.placeBlock(hole , packetPlace.getValue());
        }
        if(holes.size() == 0) disable();

        restoreItem();
    }

    private EnumHand oldhand = null;
    private int oldslot = -1;

    public void setItem(int slot)
    {
        if(silentSwitch.getValue()) {
            oldhand = null;
            if(mc.player.isHandActive()) {
                oldhand = mc.player.getActiveHand();
            }
            oldslot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        }
        else {
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public void restoreItem()
    {
        if(oldslot != -1 && silentSwitch.getValue())
        {
            if(oldhand != null) {
                mc.player.setActiveHand(oldhand);
            }
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            oldslot = -1;
            oldhand = null;
        }
    }
}
