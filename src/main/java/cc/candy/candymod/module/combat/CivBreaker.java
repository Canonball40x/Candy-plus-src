package cc.candy.candymod.module.combat;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.exploit.InstantMine;
import cc.candy.candymod.module.exploit.SilentPickel;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class CivBreaker extends Module{
    public Setting<Float> preDelay = register(new Setting("BlockDelay" , 0F , 20F , 0F));
    public Setting<Float> crystalDelay = register(new Setting("CrystalDelay" , 0F , 20F , 0F));
    public Setting<Float> breakDelay = register(new Setting("BreakDelay" , 0F , 20F , 0F));
    public Setting<Float> attackDelay = register(new Setting("AttackDelay" , 3.0F , 20F , 0F));
    public Setting<Float> endDelay = register(new Setting("EndDelay" , 0F , 20F , 0F));
    public Setting<Float> range = register(new Setting("Range" , 10F , 20F , 1F));
    public Setting<Boolean> tick = register(new Setting("Tick" , true));
    public Setting<Boolean> toggle = register(new Setting("Toggle" , true));
    public Setting<Boolean> noSwingBlock = register(new Setting("NoSwingBlock" , true));
    public Setting<Boolean> packetPlace = register(new Setting("PacketPlace" , false));
    public Setting<Boolean> packetCrystal = register(new Setting("PacketCrystal" , true));
    public Setting<Boolean> instantBreak = register(new Setting("InstantBreak" , false));
    public Setting<Boolean> toggleSilentPickel = register(new Setting("ToggleSilentPickel" , false));
    public Setting<Boolean> packetBreak = register(new Setting("PacketBreak" , true));
    public Setting<Boolean> offHandBreak = register(new Setting("OffhandBreak" , true));
    public Setting<Boolean> skip = register(new Setting("Skip" , false));
    public Setting<Integer> breakAttempts = register(new Setting("BreakAttempts" , 7 , 20 , 1));
    public Setting<Float> targetRange = register(new Setting("Target Range" , 10F , 20F , 0F));
    public Setting<CevBreaker.Target> targetType = register(new Setting("Target" , CevBreaker.Target.Nearest));
    //render options
    public Setting<Boolean> render = register(new Setting("Render" , true));
    public Setting<Color> blockColor = register(new Setting("Color" , new Color(250, 0 , 200 , 50)));
    public Setting<Boolean> line = register(new Setting("Line" , false , s -> render.getValue()));
    public Setting<Float> width = register(new Setting("Line Width" , 2.0F , 5.0F , 0.1F , s -> line.getValue() && render.getValue()));

    public BlockPos base , old = null;
    public boolean builtTrap , placedCrystal , brokeBlock , attackedCrystal , done = false;
    public int crystalSlot , obbySlot , pickelSlot = -1;
    public int attempts = 0;
    public static EntityPlayer target = null;
    public Timer blockTimer , crystalTimer , breakTimer , attackTimer , endTimer = null;
    public static boolean breaking = false;

    public CivBreaker(){
        super("CivBreaker" , Module.Categories.COMBAT , false , false);
    }

    //target type
    public enum Target
    {
        Nearest ,
        Looking ,
        Best
    };

    @Override
    public void onEnable()
    {
        reset();
        if(toggleSilentPickel.getValue())
            setToggleSilentPickel(true);
    }

    @Override
    public void onDisable()
    {
        if(toggleSilentPickel.getValue())
            setToggleSilentPickel(false);
    }

    public void setToggleSilentPickel(boolean toggle)
    {
        Module silent = CandyMod.m_module.getModuleWithClass(SilentPickel.class);
        if(toggle)
            silent.enable();
        else
            silent.disable();
    }

    @Override
    public void onTick()
    {
        if(tick.getValue())doCV();
    }

    @Override
    public void onUpdate()
    {
        if(!tick.getValue())doCV();
    }

    public void doCV()
    {
		if(nullCheck()) return;
        try {
            if (!findMaterials()) {
                if (toggle.getValue()) {
                    sendMessage("Cannot find materials! disabling...");
                    disable();
                }
                //very important
                return;
            }
            //trying find target
            target = findTarget();
            if (isNull(target)) {
                if (toggle.getValue()) {
                    sendMessage("Cannot find target! disabling...");
                    disable();
                }
                //very important
                return;
            }
            if (isNull(base)) {
                if (!findSpace(target)) {
                    if (toggle.getValue()) {
                        sendMessage("Cannot find space! disabling...");
                        disable();
                    }
                    //very important
                    return;
                }
            }

            BlockPos targetPos = new BlockPos(target.posX , target.posY , target.posZ);
            if(blockTimer == null)
            {
                blockTimer = new Timer();
            }
            if(!builtTrap)
            {
                if(BlockUtil.getBlock(base) == Blocks.AIR)
                {
                    if(blockTimer.passedX(preDelay.getValue()))
                    {
                        setItem(obbySlot);
                        placeBlock(base, false);
                        blockTimer = null;
                        builtTrap = true;
                    }
                }

            }

            if(builtTrap && (!base.equals(old) && skip.getValue()))
            {
                placedCrystal = true;
            }
            if(crystalTimer == null && builtTrap)
            {
                crystalTimer = new Timer();
            }
            if(builtTrap && !placedCrystal)
            {
                if(crystalTimer.passedX(crystalDelay.getValue()))
                {
                    if (crystalSlot != 999) {
                        setItem(crystalSlot);
                    }

                    EnumHand hand = crystalSlot != 999 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                    if (packetCrystal.getValue()) {
                        mc.player.connection.sendPacket(
                                new CPacketPlayerTryUseItemOnBlock(base, EnumFacing.DOWN, hand, 0, 0, 0));
                    } else {
                        mc.playerController.processRightClickBlock(mc.player, mc.world, base, EnumFacing.DOWN, new Vec3d(0, 0, 0), hand);
                    }
                    placedCrystal = true;
                }
            }


            if(breakTimer == null && placedCrystal)
            {
                breakTimer = new Timer();
            }
            if(placedCrystal && !brokeBlock)
            {
                if(breakTimer.passedX(breakDelay.getValue()))
                {
                    setItem(pickelSlot);

                    if(BlockUtil.getBlock(base) == Blocks.AIR)
                    {
                        brokeBlock = true;
                    }

                    if(!breaking) {
                        if(!instantBreak.getValue())
                        {
                            if (!noSwingBlock.getValue()) {
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                            }
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.START_DESTROY_BLOCK, base, EnumFacing.DOWN
                            ));
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, base, EnumFacing.DOWN
                            ));
                        }
                        else
                        {
                            if(!noSwingBlock.getValue()) {
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                            }
                            CandyMod.m_module.getModuleWithClass(InstantMine.class).enable();
                            InstantMine.startBreak(base , EnumFacing.DOWN);
                        }
                        breaking = true;
                    }

                }
            }

            if(brokeBlock && (!base.equals(old) && skip.getValue())) attackedCrystal = true;
            if(attackTimer == null && brokeBlock)
            {
                attackTimer = new Timer();
            }
            if(brokeBlock && !attackedCrystal)
            {
                breaking = false;
                if(attackTimer.passedX(attackDelay.getValue()))
                {
                    BlockPos plannedCrystalPos = base.add(0 , 1 , 0);
                    Entity crystal = mc.world.loadedEntityList.stream()
                            .filter(e -> e instanceof EntityEnderCrystal)
                            .filter(e -> new BlockPos(e.posX, e.posY , e.posZ).getDistance(plannedCrystalPos.getX() , plannedCrystalPos.getY() , plannedCrystalPos.getZ()) < 1.5)
                            .min(Comparator.comparing(c -> c.getDistance(target))).orElse(null);
                    if(crystal == null)
                    {
                        if(attempts < breakAttempts.getValue())
                        {
                            reset();
                            return;
                        }

                        attempts++;
                        return;
                    }

                    EnumHand hand = offHandBreak.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                    if (packetBreak.getValue()) {
                        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    } else {
                        mc.playerController.attackEntity(mc.player, crystal);
                        mc.player.swingArm(hand);
                    }

                    attackedCrystal = true;
                }
            }

            if(endTimer == null && attackedCrystal)
            {
                endTimer = new Timer();
            }
            if(attackedCrystal && !done)
            {
                if(endTimer.passedX(endDelay.getValue()))
                {
                    done = true;
                    old = new BlockPos(base.getX() , base.getY() , base.getZ());
                    reset();
                }
            }

            restoreItem();

        }
        catch (Exception e){}

    }

    public void reset()
    {
        base = null;
        builtTrap = false;
        placedCrystal = false;
        brokeBlock = false;
        attackedCrystal = false;
        done = false;
        crystalSlot = -1;
        obbySlot = -1;
        pickelSlot = -1;
        attempts = 0;
        target = null;
        blockTimer = null;
        crystalTimer = null;
        breakTimer = null;
        attackTimer = null;
        endTimer = null;
        breaking = false;
    }

    @Override
    public void onRender3D()
    {
        try {
            if(isNull(target))
            {
                return;
            }

            BlockPos headPos = base;

            if(line.getValue()) {
                RenderUtil3D.drawBoundingBox(headPos , 1 , 1 , new Color(blockColor.getValue().getRed() , blockColor.getValue().getGreen() , blockColor.getValue().getBlue() , 200));
            }
            else {
                RenderUtil3D.drawBox(headPos , 1 , blockColor.getValue() , GeometryMasks.Quad.ALL);
            }
        }
        catch(Exception e){}
    }

    public void setItem(int slot)
    {
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    public void restoreItem()
    {

    }

    public boolean findSpace(EntityPlayer player)
    {
        BlockPos targetPos = new BlockPos(player.posX , player.posY , player.posZ);
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);

        BlockPos[] offsets = new BlockPos[]
                {
                        new BlockPos(1 , 0 , 0 ) ,
                        new BlockPos(-1 , 0 , 0) ,
                        new BlockPos(0 , 0 , 1) ,
                        new BlockPos(0 , 0 , -1),
                        //update moment
                        //new BlockPos(1 , 0 , 1 ) ,
                        //new BlockPos(-1 , 0 , 1 ) ,
                        //new BlockPos(1 , 0 , -1 ) ,
                        //new BlockPos(-1 , 0 , -1 ) ,
                };
        java.util.List<BlockPos> posess = new ArrayList<>();

        for (BlockPos offset:
                offsets) {
            BlockPos basePos = targetPos.add(offset);
            if(!(BlockUtil.getBlock(basePos) == Blocks.OBSIDIAN || BlockUtil.getBlock(basePos) == Blocks.BEDROCK) ||
                    (!BlockUtil.canPlaceBlockFuture(basePos.add(0 , 1 , 0)) && !(BlockUtil.getBlock(basePos.add(0 , 1 , 0)) == Blocks.OBSIDIAN)) ||
                    (!CrystalUtil.canPlaceCrystal(basePos.add(0 , 1 , 0)))) {
                continue;
            }
            posess.add(basePos.add(0 , 1 , 0));
        }

        base = posess.stream()
                .filter(p -> mc.player.getDistance(p.getX() , p.getY() , p.getZ()) <= range.getValue())
                .max(Comparator.comparing(p ->  PlayerUtil.getDistanceI(p))).orElse(null);
        if(base == null)
        {
            return false;
        }
        return true;
    }

    //search target
    public EntityPlayer findTarget()
    {
        EntityPlayer target = null;
        //players
        java.util.List<EntityPlayer> players = mc.world.playerEntities;
        //nearest
        if(targetType.getValue() == CevBreaker.Target.Nearest)
        {
            //search nearest player
            target = PlayerUtil.getNearestPlayer(targetRange.getValue());
        }
        //looking
        if(targetType.getValue() == CevBreaker.Target.Looking)
        {
            //search looking player
            target = PlayerUtil.getLookingPlayer(targetRange.getValue());
        }
        //best
        if(targetType.getValue() == CevBreaker.Target.Best)
        {
            //loop
            target = players.stream().filter(/*found space*/ p -> findSpace(p))
                    .min(Comparator.comparing(p -> PlayerUtil.getDistance(p))).orElse(null);
        }

        return target;

    }

    //find slot
    public boolean findMaterials()
    {
        crystalSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        pickelSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);

        if(itemCheck(crystalSlot))
        {
            if(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)
            {
                crystalSlot = 999;
            }
        }

        //check
        if(itemCheck(crystalSlot) || itemCheck(obbySlot) || itemCheck(pickelSlot))
            return false;

        return true;
    }

    public boolean itemCheck(int slot)
    {
        return slot == -1;
    }

    public boolean isNull(Object o)
    {
        return o == null;
    }

    public void placeBlock(BlockPos pos , Boolean packet)
    {
        BlockUtil.placeBlock(pos , packet);
        if(!noSwingBlock.getValue()) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
}
