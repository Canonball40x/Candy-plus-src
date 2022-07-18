package cc.candy.candymod.module.combat;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import cc.candy.candymod.utils.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PistonAura extends Module {
    //settings
    public Setting<Float> preDelay = register(new Setting("BlockDelay" , 0F , 25F , 0F));
    public Setting<Float> pistonDelay = register(new Setting("PistonDelay" , 0F , 25F , 0F));
    public Setting<Float> crystalDelay = register(new Setting("CrystalDelay" , 0F , 25F , 0F));
    public Setting<Float> redstoneDelay = register(new Setting("RedStoneDelay" , 0F , 25F , 0F));
    public Setting<Float> pushDelay = register(new Setting("PushDelay" , 0F , 25F , 0F));
    public Setting<Float> breakDelay = register(new Setting("BreakDelay" , 5F , 25F , 0F));
    public Setting<Float> targetRange = register(new Setting("Target Range" , 10F , 20F , 0F));
    public Setting<Target> targetType = register(new Setting("Target" , Target.Nearest));
    public Setting<Float> range = register(new Setting("Range" , 10F , 20F , 1F));
    public Setting<RedStone> redStoneType = register(new Setting("Redstone" , RedStone.Both));
    public Setting<Boolean> antiBlock = register(new Setting("AntiBlock" , true));
    public Setting<Boolean> noSwingBlock = register(new Setting("NoSwingBlock" , false));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , false));
    public Setting<Boolean> packetPlace = register(new Setting("PacketPlace" , true));
    public Setting<Boolean> packetCrystal = register(new Setting("PacketCrystal" , true));
    public Setting<Boolean> packetBreak = register(new Setting("PacketBreak" , true));
    public Setting<Boolean> offHandBreak = register(new Setting("OffhandBreak" , true));
    public Setting<Boolean> sidePiston = register(new Setting("SidePiston" , false));
    public Setting<Boolean> tick = register(new Setting("Tick" , true));
    public Setting<Boolean> toggle = register(new Setting("Toggle" , true));
    public Setting<Boolean> trap = register(new Setting("Trap" , false));
    public Setting<Float> trapDelay = register(new Setting("TrapDelay" , 3F , 25F , 0F , s -> trap.getValue()));
    public Setting<Boolean> breakSync = register(new Setting("BreakSync" , true));
    public Setting<Float> maxDelay = register(new Setting("MaxDelay" , 50F , 100F , 1F , s -> breakSync.getValue()));
    public Setting<Integer> breakAttempts = register(new Setting("BreakAttempts" , 7 , 20 , 1));
    public Setting<Integer> maxY = register(new Setting("MaxY" , 2 , 4 , 1));

    //color settings
    public Setting<Boolean> render = register(new Setting("Render" , true));
    public Setting<Color> crystalColor = register(new Setting("Crystal Color" , new Color(250, 0 , 200 , 50) , s -> render.getValue()));
    public Setting<Color> pistonColor = register(new Setting("Piston Color" , new Color(40, 170, 245 , 50) , s -> render.getValue()));
    public Setting<Color> redstoneColor = register(new Setting("RedStone Color" , new Color(252, 57, 50 , 50) , s -> render.getValue()));
    public Setting<Boolean> line = register(new Setting("Line" , false , s -> render.getValue()));
    public Setting<Float> width = register(new Setting("Line Width" , 2.0F , 5.0F , 0.1F , s -> line.getValue() && render.getValue()));

    //debug
    public Setting<Boolean> debug = register(new Setting("Debug" , false));
    //render
    public List<BlockPos> debugPosess = new ArrayList<>();
    //old hand
    public int oldslot = -1;
    //enum hand
    public EnumHand oldhand = null;

    public PistonAura()
    {
        super("PistonAura" , Categories.COMBAT ,false , false);
    }

    //target entity
    public static EntityPlayer target;
    //pos
    public BlockPos pistonPos , crystalPos , redStonePos;
    //progress
    public boolean placedPiston , placedCrystal , placedRedStone , waitedPiston , brokeCrystal , builtTrap , done , retrying , digging;
    //timers
    public Timer pistonTimer , crystalTimer , redStoneTimer , pistonCrystalTimer , breakTimer , preTimer , trapTimer , syncTimer;
    //materials
    public int pistonSlot , crystalSlot , redstoneSlot , obbySlot , pickelSlot;
    //progress
    public int trapTicks = 0;
    public int attempts = 0;
    //old pos
    public BlockPos oldPiston , oldRedstone = null;

    //a
    public int tmpSlot;

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

    @Override
    public void onEnable()
    {
        reset();
    }

    @Override
    public void onUpdate()
    {
        if(!tick.getValue())doPA();
    }

    @Override
    public void onTick()
    {
        if(tick.getValue())doPA();
    }

    public void doPA()
    {
		if(nullCheck()) return;
        try {
            //TODO: support redstone block

            //disable pistonaura if cannot find materials
            if (!findMaterials()) {
                if(toggle.getValue()) {
                    sendMessage("Cannot find materials! disabling...");
                    disable();
                }
                //very important
                return;
            }
            //trying find target
            target = findTarget();
            //disable pistonaura if cannot find target
            if (isNull(target)) {
                if(toggle.getValue()) {
                    sendMessage("Cannot find target! disabling...");
                    disable();
                }
                //very important
                return;
            }
            //trying find space
            if (isNull(pistonPos) || isNull(crystalPos) || isNull(redStonePos)) {
                if (!findSpace(target, redStoneType.getValue())) {
                    if(toggle.getValue()) {
                        sendMessage("Cannot find space! disabling...");
                        disable();
                    }
                    //very important
                    return;
                }
            }

            //prepare pos
            if (preTimer == null) {
                preTimer = new Timer();
            }

            if (preTimer.passedX(preDelay.getValue())) {
                if (!prepareBlock()) {
                    restoreItem();
                    return;
                }
            }

            //trap
            if (trapTimer == null) {
                trapTimer = new Timer();
            }

            if (!trap.getValue()) builtTrap = true;
            BlockPos targetPos = new BlockPos(target.posX, target.posY, target.posZ);
            if(BlockUtil.getBlock(targetPos.add(0 , 2 , 0)) == Blocks.OBSIDIAN
                    || pistonPos.getY() >= targetPos.add(0 , 2 , 0).getY()) builtTrap = true;

            if (!builtTrap) {
                if (trapTimer.passedX(trapDelay.getValue())) {
                    BlockPos offset = new BlockPos(crystalPos.getX() - targetPos.getX(), 0, crystalPos.getZ() - targetPos.getZ());
                    BlockPos trapBase = targetPos.add(offset.getX() * -1, 0, offset.getZ() * -1);

                    if (trapTicks == 0
                            && BlockUtil.getBlock(trapBase) == Blocks.AIR) {
                        setItem(obbySlot);
                        placeBlock(trapBase, false);
                        trapTimer = new Timer();
                        trapTicks = 1;
                    } else {
                        trapTicks = 1;
                    }
                    if (trapTicks == 1) {
                        setItem(obbySlot);
                        placeBlock(trapBase.add(0, 1, 0), false);
                        trapTimer = new Timer();
                        trapTicks = 2;
                    }
                    if (trapTicks == 2) {
                        setItem(obbySlot);
                        placeBlock(trapBase.add(0, 2, 0), false);
                        trapTimer = new Timer();
                        trapTicks = 3;
                    }
                    if (trapTicks == 3) {
                        setItem(obbySlot);
                        placeBlock(targetPos.add(0, 2, 0), false);
                        trapTimer = new Timer();
                        trapTicks = 4;
                        builtTrap = true;
                    }

                    restoreItem();
                    return;
                }
            }

            //place piston
            if (pistonTimer == null && builtTrap) {
                pistonTimer = new Timer();
            }
            if (builtTrap && !placedPiston) {
                if (pistonTimer.passedX(pistonDelay.getValue())) {
                    setItem(pistonSlot);
                    float[] angle = MathUtil.calcAngle(new Vec3d(crystalPos), new Vec3d(targetPos));
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0] + 180.0F, angle[1], true));
                    placeBlock(pistonPos, packetPlace.getValue());
                    placedPiston = true;
                }
            }

            //place crystal
            if (crystalTimer == null && placedPiston) {
                crystalTimer = new Timer();
            }
            if (placedPiston && !placedCrystal) {
                if (crystalTimer.passedX(crystalDelay.getValue())) {
                    if (crystalSlot != 999) {
                        setItem(crystalSlot);
                    }

                    EnumHand hand = crystalSlot != 999 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                    if (packetCrystal.getValue()) {
                        mc.player.connection.sendPacket(
                                new CPacketPlayerTryUseItemOnBlock(crystalPos, EnumFacing.DOWN, hand, 0, 0, 0));
                    } else {
                        mc.playerController.processRightClickBlock(mc.player, mc.world, crystalPos, EnumFacing.DOWN, new Vec3d(0, 0, 0), hand);
                    }
                    placedCrystal = true;
                }
            }

            //place redstone
            if (redStoneTimer == null && placedCrystal) {
                redStoneTimer = new Timer();
            }
            if (placedCrystal && !placedRedStone) {
                if (redStoneTimer.passedX(redstoneDelay.getValue())) {
                    setItem(redstoneSlot);
                    placeBlock(redStonePos, packetPlace.getValue());
                    placedRedStone = true;
                }
            }

            //wait
            if (pistonCrystalTimer == null && placedRedStone) {
                pistonCrystalTimer = new Timer();
            }
            if (placedRedStone && !waitedPiston) {
                if (pistonCrystalTimer.passedX(pushDelay.getValue())) {
                    waitedPiston = true;
                }
            }


            if (retrying) {
                setItem(pickelSlot);
                if (!digging) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, redStonePos, EnumFacing.DOWN));
                    digging = true;
                }
                if (digging && BlockUtil.getBlock(redStonePos) == Blocks.AIR) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, redStonePos, EnumFacing.DOWN));
                    //place redstone
                    placedCrystal = false;
                    placedRedStone = false;
                    waitedPiston = false;
                    brokeCrystal = false;
                    done = false;
                    digging = false;
                    retrying = false;

                    crystalTimer = null;
                    redStoneTimer = null;
                    pistonCrystalTimer = null;
                    breakTimer = null;

                    attempts = 0;
                }

                restoreItem();
                return;
            }

            //break
            if (waitedPiston && !brokeCrystal) {
                Entity crystal = mc.world.loadedEntityList.
                        stream().filter(e -> e instanceof EntityEnderCrystal)
                        .filter(e -> target.getDistance(e) < 5)
                        .min(Comparator.comparing(e -> target.getDistance(e))).orElse(null);

                if (crystal == null) {
                    if (attempts < breakAttempts.getValue()) {
                        attempts++;
                    } else {
                        attempts = 0;
                        digging = false;
                        retrying = true;
                    }
                    restoreItem();
                    return;
                }
                EnumHand hand = offHandBreak.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                if (packetBreak.getValue()) {
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                } else {
                    mc.playerController.attackEntity(mc.player, crystal);
                    mc.player.swingArm(hand);
                }

                brokeCrystal = true;
            }

            //reset
            if (breakTimer == null && brokeCrystal) {
                breakTimer = new Timer();
            }
            if (brokeCrystal && !done) {
                if (breakTimer.passedX(breakDelay.getValue())) {
                    done = true;
                }
            }

            if (done) {
                if((BlockUtil.getBlock(redStonePos) != Blocks.REDSTONE_BLOCK && BlockUtil.getBlock(redStonePos) != Blocks.REDSTONE_TORCH)
                        || !breakSync.getValue())
                {
                    reset();
                }
                else {
                    if(syncTimer == null)
                    {
                        syncTimer = new Timer();
                    }
                    if(syncTimer.passedDms(maxDelay.getValue()) && maxDelay.getValue() != -1)
                    {
                        reset();
                    }
                    else {
                        //rebreak
                        Entity crystal = mc.world.loadedEntityList.
                                stream().filter(e -> e instanceof EntityEnderCrystal)
                                .filter(e -> target.getDistance(e) < 5)
                                .min(Comparator.comparing(e -> target.getDistance(e))).orElse(null);

                        if (crystal == null) {
                            restoreItem();
                            return;
                        }
                        EnumHand hand = offHandBreak.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                        if (packetBreak.getValue()) {
                            mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                        } else {
                            mc.playerController.attackEntity(mc.player, crystal);
                            mc.player.swingArm(hand);
                        }
                        breakTimer = null;
                        done = false;
                    }
                }
            }

            restoreItem();
        }
        catch (Exception e) {
            CandyMod.Log(Level.ERROR , e.getMessage());
        }
    }

    @Override
    public void onRender3D()
    {
        try{
            if(render.getValue()) {
                if (isNull(pistonPos) || isNull(crystalPos) || isNull(redStonePos)) {
                    return;
                }

                if (line.getValue()) {
                    RenderUtil3D.drawBoundingBox(crystalPos, 1, width.getValue(), convert(crystalColor.getValue()));
                    RenderUtil3D.drawBoundingBox(pistonPos, 1, width.getValue(), convert(pistonColor.getValue()));
                    RenderUtil3D.drawBoundingBox(redStonePos, 1, width.getValue(), convert(redstoneColor.getValue()));
                } else {
                    RenderUtil3D.drawBox(crystalPos, 1, crystalColor.getValue(), GeometryMasks.Quad.ALL);
                    RenderUtil3D.drawBox(pistonPos, 1, pistonColor.getValue(), GeometryMasks.Quad.ALL);
                    RenderUtil3D.drawBox(redStonePos, 1, redstoneColor.getValue(), GeometryMasks.Quad.ALL);
                }

                if (debug.getValue()) {
                    for (BlockPos pos :
                            debugPosess) {
                        if (pos == null) {
                            continue;
                        }

                        RenderUtil3D.drawBoundingBox(pos, 1, width.getValue(), new Color(230, 230, 230));
                    }
                }
            }
        }
        catch (Exception e)
        {
            //ignore
        }

    }
    

    public Color convert(Color c)
    {
        return new Color(c.getRed() , c.getGreen() , c.getBlue() , 240);
    }

    //search space
    //return value : found space
    public boolean findSpace(EntityPlayer target , RedStone type)
    {
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
        BlockPos base = new BlockPos(target.posX, target.posY , target.posZ);
        BlockPos[] offsets = new BlockPos[]
                {
                        new BlockPos(1 , 0 , 0) ,
                        new BlockPos(-1 , 0 , 0) ,
                        new BlockPos(0 , 0 , 1) ,
                        new BlockPos(0 , 0 , -1)
                };
        List<PistonAuraPos> poses = new ArrayList();
        for(BlockPos offset : offsets)
        {
            for(int y = 0; y <= maxY.getValue(); y++) {
                PistonAuraPos pos = new PistonAuraPos();
                BlockPos crystalPos = base.add(offset.getX(), y, offset.getZ());

                //check base block
                if (BlockUtil.getBlock(crystalPos) == Blocks.ENDER_CHEST) {
                    sendmsg("x");
                    continue;
                }
                //check crystal pos
                if ((BlockUtil.getBlock(crystalPos) != Blocks.OBSIDIAN && BlockUtil.getBlock(crystalPos) != Blocks.BEDROCK) || BlockUtil.getBlock(crystalPos.add(0, 1, 0)) != Blocks.AIR || BlockUtil.getBlock(crystalPos.add(0, 2, 0)) != Blocks.AIR) {
                    sendmsg("y");
                    continue;
                }
                //check x and z
                if (mypos.getX() == crystalPos.getX() && mypos.getZ() == crystalPos.getZ()) {
                    sendmsg("a");
                    continue;
                }

                pos.setCrystal(crystalPos);

                List<BlockPos> pistonOffsets = new ArrayList();
                pistonOffsets.add(new BlockPos(1, 0, 0));
                pistonOffsets.add(new BlockPos(-1, 0, 0));
                pistonOffsets.add(new BlockPos(0, 0, 1));
                pistonOffsets.add(new BlockPos(0, 0, -1));

                if (!antiBlock.getValue()) {
                    pistonOffsets.add(new BlockPos(0, 0, 0));
                }

                //piston base
                BlockPos pistonBase = base.add(offset.getX() * 2, y + 1, offset.getZ() * 2);
                List<BlockPos> pistonPoses = new ArrayList<>();
                for (BlockPos poff : pistonOffsets) {
                    BlockPos pPos = pistonBase.add(poff);
                    if (BlockUtil.getBlock(pPos) != Blocks.AIR) {
                        sendmsg("b");
                        continue;
                    }

                    BlockPos checkPos_c = pPos.add(offset.getX() * -1, offset.getY(), offset.getZ() * -1);
                    BlockPos checkPos_r = pPos.add(offset.getX() * 1, offset.getY(), offset.getZ() * 1);

                    if(mypos.getDistance(pPos.getX() , pPos.getY() , pPos.getZ()) < 3.6D + (pPos.getY() - (mypos.getY() + 1))
                                    && pPos.getY() > mypos.getY() + 1)
                    {
                        sendmsg("++");
                        continue;
                    }

					/*
                    //anti no damage
                    if(((pPos.getX() - crystalPos.getX() != 0 && pPos.getZ() - crystalPos.getZ() == 0)
                                || (pPos.getZ() - crystalPos.getZ() != 0 && pPos.getX() - crystalPos.getX() == 0)
                                    && !(offset.getX() == 0 && offset.getZ() == 0)))
                    {
                        sendmsg("no");
                        continue;
                    }
					*/

                    if (BlockUtil.getBlock(checkPos_c) != Blocks.AIR ||
                            BlockUtil.getBlock(checkPos_r) != Blocks.AIR ||
                            (pPos.getX() == crystalPos.getX() && pPos.getZ() == crystalPos.getZ()) ||
                            (mypos.getX() == pPos.getX() && mypos.getZ() == pPos.getZ()) ||
                            (mypos.getX() == checkPos_r.getX() && mypos.getZ() == checkPos_r.getZ()) ||
                            (mypos.getX() == checkPos_c.getX() && mypos.getZ() == checkPos_c.getZ())) {
                        sendmsg("d = " + checkPos_c.toString());
                        continue;
                    }
                    pistonPoses.add(pPos);
                }

                /*
                if(sidePiston.getValue()) {
                    //crystal base
                    BlockPos crystalBase = base.add(offset.getX() * 1, y + 1, offset.getZ() * 1);
                    for (BlockPos poff : pistonOffsets) {
                        BlockPos pPos = crystalBase.add(poff);
                        BlockPos invalidPos = base.add(offset.getX() * 2, y + 1, offset.getZ() * 2);
                        BlockPos checkPos_c = pPos.add(offset.getX() * -1, 0, offset.getZ() * -1);
                        BlockPos checkPos_r = pPos.add(new BlockPos(pPos.getX() - crystalPos.getX()
                                , 0
                                , pPos.getZ() - crystalPos.getZ()));

                        if (invalidPos.getX() == pPos.getX() && invalidPos.getZ() == pPos.getZ())
                            continue;

                        if (mypos.getDistance(pPos.getX(), pPos.getY(), pPos.getZ()) < 3.6D
                                && pPos.getY() > mypos.getY() + 1) {
                            sendmsg("++");
                            continue;
                        }

                        if ((pPos.getX() == mypos.getX() && pPos.getZ() == mypos.getZ()) ||
                                ((pPos.getX() == base.getX() && pPos.getZ() == base.getZ())) ||
                                (checkPos_c.getX() == mypos.getX() && checkPos_c.getZ() == mypos.getZ()) ||
                                BlockUtil.getBlock(checkPos_c) != Blocks.AIR ||
                                BlockUtil.getBlock(pPos) != Blocks.AIR ||
                                BlockUtil.getBlock(checkPos_r) != Blocks.AIR) {
                            continue;
                        }

                        pistonPoses.add(pPos);
                    }
                }

                 */

                pos.setPiston(pistonPoses.stream()
                        .min(Comparator.comparing(p -> mypos.getDistance(p.getX(), p.getY(), p.getZ()))).orElse(null));

                if (isNull(pos.piston)) {
                    sendmsg("e");
                    continue;
                }

                BlockPos redstonePos = null;
                /*
                                            || (((pos.piston.getX() - crystalPos.getX() != 0 && pos.piston.getZ() - crystalPos.getZ() == 0)
                        || (pos.piston.getZ() - crystalPos.getZ() != 0 && pos.piston.getX() - crystalPos.getX() == 0)
                        && !(offset.getX() == 0 && offset.getZ() == 0)) && (pos.piston.getX() == base.getX() || pos.piston.getX() == base.getZ() ))
                 */

                if((crystalPos.getX() != pos.piston.getX() && crystalPos.getZ() != pos.piston.getZ() ||
                        (((pos.piston.getX() - crystalPos.getX() != 0 && pos.piston.getZ() - crystalPos.getZ() == 0)
                        || (pos.piston.getZ() - crystalPos.getZ() != 0 && pos.piston.getX() - crystalPos.getX() == 0)
                        && !(offset.getX() == 0 && offset.getZ() == 0)))))
                {
                    redstonePos = pos.piston.add(offset);
                }
                else
                {
                    redstonePos = pos.piston.add(new BlockPos(pos.piston.getX() - pos.crystal.getX()
                            , 0
                            , pos.piston.getZ() - pos.crystal.getZ()));
                }

                if (BlockUtil.getBlock(redstonePos) != Blocks.AIR) {
                    sendmsg("f");
                    continue;
                }

                pos.setRedStone(redstonePos);

                poses.add(pos);
            }
        }

        if(poses.size() == 0 )
        {
            return false;
        }

        PistonAuraPos bestPos = poses.stream()
                .filter(p -> p.getMaxRange() <= range.getValue())
                .min(Comparator.comparing(p -> p.getMaxRange())).orElse(null);

        if(bestPos == null)
        {
            return false;
        }

        pistonPos = bestPos.piston;
        crystalPos = bestPos.crystal;
        redStonePos = bestPos.redstone;

        return true;
    }

    //search target
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
                    .filter(/*found space*/ p -> findSpace(p , redStoneType.getValue()))
                    .min(Comparator.comparing(p -> PlayerUtil.getDistance(p))).orElse(null);
        }

        return target;

    }

    //find slot
    public boolean findMaterials()
    {
        pistonSlot = InventoryUtil.findHotbarBlock(Blocks.PISTON);
        crystalSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        int redstoneBlock = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
        int redstoneTorch = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_TORCH);
        obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        pickelSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);

        if(itemCheck(crystalSlot))
        {
            if(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)
            {
                crystalSlot = 999;
            }
        }

        //check piston
        if(itemCheck(pistonSlot))
            //change sticky
            pistonSlot = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON);

        //check redstone
        if(redStoneType.getValue() == RedStone.Block)
            //set redstone block
            redstoneSlot = redstoneBlock;

        if(redStoneType.getValue() == RedStone.Torch)
            //set redstone block
            redstoneSlot = redstoneTorch;

        //select torch or block
        if(redStoneType.getValue() == RedStone.Both) {
            redstoneSlot = redstoneTorch;
            if(itemCheck(redstoneSlot))
                redstoneSlot = redstoneBlock;
        }
        //check
        if(itemCheck(crystalSlot) || itemCheck(obbySlot) || itemCheck(pickelSlot))
            return false;
        if(itemCheck(redstoneSlot) || itemCheck(pistonSlot))
            return false;

        return true;
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
        BlockPos crystal = crystalPos;
        BlockPos piston = pistonPos.add(0 , -1 , 0);
        BlockPos redstone = redStonePos.add(0 , -1 , 0);

        if(BlockUtil.getBlock(crystal) == Blocks.AIR)
        {
            //place piston
            setItem(obbySlot);
            placeBlock(crystal , packetPlace.getValue());

            if(delayCheck())
                return false ;
        }
        if(!BlockUtil.hasNeighbour(piston)) {
            setItem(obbySlot);
            BlockPos base = crystal.add( crystal.getX() - targetPos.getX(), 0, crystal.getZ() - targetPos.getZ());
            placeBlock(base , packetPlace.getValue());

            if(delayCheck())
                return false;
        }
        //piston
        if(BlockUtil.getBlock(piston) == Blocks.AIR)
        {
            //place piston
            setItem(obbySlot);
            placeBlock(piston , packetPlace.getValue());

            if(delayCheck())
                return false;
        }
        //redstone
        if(BlockUtil.getBlock(redstone) == Blocks.AIR
                        && !(piston.getX() == redstone.getX() && piston.getZ() == redstone.getZ()))
        {
            //place piston
            setItem(obbySlot);
            placeBlock(redstone , packetPlace.getValue());

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

    public Vec3i getOffset(BlockPos base , int x , int z)
    {
        return new Vec3i(base.getX() * x , 0 , base.getZ() * z);
    }

    public boolean isNull(Object b)
    {
        return b == null;
    }

    public void setTmp()
    {
        tmpSlot = mc.player.inventory.currentItem;
    }
    public void updateItem()
    {
        mc.player.inventory.currentItem = tmpSlot;
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

    public void sendmsg(String s)
    {
        if(debug.getValue())
            sendMessage(s);
    }

    public void placeBlock(BlockPos pos , Boolean packet)
    {
        BlockUtil.placeBlock(pos , packet);
        if(!noSwingBlock.getValue()) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public void reset() {
        //set old
        oldPiston = pistonPos;
        oldRedstone = redStonePos;

        target = null;
        pistonPos = null;
        crystalPos = null;
        redStonePos = null;
        placedPiston = false;
        placedCrystal = false;
        placedRedStone = false;
        waitedPiston = false;
        brokeCrystal = false;
        builtTrap = false;
        done = false;
        digging = false;
        retrying = false;
        pistonTimer = null;
        crystalTimer = null;
        redStoneTimer = null;
        pistonCrystalTimer = null;
        breakTimer = null;
        preTimer = null;
        trapTimer = null;
        syncTimer = null;
        pistonSlot = -1;
        crystalSlot = -1;
        redstoneSlot = -1;
        obbySlot = -1;
        pickelSlot = -1;
        trapTicks = 0;
        attempts = 0;
    }

    //for choice best space
    public class PistonAuraPos
    {
        private BlockPos piston , crystal , redstone;

        public void setPiston(BlockPos piston)
        {
            this.piston = piston;
        }

        public void setCrystal(BlockPos crystal)
        {
            this.crystal = crystal;
        }

        public void setRedStone(BlockPos redstone)
        {
            this.redstone = redstone;
        }

        public BlockPos getCrystalPos()
        {
            return crystal;
        }

        public BlockPos getPistonPos()
        {
            return piston;
        }

        public BlockPos getRedstone()
        {
            return redstone;
        }

        public double getMaxRange()
        {
            double p = PlayerUtil.getDistanceI(piston);
            double c = PlayerUtil.getDistanceI(crystal);
            double r = PlayerUtil.getDistanceI(redstone);

            return Math.max(Math.max(p , c) , r);
        }
    }

}