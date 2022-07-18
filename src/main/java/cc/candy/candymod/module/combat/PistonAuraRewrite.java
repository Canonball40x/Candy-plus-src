package cc.candy.candymod.module.combat;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PistonAuraRewrite extends Module {
    public Setting<Float> preDelay = register(new Setting("BlockDelay" , 0F , 25F , 0F));
    public Setting<Float> pistonDelay = register(new Setting("PistonDelay" , 0F , 25F , 0F));
    public Setting<Float> crystalDelay = register(new Setting("CrystalDelay" , 0F , 25F , 0F));
    public Setting<Float> redstoneDelay = register(new Setting("RedstoneDelay" , 0F , 25F , 0F));
    public Setting<Float> breakDelay = register(new Setting("BreakDelay" , 1.0F , 25F , 0F));
    public Setting<Float> destroyDelay = register(new Setting("DestroyDelay" , 1.0F , 25F , 0F));

    public Setting<findType> targetFindType = register(new Setting("Target" , findType.Nearest));
    public Setting<Redstone> redstone = register(new Setting("Redstone" , Redstone.Block));
    public Setting<Boolean> breakSync = register(new Setting("BreakSync" , true));
    public Setting<Float> maxDelay = register(new Setting("MaxDelay" , 30F , 60F , 5F , v -> breakSync.getValue()));
    public Setting<Integer> breakAttempts = register(new Setting("BreakAttempts" , 7 , 20 , 1));
    public Setting<Boolean> packetPlace = register(new Setting("PacketPlace" , true));
    public Setting<Boolean> packetCrystal = register(new Setting("PacketCrystal" , true));
    public Setting<Boolean> packetBreak = register(new Setting("PacketBreak" , true));
    public Setting<Arm> swingArm = register(new Setting("SwingArm" , Arm.None));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , true));
    public Setting<Boolean> toggle = register(new Setting("Toggle" , true));
    public Setting<Boolean> multiThreading = register(new Setting("MultiThreading" , false));
    public Setting<Integer> maxY = register(new Setting("MaxY" , 3 , 5 , 1));
    public Setting<Float> range = register(new Setting("Range" , 6.0F , 15.0F , 0.0F));
    public Setting<Boolean> tick = register(new Setting("Tick" , true));
    public Setting<Boolean> render = register(new Setting("Render" , true));
    public Setting<Boolean> line = register(new Setting("Line" , false , v -> render.getValue()));
    public Setting<Float> width = register(new Setting("Width" , 2.0F , 5.0F , 0.1F , s -> line.getValue() && render.getValue()));
    public Setting<Color> pistonColor = register(new Setting("PistonColor" , new Color(10 , 100 , 100 , 100) , v -> render.getValue()));
    public Setting<Color> crystalColor = register(new Setting("CrystalColor" , new Color(250, 0 , 200 , 50) , v -> render.getValue()));
    public Setting<Color> redstoneColor = register(new Setting("RedstoneColor" , new Color(250, 10 , 10 , 50) , v -> render.getValue()));

    public static EntityPlayer target = null;
    public BlockPos pistonPos , crystalPos , redStonePos = null;
    public int pistonSlot , crystalSlot , redStoneSlot , obbySlot = -1;
    public int oldslot = -1;
    public int attempts = 0;
    public EnumHand oldhand = null;
    public Timer preTimer , pistonTimer , crystalTimer , redstoneTimer , breakTimer , destroyTimer , maxTimer = new Timer();
    public boolean preparedSpace , placedPiston , placedCrystal , placedRedstone , synced , brokeCrystal = false;

    public enum findType {
        Nearest ,
        Looking ,
        Best
    }

    public enum Redstone {
        Block ,
        Torch
    }

    public enum Arm{
        Mainhand ,
        Offhand ,
        None
    }

    public PistonAuraRewrite()
    {
        super("PistonAuraRewrite" , Categories.COMBAT , false , false);
    }

    @Override
    public void onEnable()
    {
        reset();
    }

    @Override
    public void onUpdate()
    {
        if(!tick.getValue())
            doPA();
    }

    @Override
    public void onTick()
    {
        if(tick.getValue())
            doPA();
    }

    public void doPA()
    {
        if(multiThreading.getValue())
            MultiThreading.runAsync(this::_doPA);
        else
            _doPA();
    }

    public void _doPA()
    {
        try{
            if(nullCheck()) return;
            if(setup()) return;
    
            BlockPos targetPos = new BlockPos(target.posX , target.posY , target.posZ);
    
            if(preTimer.passedX(preDelay.getValue()) && !preparedSpace) {
                preTimer.reset();
                pistonTimer.reset();
                preparedSpace = prepareSpace();
            }
    
            if(pistonTimer.passedX(pistonDelay.getValue()) && preparedSpace && !placedPiston) {
                crystalTimer.reset();
                setItem(pistonSlot);
                float[] angle = MathUtil.calcAngle(new Vec3d(crystalPos), new Vec3d(targetPos));
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0] + 180.0F, angle[1], true));
                BlockUtil.placeBlock(pistonPos, packetPlace.getValue());
                placedPiston = true;
            }
    
            if(crystalTimer.passedX(crystalDelay.getValue()) && placedPiston && !placedCrystal) {
                redstoneTimer.reset();
                setItem(crystalSlot);
                EnumHand hand = crystalSlot != 999 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                if (packetCrystal.getValue())
                    mc.player.connection.sendPacket(
                            new CPacketPlayerTryUseItemOnBlock(crystalPos, EnumFacing.DOWN, hand, 0, 0, 0));
                else
                    mc.playerController.processRightClickBlock(mc.player, mc.world, crystalPos, EnumFacing.DOWN, new Vec3d(0, 0, 0), hand);
                placedCrystal = true;
            }
    
            if(redstoneTimer.passedX(redstoneDelay.getValue()) && placedCrystal && !placedRedstone){
                breakTimer.reset();
                setItem(redStoneSlot);
                if(redstone.getValue() == Redstone.Block) {
                    //search facing
                    EnumFacing facing = null;
                    for(EnumFacing facing1 : EnumFacing.values()){
                        if(pistonPos.add(facing1.getDirectionVec()).equals(redStonePos)) facing = facing1;
                    }
                    BlockUtil.rightClickBlock(pistonPos , facing , packetPlace.getValue());
                }
                else{
                    BlockUtil.placeBlock(redStonePos , packetPlace.getValue());
                }
                placedRedstone = true;
            }
    
            if(breakTimer.passedX(breakDelay.getValue()) && placedRedstone && !brokeCrystal){
                maxTimer.reset();
    
                Entity crystal = mc.world.loadedEntityList.
                        stream().filter(e -> e instanceof EntityEnderCrystal)
                        .filter(e -> target.getDistance(e) < 5)
                        .min(Comparator.comparing(e -> target.getDistance(e))).orElse(null);
    
                if(crystal == null){
                    if(attempts <= breakAttempts.getValue())
                        attempts++;
                    else
                        reset();
    
                    restoreItem();
                    return;
                }
    
                if(packetBreak.getValue())
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                else
                    mc.playerController.attackEntity(mc.player, crystal);
    
                EnumHand hand = swingArm.getValue() == Arm.Mainhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                if(swingArm.getValue() != Arm.None)
                    mc.player.swingArm(hand);
    
                brokeCrystal = true;
            }
    
            if(!breakSync.getValue()) synced = true;
            if(breakSync.getValue() && brokeCrystal && !synced) {
                destroyTimer.reset();
                if(maxTimer.passedDms(maxDelay.getValue())) synced = true;
    
                if(BlockUtil.getBlock(pistonPos) == Blocks.AIR)
                    synced = true;
                else {
                    Entity crystal = mc.world.loadedEntityList.
                            stream().filter(e -> e instanceof EntityEnderCrystal)
                            .filter(e -> target.getDistance(e) < 5)
                            .min(Comparator.comparing(e -> target.getDistance(e))).orElse(null);
    
                    if(crystal == null){
                        restoreItem();
                        return;
                    }
    
                    if(packetBreak.getValue())
                        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    else
                        mc.playerController.attackEntity(mc.player, crystal);
    
                    EnumHand hand = swingArm.getValue() == Arm.Mainhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                    if(swingArm.getValue() != Arm.None)
                        mc.player.swingArm(hand);
                }
            }
    
            if(destroyTimer.passedX(destroyDelay.getValue()) && synced)
                reset();
    
            restoreItem();
        }
        catch(Exception ignored){

        }
    }

    public boolean setup()
    {
        if(!findMaterials())
        {
            if((toggle.getValue()))
            {
                sendMessage("Cannot find materials! disabling");
                disable();
            }
            return true;
        }

        if(!findTarget())
        {
            if(toggle.getValue())
            {
                sendMessage("Cannot find target! disabling");
                disable();
            }
            return true;
        }

        if(pistonPos == null)
            if(!findSpace(target))
            {
                if(toggle.getValue())
                {
                    sendMessage("Cannot find space! disabling");
                    disable();
                }
                return true;
            }

        return false;
    }

    @Override
    public void onRender3D()
    {
        try
        {
            if(crystalPos == null) return;

            if(line.getValue()){
                RenderUtil3D.drawBoundingBox(pistonPos, 1, width.getValue(), pistonColor.getValue());
                RenderUtil3D.drawBoundingBox(crystalPos, 1, width.getValue(), crystalColor.getValue());
                RenderUtil3D.drawBoundingBox(redStonePos, 1, width.getValue(), redstoneColor.getValue());
            }
            else{
                RenderUtil3D.drawBox(pistonPos , 1.0F , pistonColor.getValue() , GeometryMasks.Quad.ALL);
                RenderUtil3D.drawBox(crystalPos , 1.0F , crystalColor.getValue() , GeometryMasks.Quad.ALL);
                RenderUtil3D.drawBox(redStonePos , 1.0F , redstoneColor.getValue() , GeometryMasks.Quad.ALL);
            }
        }catch (Exception ignored) {}
    }

    public boolean prepareSpace()
    {
        BlockPos targetPos = new BlockPos(target.posX , target.posY , target.posZ);
        BlockPos piston = pistonPos.add(0 , -1 , 0);
        BlockPos redstone = redStonePos.add(0 , -1 , 0);

        if (BlockUtil.getBlock(piston) == Blocks.AIR) {
            BlockPos offset = new BlockPos( crystalPos.getX() - targetPos.getX(), 0, crystalPos.getZ() - targetPos.getZ());
            BlockPos crystalOffset = crystalPos.add(offset);
            BlockPos crystalOffset1 = crystalOffset.add(offset);

            if (BlockUtil.hasNeighbour(piston))
            {
                setItem(obbySlot);
                BlockUtil.placeBlock(piston , packetPlace.getValue());
                return false;
            }
            else if(BlockUtil.getBlock(crystalOffset) == Blocks.AIR)
            {
                setItem(obbySlot);
                BlockUtil.placeBlock(crystalOffset , packetPlace.getValue());
                return false;
            }
            else if(BlockUtil.getBlock(crystalOffset1) == Blocks.AIR)
            {
                setItem(obbySlot);
                BlockUtil.placeBlock(crystalOffset1 , packetPlace.getValue());
                return false;
            }
        }
        if(BlockUtil.getBlock(redstone) == Blocks.AIR
                        && !(pistonPos.getX() == redStonePos.getX() && pistonPos.getZ() == redStonePos.getZ()))
        {
            setItem(obbySlot);
            BlockUtil.placeBlock(redstone , packetPlace.getValue());
            return false;
        }

        return true;
    }

    public boolean findSpace(EntityPlayer target)
    {
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
        BlockPos targetPos = new BlockPos(target.posX , target.posY , target.posZ);
        BlockPos[] offsets = new BlockPos[]
                {
                        new BlockPos(1 , 0 , 0 ) ,
                        new BlockPos(-1 , 0 , 0 ) ,
                        new BlockPos(0 , 0 , 1) ,
                        new BlockPos(0 , 0 , -1)
                };

        List<AuraPos> aurapos = new ArrayList<>();
        for(int y = 0; y <= maxY.getValue(); y++) {
            for (BlockPos offset : offsets) {
                BlockPos crystalPos = targetPos.add(offset.add(0 , y , 0));
                if (!(BlockUtil.getBlock(crystalPos) == Blocks.OBSIDIAN || BlockUtil.getBlock(crystalPos) == Blocks.BEDROCK))
                    continue;
                if (!(BlockUtil.getBlock(crystalPos.add(0, 1, 0)) == Blocks.AIR && BlockUtil.getBlock(crystalPos.add(0, 2, 0)) == Blocks.AIR))
                    continue;
                if (checkPos(crystalPos)) continue;

                //normal
                BlockPos normal = crystalPos.add(offset);
                //1block
                BlockPos oneBlock = normal.add(offset);
                //side
                BlockPos side0 = normal.add(offset.getZ(), offset.getY(), offset.getX());
                BlockPos side1 = normal.add(offset.getZ() * -1, offset.getY(), offset.getX() * -1);
                //1block side
                BlockPos side2 = oneBlock.add(offset.getZ(), offset.getY(), offset.getX());
                BlockPos side3 = oneBlock.add(offset.getZ() * -1, offset.getY(), offset.getX() * -1);

                List<PistonPos> pistons = new ArrayList<PistonPos>();
                add(pistons, new PistonPos(normal, false));
                add(pistons, new PistonPos(oneBlock, true));
                add(pistons, new PistonPos(side0, false));
                add(pistons, new PistonPos(side1, false));
                add(pistons, new PistonPos(side2, true));
                add(pistons, new PistonPos(side3, true));

                PistonPos piston = pistons.stream()
                        .filter(p -> BlockUtil.getBlock(p.pos) == Blocks.AIR)
                        .filter(p -> {
                            BlockPos headPos = p.pos.add(offset.getX() * -1, 0, offset.getZ() * -1);
                            if (BlockUtil.getBlock(headPos) != Blocks.AIR || checkPos(headPos)) return false;
                            return true;
                        })
                        .filter(p -> getRedStonePos(crystalPos, p, offset) != null)
                        .filter(p -> !checkPos(p.pos))
                        .filter(p -> PlayerUtil.getDistanceI(p.pos) <= range.getValue())
                        .filter(p -> !(mypos.getDistance(p.pos.getX() , p.pos.getY() , p.pos.getZ()) < 3.6D + (p.pos.getY() - (mypos.getY() + 1))
                                && p.pos.getY() > mypos.getY() + 1))
                        .min(Comparator.comparing(p -> PlayerUtil.getDistanceI(p.pos))).orElse(null);
                if (piston == null) continue;

                aurapos.add(new AuraPos(piston.pos, crystalPos, getRedStonePos(crystalPos, piston, offset)));
            }
        }

        AuraPos best = aurapos.stream()
                .min(Comparator.comparing(p -> p.range())).orElse(null);
        if(best == null)
            return false;

        pistonPos = best.piston;
        crystalPos = best.crystal;
        redStonePos = best.redstone;

        return true;
    }

    public BlockPos getRedStonePos(BlockPos crystalPos , PistonPos piston , BlockPos offset)
    {
        BlockPos pistonPos = piston.pos;
        //block
        if(redstone.getValue() == Redstone.Block)
        {
            List<BlockPos> redstone = new ArrayList<>();
            redstone.add(pistonPos.add(offset));
            if(piston.allowUpside)
                redstone.add(pistonPos.add(0 , 1 , 0));

            return redstone.stream()
                    .filter(p -> BlockUtil.getBlock(p) == Blocks.AIR)
                    .filter(p -> !checkPos(p))
                    .min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
        }
        //torch
        if(redstone.getValue() == Redstone.Torch)
        {
            BlockPos[] offsets = new BlockPos[]
                    {
                            new BlockPos(1 , 0 ,0) ,
                            new BlockPos(-1 , 0 , 0) ,
                            new BlockPos(0 , 0 ,1) ,
                            new BlockPos(0 , 0 , -1)
                    };

            List<BlockPos> torchs = new ArrayList<>();
            for(BlockPos offs : offsets)
            {
                BlockPos torch = pistonPos.add(offs);
                BlockPos pistonP = pistonPos.add(offset.getX() * -1 , 0 , offset.getZ() * -1);
                if(torch.getX() == crystalPos.getX() && torch.getZ() == crystalPos.getZ()) continue;
                if(torch.getX() == pistonP.getX() && torch.getZ() == pistonP.getZ()) continue;
                if(BlockUtil.getBlock(torch) != Blocks.AIR) continue;
                if(checkPos(torch)) continue;
                torchs.add(torch);
            }

            return torchs.stream().min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
        }

        return null;
    }

    public void add(List<PistonPos> pistons , PistonPos pos)
    {
        pistons.add(new PistonPos(pos.pos.add(0 , 1 , 0) , pos.allowUpside));
        pistons.add(new PistonPos(pos.pos.add(0 , 2 , 0) , true));
    }

    public boolean findTarget()
    {
        if(targetFindType.getValue() == findType.Nearest)
            target = PlayerUtil.getNearestPlayer(range.getValue());
        if(targetFindType.getValue() == findType.Looking)
            target = PlayerUtil.getLookingPlayer(range.getValue());

        if(target == null) return false;
        return true;
    }

    public boolean findMaterials()
    {
        pistonSlot = InventoryUtil.findHotbarBlock(Blocks.PISTON);
        obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        crystalSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);

        if(pistonSlot == -1) pistonSlot = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON);
        if(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) crystalSlot = 999;

        int block = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
        int torch = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_TORCH);
        if(this.redstone.getValue() == Redstone.Block) redStoneSlot = block;
        if(this.redstone.getValue() == Redstone.Torch) redStoneSlot = torch;

        if(pistonSlot == -1 || obbySlot == -1 || crystalSlot == -1 ||
                redStoneSlot == -1) return false;

        return true;
    }

    public void reset()
    {
        target = null;

        pistonPos = null;
        crystalPos = null;
        redStonePos = null;

        pistonSlot = -1;
        crystalSlot = -1;
        redStoneSlot = -1;
        obbySlot = -1;

        oldslot = -1;
        oldhand = null;

        preTimer = new Timer();
        pistonTimer = new Timer();
        crystalTimer = new Timer();
        redstoneTimer = new Timer();
        breakTimer = new Timer();
        destroyTimer = new Timer();
        maxTimer = new Timer();

        preparedSpace = false;
        placedPiston = false;
        placedCrystal = false;
        placedRedstone = false;
        brokeCrystal = false;
        synced = false;

        attempts = 0;
    }

    public boolean checkPos(BlockPos pos)
    {
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
        return pos.getX() == mypos.getX() && pos.getZ() == mypos.getZ();
    }

    public void setItem(int slot)
    {
        if(slot == 999) return;

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

    public class AuraPos
    {
        public BlockPos piston , crystal , redstone = null;

        public AuraPos(BlockPos piston , BlockPos crystal , BlockPos redstone)
        {
            this.piston = piston;
            this.crystal = crystal;
            this.redstone = redstone;
        }

        public double range()
        {
            double pistonRange = PlayerUtil.getDistanceI(piston);
            double crystalRange = PlayerUtil.getDistanceI(crystal);
            double redstoneRange = PlayerUtil.getDistanceI(redstone);
            return Math.max(Math.max(pistonRange , crystalRange) , redstoneRange);
        }
    }

    public class PistonPos
    {
        public BlockPos pos;
        public boolean allowUpside;
        public PistonPos(BlockPos pos , boolean allowUpside)
        {
            this.pos = pos;
            this.allowUpside = allowUpside;
        }
    }
}
