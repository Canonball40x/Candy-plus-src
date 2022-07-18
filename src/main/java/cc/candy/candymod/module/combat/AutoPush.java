package cc.candy.candymod.module.combat;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoPush extends Module {
    public Setting<Float> preDelay = register(new Setting("BlockDelay" , 0F , 25F , 0F));
    public Setting<Float> placeDelay = register(new Setting("PlaceDelay" , 0F , 25F , 0F));
    public Setting<Boolean> packetPlace = register(new Setting("PacketPlace" , false));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , false));
    public Setting<Float> range = register(new Setting("Range" , 10F , 20F , 1F));
    public Setting<RedStone> redStoneType = register(new Setting("Redstone" , RedStone.Both));
    public Setting<Target> targetType = register(new Setting("Target" , Target.Nearest));
    public Setting<Float> targetRange = register(new Setting("Target Range" , 10F , 20F , 0F));

    public AutoPush()
    {
        super("AutoPush" , Categories.COMBAT , false , false);
    }

    public EntityPlayer target = null;
    public int pistonSlot , redstoneSlot , obbySlot = -1;
    public BlockPos pistonPos , redstonePos = null;
    public int stage = 0;
    public Timer preTimer , timer = null;
    public int oldslot = -1;
    public EnumHand oldhand = null;
    public boolean isTorch = false;

    //target type
    public enum Target
    {
        Nearest ,
        Looking ,
        Best
    };

    //redstone type
    public enum RedStone
    {
        Block ,
        Torch ,
        Both
    };

    public void reset()
    {
        target = null;
        pistonSlot = -1;
        redstoneSlot = -1;
        obbySlot = -1;
        pistonPos = null;
        redstonePos = null;
        stage = 0;
        preTimer = null;
        timer = null;
        oldslot = -1;
        oldhand = null;
        isTorch = false;
    }

    @Override
    public void onEnable()
    {
        reset();
    }

    @Override
    public void onTick(){
        if(nullCheck()) return;

        if(!findMaterials())
        {
            sendMessage("Cannot find materials! disabling...");
            disable();
            return;
        }
        target = findTarget();
        if(target == null)
        {
            sendMessage("Cannot find target! disabling...");
            disable();
            return;
        }
        if (isNull(pistonPos) || isNull(redstonePos)) {
            if (!findSpace(target)) {
                sendMessage("Cannot find space! disabling...");
                disable();
                return;
            }
        }

        if(preTimer == null)
        {
            preTimer = new Timer();
        }
        if (preTimer.passedX(preDelay.getValue())) {
            if (!prepareBlock()) {
                restoreItem();
                return;
            }
        }

        if(timer == null)
        {
            timer = new Timer();
        }
        if (stage == 0) {
            if(timer.passedX(placeDelay.getValue())) {
                setItem(pistonSlot);
                BlockPos targetPos = new BlockPos(target.posX , target.posY , target.posZ);
                float[] angle = MathUtil.calcAngle(new Vec3d(pistonPos), new Vec3d(targetPos));
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0] + 180.0F, angle[1], true));

                BlockUtil.placeBlock(pistonPos, packetPlace.getValue());
                stage = 1;
                timer.reset();
            }
        }
        if (stage == 1) {
            if(timer.passedX(placeDelay.getValue())) {
                setItem(redstoneSlot);
                BlockUtil.placeBlock(redstonePos , packetPlace.getValue());
                stage = 2;
                disable();
                reset();
            }
        }

        restoreItem();
    }

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

    public boolean isNull(Object object)
    {
        return object == null;
    }

    public boolean findSpace(EntityPlayer target)
    {
        BlockPos targetPos = new BlockPos(target.posX , target.posY , target.posZ);
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
        BlockPos[] offsets = new BlockPos[]
                {
                        new BlockPos(1 , 0 , 0) ,
                        new BlockPos(-1 , 0 , 0),
                        new BlockPos(0 , 0 ,1) ,
                        new BlockPos(0 , 0 , -1) ,
                };
        List<AutoPushPos> poses = new ArrayList<>();
        for(BlockPos offset : offsets)
        {
            AutoPushPos pos = new AutoPushPos();
            BlockPos base = targetPos.add(offset);
            if(BlockUtil.getBlock(base) == Blocks.AIR) continue;
            BlockPos pistonPos = base.add(0 , 1 , 0);
            if(BlockUtil.getBlock(pistonPos) != Blocks.AIR) continue;
            if(checkPos(pistonPos)) continue;
            if(PlayerUtil.getDistance(pistonPos) < 3.6D
                    && pistonPos.getY() > mypos.getY() + 1) continue;
            if(BlockUtil.getBlock(targetPos.add(offset.getX() * -1 , 1 , offset.getZ() * -1))
                        != Blocks.AIR) continue;

            List<BlockPos> redstonePoses = new ArrayList<>();
            List<BlockPos> roffsets = new ArrayList<>();
            roffsets.add(new BlockPos(1 , 0 , 0));
            roffsets.add(new BlockPos(-1 , 0 , 0));
            roffsets.add(new BlockPos(0 , 0 ,1));
            roffsets.add(new BlockPos(0 , 0 , -1));
            if(redStoneType.getValue() == RedStone.Block) {
                roffsets.add(new BlockPos(0, 1, 0));
            }
            for(BlockPos roffset : roffsets)
            {
                BlockPos redstonePos = pistonPos.add(roffset);
                if(redstonePos.getX() == targetPos.getX() && redstonePos.getZ() == targetPos.getZ()) continue;
                if(checkPos(redstonePos)) continue;
                if(BlockUtil.getBlock(redstonePos) != Blocks.AIR) continue;
                redstonePoses.add(redstonePos);
            }
            BlockPos redstonePos = redstonePoses.stream()
                    .min(Comparator.comparing(b -> mc.player.getDistance(b.getX() , b.getY()  ,b.getZ())))
                    .orElse(null);
            if(redstonePos == null) continue;
            pos.setPiston(pistonPos);
            pos.setRedStone(redstonePos);

            poses.add(pos);
        }

        AutoPushPos bestPos = poses.stream()
                .filter(p -> p.getMaxRange() <= range.getValue())
                .min(Comparator.comparing(p -> p.getMaxRange())).orElse(null);
        if(bestPos != null)
        {
            pistonPos = bestPos.piston;
            redstonePos = bestPos.redstone;
            return true;
        }

        return false;
    }

    public EntityPlayer findTarget()
    {
        EntityPlayer target = null;
        //players
        List<EntityPlayer> players = mc.world.playerEntities;
        //nearest
        if(targetType.getValue() == Target.Nearest)
        {
            //search nearest player
            target = PlayerUtil.getNearestPlayer(targetRange.getValue());
        }
        //looking
        if(targetType.getValue() == Target.Looking)
        {
            //search looking player
            target = PlayerUtil.getLookingPlayer(targetRange.getValue());
        }
        //best
        if(targetType.getValue() == Target.Best)
        {
            //loop
            target = players.stream()
                    .filter(p -> p.entityId != mc.player.entityId)
                    .filter(/*found space*/ p -> findSpace(p))
                    .min(Comparator.comparing(p -> PlayerUtil.getDistance(p))).orElse(null);
        }

        return target;

    }

    //find slot
    public boolean findMaterials()
    {
        pistonSlot = InventoryUtil.findHotbarBlock(Blocks.PISTON);
        int redstoneBlock = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
        int redstoneTorch = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_TORCH);
        obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);


        //check piston
        if(itemCheck(pistonSlot))
            //change sticky
            pistonSlot = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON);

        //check redstone
        if(redStoneType.getValue() == RedStone.Block) {
            isTorch = false;
            //set redstone block
            redstoneSlot = redstoneBlock;
        }

        if(redStoneType.getValue() == RedStone.Torch) {
            isTorch = true;
            //set redstone torch
            redstoneSlot = redstoneTorch;
        }

        //select torch or block
        if(redStoneType.getValue() == RedStone.Both) {
            isTorch = true;
            redstoneSlot = redstoneTorch;
            if(itemCheck(redstoneSlot)) {
                isTorch = false;
                redstoneSlot = redstoneBlock;
            }
        }
        //check
        if(itemCheck(redstoneSlot) || itemCheck(pistonSlot) || itemCheck(obbySlot))
            return false;

        return true;
    }

    public boolean checkPos(BlockPos pos)
    {
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
        return pos.getX() == mypos.getX() && pos.getZ() == mypos.getZ();
    }

    public boolean itemCheck(int slot)
    {
        return slot == -1;
    }

    //is done
    public boolean prepareBlock()
    {
        //crystal
        BlockPos targetPos = new BlockPos(target.posX , target.posY , target.posZ);
        BlockPos piston = pistonPos.add(0 , -1 , 0);
        BlockPos redstone = redstonePos.add(0 , -1 , 0);

        if(BlockUtil.getBlock(piston) == Blocks.AIR)
        {
            //place piston
            setItem(obbySlot);
            BlockUtil.placeBlock(piston , packetPlace.getValue());

            if(delayCheck())
                return false;
        }
        //redstone
        if(BlockUtil.getBlock(redstone) == Blocks.AIR)
        {
            //place piston
            setItem(obbySlot);
            BlockUtil.placeBlock(redstone , packetPlace.getValue());

            if(delayCheck())
                return false;
        }
        //done place
        return true;
    }

    public boolean delayCheck()
    {
        boolean hasDelay = !(preDelay.getValue() == 0);
        //if(hasDelay)
        //    updateItem();

        return hasDelay;
    }

    public class AutoPushPos
    {
        public BlockPos piston;
        public BlockPos redstone;

        public double getMaxRange()
        {
            if(piston == null || redstone == null) return 999999;
            return Math.max(PlayerUtil.getDistance(piston) , PlayerUtil.getDistance(redstone));
        }

        public void setPiston(BlockPos piston)
        {
            this.piston = piston;
        }

        public void setRedStone(BlockPos redstone)
        {
            this.redstone = redstone;
        }
    }
}
