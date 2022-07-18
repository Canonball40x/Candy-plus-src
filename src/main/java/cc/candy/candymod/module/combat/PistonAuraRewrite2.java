package cc.candy.candymod.module.combat;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.event.events.player.UpdateWalkingPlayerEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.*;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.actors.ActorCanReply;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fast_mac
 * not gay
 */
public class PistonAuraRewrite2 extends Module {
    public Setting<Float> preDelay = register(new Setting("BlockDelay" , 0F , 25F , 0F));
    public Setting<Float> pistonDelay = register(new Setting("PistonDelay" , 0F , 25F , 0F));
    public Setting<Float> breakDelay = register(new Setting("BreakDelay" , 1.0F , 25F , 0F));
    public Setting<Float> destroyDelay = register(new Setting("DestroyDelay" , 1.0F , 25F , 0F));

    public Setting<Float> range = register(new Setting("Range" , 5.0F , 15.0F , 0.0F));
    public Setting<findType> targetFindType = register(new Setting("Target" , findType.Nearest));
    public Setting<Boolean> breakSync = register(new Setting("BreakSync" , true));
    public Setting<Float> maxDelay = register(new Setting("MaxDelay" , 30F , 60F , 5F , v -> breakSync.getValue()));
    public Setting<Integer> breakAttempts = register(new Setting("BreakAttempts" , 7 , 20 , 1));
    public Setting<_type> type = register(new Setting("Type" , _type.Tick));
    public Setting<Redstone> redstone = register(new Setting("Redstone" , Redstone.Block));
    public Setting<Boolean> toggle = register(new Setting("Toggle" , true));
    public Setting<Boolean> multiThreading = register(new Setting("MultiThreading" , false));
    public Setting<Boolean> sound = register(new Setting("Sound" , true));
    public Setting<Integer> maxY = register(new Setting("MaxY" , 3 , 5 , 1));
    public Setting<Boolean> packetPlace = register(new Setting("PacketPlace" , true));
    public Setting<Boolean> packetCrystal = register(new Setting("PacketCrystal" , true));
    public Setting<Boolean> packetBreak = register(new Setting("PacketBreak" , true));
    public Setting<Arm> swingArm = register(new Setting("SwingArm" , Arm.None));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , true));

    public Setting<Boolean> render = register(new Setting("Render" , true));
    public Setting<Boolean> line = register(new Setting("Line" , false , v -> render.getValue()));
    public Setting<Float> width = register(new Setting("Width" , 2.0F , 5.0F , 0.1F , s -> line.getValue() && render.getValue()));
    public Setting<Color> pistonColor = register(new Setting("PistonColor" , new Color(10 , 100 , 150 , 100) , v -> render.getValue()));
    public Setting<Color> crystalColor = register(new Setting("CrystalColor" , new Color(250, 0 , 200 , 50) , v -> render.getValue()));
    public Setting<Color> redstoneColor = register(new Setting("RedstoneColor" , new Color(250, 10 , 10 , 50) , v -> render.getValue()));

    public static EntityPlayer target = null;
    public BlockPos pistonPos , crystalPos , redStonePos = null;
    public int pistonSlot , crystalSlot , redStoneSlot , obbySlot = -1;
    public boolean isTorchSlot = false;
    public Timer preTimer , pistonTimer , breakTimer , destroyTimer , maxTimer = new Timer();
    public boolean preparedSpace , placedPiston , placedRedstone , placedCrystal , brokeCrystal , synced = false;
    public int attempts = 0;
    public int lastCrystal = -1;

    public enum _type{
        Tick ,
        Update ,
        Walking
    }

    public enum findType {
        Nearest ,
        Looking ,
        Best
    }

    public enum Redstone {
        Block ,
        Torch ,
        Both
    }

    public enum Arm{
        Mainhand ,
        Offhand ,
        None
    }

    public PistonAuraRewrite2(){
        super("PistonAuraRewrite2" , Categories.COMBAT , false , false);
    }

    @Override
    public void onTick(){
        if(type.getValue() == _type.Tick) doPA();
    }

    @Override
    public void onUpdate(){
        if(type.getValue() == _type.Update) doPA();
    }

    @Override
    public void onEnable(){
        lastCrystal = -1;
        reset();
    }

    @SubscribeEvent
    public void onUpdateWalking(UpdateWalkingPlayerEvent event){
        if(type.getValue() == _type.Walking) doPA();
    }

    public void doPA()
    {
        if(multiThreading.getValue())
            MultiThreading.runAsync(this::_doPA);
        else
            _doPA();
    }

    public void _doPA(){
        if(nullCheck()) return;
        if(!setup()) return;

        BlockPos targetPos = new BlockPos(target.posX , target.posY , target.posZ);

        if(preTimer.passedX(preDelay.getValue()) && !preparedSpace) {
            pistonTimer.reset();
            preparedSpace = prepareSpace();
        }
        //redstone base
        if(preparedSpace &&!placedPiston && !placedRedstone){
            if(redStonePos.equals(pistonPos.add(0 , -1 , 0))){
                setItem(redStoneSlot);
                BlockUtil.placeBlock(redStonePos, packetPlace.getValue());
                placedRedstone = true;
            }
        }
        if(pistonTimer.passedX(pistonDelay.getValue()) && preparedSpace && !placedPiston){
            setItem(pistonSlot);
            float[] angle = MathUtil.calcAngle(new Vec3d(crystalPos), new Vec3d(targetPos));
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0] + 180.0F, angle[1], true));
            BlockUtil.placeBlock(pistonPos, packetPlace.getValue());
            placedPiston = true;
        }
        //not based
        if(placedPiston && !placedRedstone){
            setItem(redStoneSlot);
            if(!isTorchSlot) {
                //search facing
                EnumFacing facing = null;
                for(EnumFacing facing1 : EnumFacing.values()){
                    if(pistonPos.add(facing1.getDirectionVec()).equals(redStonePos)) facing = facing1;
                }
                //skip
                if(facing == null) return;
                BlockUtil.rightClickBlock(pistonPos , facing , packetPlace.getValue());
            }
            else{
                BlockUtil.placeBlock(redStonePos , packetPlace.getValue());
            }
            placedRedstone = true;
        }
        if(placedRedstone && !placedCrystal){
            setItem(crystalSlot);
            EnumHand hand = crystalSlot != 999 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
            if (packetCrystal.getValue())
                mc.player.connection.sendPacket(
                        new CPacketPlayerTryUseItemOnBlock(crystalPos, EnumFacing.DOWN, hand, 0, 0, 0));
            else
                mc.playerController.processRightClickBlock(mc.player, mc.world, crystalPos, EnumFacing.DOWN, new Vec3d(0, 0, 0), hand);
            //predicthit
            /*
            if(lastCrystal != -1 && packetBreak.getValue() && predictHit.getValue()){
                for(int i = 0; i < amount.getValue(); i++){
                    CPacketUseEntity packet = new CPacketUseEntity();
                    packet.entityId = lastCrystal + i + 1;
                    packet.action = CPacketUseEntity.Action.ATTACK;
                    mc.player.connection.sendPacket(packet);
                }
            }
             */

            breakTimer.reset();
            placedCrystal = true;
        }
        if(breakTimer.passedX(breakDelay.getValue()) && placedCrystal && !brokeCrystal){
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

            maxTimer.reset();
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

                lastCrystal = crystal.entityId;

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

    public boolean prepareSpace()
    {
        BlockPos targetPos = new BlockPos(target.posX , target.posY , target.posZ);
        BlockPos redstone = redStonePos.add(0 , -1 , 0);
        BlockPos base =  pistonPos.add(0 , -1 , 0);

        if (BlockUtil.getBlock(base) == Blocks.AIR) {
            BlockPos offset = new BlockPos( crystalPos.getX() - targetPos.getX(), 0, crystalPos.getZ() - targetPos.getZ());
            BlockPos crystalOffset = crystalPos.add(offset);
            BlockPos crystalOffset1 = crystalOffset.add(offset);

            if (BlockUtil.hasNeighbour(base) || base.equals(redStonePos))
            {
                if(!base.equals(redStonePos)){
                    setItem(obbySlot);
                    BlockUtil.placeBlock(base , packetPlace.getValue());
                    return false;
                }
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
                && !(pistonPos.getX() == redStonePos.getX() && pistonPos.getZ() == redStonePos.getZ()) && isTorchSlot)
        {
            setItem(obbySlot);
            BlockUtil.placeBlock(redstone , packetPlace.getValue());
            return false;
        }

        return true;
    }

    public boolean setup(){
        if(!findMaterials()) {
            disablePA("Cannot find materials! disabling");
            return false;
        }

        if(!findTarget()){
            disablePA("Cannot find target! disabling");
            return false;
        }

        if(pistonPos == null){
            if(!findSpace(target)){
                disablePA("Cannot find space! disabling");
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRender3D(){
        try{
            if(render.getValue()){
                if(crystalPos == null) return;

                if(line.getValue()){
                    RenderUtil3D.drawBoundingBoxWithSides(crystalPos ,  (int)(width.getValue() + 3.0F) , crystalColor.getValue() , GeometryMasks.Quad.UP);
                    RenderUtil3D.drawBoundingBox(pistonPos , 1.0 , width.getValue() , pistonColor.getValue());
                    RenderUtil3D.drawBoundingBox(redStonePos , 1.0 , width.getValue() , redstoneColor.getValue());
                }
                else{
                    RenderUtil3D.drawBox(crystalPos , 1.0 , crystalColor.getValue() , GeometryMasks.Quad.UP);
                    RenderUtil3D.drawBox(pistonPos , 1.0 , pistonColor.getValue() , GeometryMasks.Quad.ALL);
                    RenderUtil3D.drawBox(redStonePos , 1.0 , redstoneColor.getValue() , GeometryMasks.Quad.ALL);
                }
            }
        }
        catch (Exception ignored){

        }
    }

    public void reset(){
        preTimer = new Timer();
        pistonTimer = new Timer();
        breakTimer = new Timer();
        maxTimer = new Timer();
        destroyTimer = new Timer();

        preparedSpace = false;
        placedPiston = false;
        placedRedstone = false;
        placedCrystal = false;
        brokeCrystal = false;
        synced = false;

        pistonPos = null;
        crystalPos = null;
        redStonePos = null;

        attempts = 0;
    }

    public boolean findSpace(EntityPlayer target){
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
        BlockPos targetPos = new BlockPos(target.posX , target.posY , target.posZ);

        BlockPos[] offsets = new BlockPos[]{
                new BlockPos(1 , 0 ,0) ,
                new BlockPos(-1 , 0 , 0) ,
                new BlockPos(0 , 0 , 1) ,
                new BlockPos(0 , 0 , -1)
        };

        List<PistonAuraPos> can = new ArrayList<>();
        for(int y = 0; y <= maxY.getValue(); y++) {
            for (BlockPos offset : offsets) {
                BlockPos crystalPos = targetPos.add(offset.getX() , y , offset.getZ());
                //base
                if (!(BlockUtil.getBlock(crystalPos) == Blocks.OBSIDIAN ||
                        BlockUtil.getBlock(crystalPos) == Blocks.BEDROCK)) continue;
                if (!(BlockUtil.getBlock(crystalPos.add(0, 1, 0)) == Blocks.AIR
                        && BlockUtil.getBlock(crystalPos.add(0, 2, 0)) == Blocks.AIR)) continue;
                if (PlayerUtil.getDistanceI(crystalPos) > range.getValue()) continue;
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
                //gamesense like side
                BlockPos side4 = crystalPos.add(offset.getZ(), offset.getY(), offset.getX());
                BlockPos side5 = crystalPos.add(offset.getZ() * -1, offset.getY(), offset.getX() * -1);

                List<PistonPos> pistons = new ArrayList<PistonPos>();
                add(pistons, new PistonPos(normal));
                add(pistons, new PistonPos(oneBlock));
                add(pistons, new PistonPos(side0));
                add(pistons, new PistonPos(side1));
                add(pistons, new PistonPos(side2));
                add(pistons, new PistonPos(side3));
                add(pistons, new PistonPos(side4 , true));
                add(pistons, new PistonPos(side5 , true));

                PistonPos piston = pistons.stream()
                        .filter(p -> BlockUtil.getBlock(p.pos) == Blocks.AIR)
                        .filter(p -> PlayerUtil.getDistanceI(p.pos) <= range.getValue())
                        .filter(p -> !checkPos(p.pos))
                        .filter(p -> {
                            BlockPos headPos = p.pos.add(offset.getX() * -1, 0, offset.getZ() * -1);
                            return BlockUtil.getBlock(headPos) == Blocks.AIR && !checkPos(headPos);
                        })
                        .filter(p -> !(mypos.getDistance(p.pos.getX(), p.pos.getY(), p.pos.getZ()) < 3.6D + (p.pos.getY() - (mypos.getY() + 1))
                                && p.pos.getY() > mypos.getY() + 1))
                        .filter(p -> getRedStonePos(p.pos, crystalPos, offset , p.swap) != null)
                        .min(Comparator.comparing(p -> PlayerUtil.getDistanceI(p.pos))).orElse(null);
                if (piston == null) continue;

                PistonAuraPos pos = new PistonAuraPos(crystalPos, piston.pos, getRedStonePos(piston.pos, crystalPos, offset , piston.swap));
                can.add(pos);
            }
        }

        //a
        PistonAuraPos best = can.stream().min(Comparator.comparing(PistonAuraPos::range)).orElse(null);

        if(best == null) return false;

        crystalPos = best.crystal;
        pistonPos = best.piston;
        redStonePos = best.redstone;
        return true;
    }

    public BlockPos getRedStonePos(BlockPos pistonPos , BlockPos crystalPos , BlockPos _offset , boolean swap){
        BlockPos reverseOffset = new BlockPos(_offset.getX() * -1 , _offset.getY() , _offset.getZ() * -1);
        BlockPos pistonOffset = pistonPos.add(reverseOffset);

        BlockPos[] offsets = new BlockPos[]{
                new BlockPos(1 , 0 , 0) ,
                new BlockPos(-1 , 0 , 0) ,
                new BlockPos(0 , 0 , 1) ,
                new BlockPos(0 , 0 , -1) ,
                new BlockPos(0 , 1 , 0) ,
                new BlockPos(0 , -1 , 0)
        };

        //already placed
        for(BlockPos offset : offsets){
            BlockPos redstone = pistonPos.add(offset);
            if(redstone.equals(pistonOffset)) continue;
            if(BlockUtil.getBlock(redstone) == Blocks.REDSTONE_BLOCK
                    || (BlockUtil.getBlock(redstone) == Blocks.REDSTONE_TORCH && offset.getY() != 1)) return redstone;
        }

        List<BlockPos> pos = new ArrayList<>();
        //search
        for(BlockPos offset : offsets){
            BlockPos redstone = pistonPos.add(offset);
            if(isTorchSlot && offset.getY() == 1) continue;
            if(BlockUtil.getBlock(redstone) != Blocks.AIR) continue;
            if(redstone.getX() == crystalPos.getX() && redstone.getZ() == crystalPos.getZ()) continue;
            if(redstone.equals(pistonOffset)) continue;
            if(checkPos(redstone)) continue;

            pos.add(redstone);
        }

        return pos.stream().filter(b -> PlayerUtil.getDistance(b) <= range.getValue())
                .max(Comparator.comparing(PlayerUtil::getDistanceI)).orElse(null);
    }

    public boolean checkPos(BlockPos pos)
    {
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
        return pos.getX() == mypos.getX() && pos.getZ() == mypos.getZ();
    }

    public void add(List<PistonPos> pistons , PistonPos pos)
    {
        pistons.add(new PistonPos(pos.pos.add(0 , 1 , 0) , pos.swap));
        pistons.add(new PistonPos(pos.pos.add(0 , 2 , 0) , pos.swap));
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
        if(this.redstone.getValue() == Redstone.Both){
            if(block != -1) redStoneSlot = block;
            else redStoneSlot = torch;
        }

        isTorchSlot = redStoneSlot == torch;

        return pistonSlot != -1 && obbySlot != -1 && crystalSlot != -1 && redStoneSlot != -1;
    }

    public boolean findTarget()
    {
        if(targetFindType.getValue() == findType.Nearest)
            target = PlayerUtil.getNearestPlayer(30);
        if(targetFindType.getValue() == findType.Looking)
            target = PlayerUtil.getLookingPlayer(30);
        if(targetFindType.getValue() == findType.Best)
            target = mc.world.playerEntities
                    .stream().filter(e -> e.entityId != mc.player.entityId)
                    .filter(e -> !findSpace(e)).min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);

        return target != null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPacketReceive(PacketEvent.Receive event) {
        if(!isEnable) return;
        if(sound.getValue()){
            if (event.getPacket() instanceof SPacketSoundEffect) {
                if (((SPacketSoundEffect) event.getPacket()).getCategory() == SoundCategory.BLOCKS && ((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity crystal : new ArrayList<>(mc.world.loadedEntityList)) {
                        if (crystal instanceof EntityEnderCrystal)
                            if (crystal.getDistance(((SPacketSoundEffect) event.getPacket()).getX(), ((SPacketSoundEffect) event.getPacket()).getY(), ((SPacketSoundEffect) event.getPacket()).getZ()) <= range.getValue() + 5) {
                                crystal.setDead();
                            }
                    }
                }
            }
        }
    }

    public void disablePA(String msg){
        if(toggle.getValue()){
            sendMessage(msg);
            disable();
        }
    }

    private EnumHand oldhand = null;
    private int oldslot = -1;

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

    public class PistonAuraPos {
        public BlockPos crystal , piston , redstone;
        public PistonAuraPos(BlockPos crystal , BlockPos piston , BlockPos redstone){
            this.crystal = crystal;
            this.piston = piston;
            this.redstone = redstone;
        }

        public double range()
        {
            double crystalRange = PlayerUtil.getDistanceI(crystal);
            double pistonRange = PlayerUtil.getDistanceI(piston);
            return Math.max(pistonRange , crystalRange);
        }
    }

    public class PistonPos
    {
        public BlockPos pos;
        public boolean swap;

        public PistonPos(BlockPos pos)
        {
            this.pos = pos;
            this.swap = false;
        }
        public PistonPos(BlockPos pos , boolean swap)
        {
            this.pos = pos;
            this.swap = swap;
        }
    }
}
