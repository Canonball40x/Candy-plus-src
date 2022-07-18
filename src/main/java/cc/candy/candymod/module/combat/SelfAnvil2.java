package cc.candy.candymod.module.combat;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.BlockUtil;
import cc.candy.candymod.utils.InventoryUtil;
import cc.candy.candymod.utils.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SelfAnvil2 extends Module {
    public Setting<Boolean> packetPlace = register(new Setting("PacketPlace" , true));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , true));
    public Setting<Boolean> entityCheck = register(new Setting("EntityCheck" , true));
    public Setting<Boolean> crystalOnly = register(new Setting("CrystalOnly" , false , v -> entityCheck.getValue()));

    public BlockPos basePos;
    public int stage;

    public SelfAnvil2(){
        super("SelfAnvil2" , Categories.COMBAT , false , false);
    }

    public void onEnable(){
        basePos = null;
        stage = 0;
    }

    public void onTick(){
        if(nullCheck()) return;

        int obby = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        int anvil = InventoryUtil.findHotbarBlock(Blocks.ANVIL);
        if(obby == -1 || anvil == -1){
            sendMessage("Cannot find materials! disabling");
            disable();
            return;
        }
        BlockPos anvilPos = PlayerUtil.getPlayerPos().add(0 , 2,  0);
        if(!BlockUtil.hasNeighbour(anvilPos)) {
            if((basePos = findPos()) == null){
                sendMessage("Cannot find space! disabling");
                disable();
                return;
            }
            //build base
            setItem(obby);
            BlockPos pos0 = basePos.add(0 , 1 , 0);
            BlockPos pos1 = basePos.add(0 , 2 , 0);
            BlockUtil.placeBlock(pos0 , packetPlace.getValue());
            BlockUtil.rightClickBlock(pos0 , EnumFacing.UP , packetPlace.getValue());
            //place anvil
            setItem(anvil);
            EnumFacing facing = null;
            for(EnumFacing f : EnumFacing.values()) if(pos1.add(f.getDirectionVec()).equals(anvilPos)) facing = f;
            BlockUtil.rightClickBlock(anvilPos , facing , packetPlace.getValue());
            restoreItem();
            disable();
        }
        else{
            setItem(anvil);
            BlockUtil.placeBlock(anvilPos , packetPlace.getValue());
            restoreItem();
            disable();
        }
    }

    public BlockPos findPos(){
        BlockPos playerPos = PlayerUtil.getPlayerPos();
        BlockPos lookingPos = playerPos.add(BlockUtil.getBackwardFacing(PlayerUtil.getLookingFacing()).getDirectionVec());
        List<BlockPos> possiblePlacePositions = new ArrayList<>();
        BlockPos[] offsets = new BlockPos[]{
                new BlockPos(1 , 0 , 0) ,
                new BlockPos(-1 , 0 , 0) ,
                new BlockPos(0 , 0 , 1) ,
                new BlockPos(0 , 0 , -1)
        };
        for(BlockPos offset : offsets){
            BlockPos pos = playerPos.add(offset);
            if(BlockUtil.getBlock(pos) == Blocks.AIR || !BlockUtil.canRightClickForPlace(pos)) continue;
            BlockPos pos0 = pos.add(0 , 1 , 0);
            if(entityCheck(pos0)) continue;
            BlockPos pos1 = pos0.add(0 , 1,  0);
            if(entityCheck(pos1)) continue;
            //anvil
            BlockPos anvil = playerPos.add(0 , 2 , 0);
            if(entityCheck(anvil)) continue;

            possiblePlacePositions.add(pos);
        }
        return possiblePlacePositions
                .stream().min(Comparator.comparing(b -> lookingPos.getDistance(b.getX() , b.getY() , b.getZ()))).orElse(null);
    }

    public boolean entityCheck(BlockPos pos){
        if(!entityCheck.getValue()) return false;
        for(Entity e : mc.world.getEntitiesWithinAABB(Entity.class , new AxisAlignedBB(pos))){
            if(!(e instanceof EntityEnderCrystal) && crystalOnly.getValue()) continue;
            return true;
        }
        return false;
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
