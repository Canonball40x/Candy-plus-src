package cc.candy.candymod.module.combat;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import scala.swing.Action;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Blocker extends Module {
    public Setting<Boolean> piston = register(new Setting("Piston" , true));
    public Setting<Boolean> crystalSync = register(new Setting("CrystalSyncPA" , false , v -> piston.getValue()));
    public Setting<Boolean> breakCrystalPA = register(new Setting("BreakCrystalPA" , false , v -> piston.getValue()));
    public Setting<Boolean> teleportPA = register(new Setting("FlightBreakPA" , true , v -> piston.getValue() && breakCrystalPA.getValue()));
    public Setting<Integer> limitPA = register(new Setting("LimitPA" , 3 , 10 , 1 , v -> piston.getValue() && breakCrystalPA.getValue() && teleportPA.getValue()));
    public Setting<Float> crystalDelayPA = register(new Setting("CrystalDelayPA" , 3.0F , 25.0F , 0.0F , v -> piston.getValue() && breakCrystalPA.getValue()));
    public Setting<Float> range = register(new Setting("Range" , 7.0F , 13.0F , 0.0F , v -> piston.getValue()));
    public Setting<Integer> maxY = register(new Setting("MaxY" , 4 , 6 , 2 , v -> piston.getValue()));

    public Setting<Boolean> cev = register(new Setting("CevBreaker" , true));
    public Setting<Float> crystalDelayCEV = register(new Setting("CrystalDelayCEV" , 3.0F , 25.0F , 0.0F , v -> cev.getValue()));
    public Setting<Boolean> teleportCEV = register(new Setting("FlightBreakCEV" , true , v -> cev.getValue()));
    public Setting<Integer> limitCEV = register(new Setting("LimitCEV" , 3 , 10 , 1 , v -> piston.getValue() && teleportCEV.getValue()));

    public Setting<Boolean> civ = register(new Setting("CivBreaker" , true));
    public Setting<Float> crystalDelayCIV = register(new Setting("CrystalDelayCIV" , 3.0F , 25.0F , 0.0F , v -> cev.getValue()));

    public Setting<Float> placeDelay = register(new Setting("PlaceDelay" , 3.0F , 25.0F , 0.0F));
    public Setting<Boolean> packetPlace = register(new Setting("PacketPlace" , false));

    public Setting<Boolean> packetBreak = register(new Setting("PacketBreak" , true));
    public Setting<Arm> swingArm = register(new Setting("SwingArm" , Arm.None));

    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , false));
    public Setting<Boolean> tick = register(new Setting("Tick" , true));
    public Setting<Boolean> render = register(new Setting("Render" , true));

    public Setting<Color> pistonColor = register(new Setting("PistonColor" , new Color(230 , 10 , 10 , 50) , v -> piston.getValue()));

    public Entity PAcrystal = null;
    public List<BlockPos> pistonPos = new ArrayList<>();
    public Timer crystalTimerPA = new Timer();
    public int oldCrystal = -1;
    public int limitCounterPA = 0;

    public boolean needBlockCEV = false;
    public Timer crystalTimerCEV = new Timer();
    public int limitCounterCEV = 0;
    public int stage = 0;

    public Timer timerCiv = new Timer();
    public List<BlockPos> detectedPosCiv = new ArrayList<>();

    public Timer placeTimer = new Timer();

    private int oldslot = -1;
    private EnumHand oldhand = null;

    public enum Arm
    {
        Mainhand ,
        Offhand ,
        None
    }

    public Blocker()
    {
        super("Blocker" , Categories.COMBAT , false , false);
    }

    @Override
    public void onEnable()
    {
        PAcrystal = null;
        pistonPos = new ArrayList<>();
        crystalTimerPA = new Timer();

        needBlockCEV = false;
        crystalTimerCEV = new Timer();
        limitCounterCEV = 0;
        stage = 0;

        timerCiv = new Timer();
        detectedPosCiv = new ArrayList<>();

        placeTimer = new Timer();
    }

    @Override
    public void onTick()
    {
        if(tick.getValue())
            doBlock();
    }

    @Override
    public void onUpdate()
    {
        if(!tick.getValue())
            doBlock();
    }

    public void doBlock()
    {
        if(nullCheck()) return;
        int obby = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        if(obby == -1) return;

        Module pa = CandyMod.m_module.getModuleWithClass(PistonAura.class);
		Module paRe = CandyMod.m_module.getModuleWithClass(PistonAuraRewrite.class);
        Module paRe2 = CandyMod.m_module.getModuleWithClass(PistonAuraRewrite2.class);
        Module paRe3 = CandyMod.m_module.getModuleWithClass(PistonAuraRewrite3.class);

        if(piston.getValue() && !pa.isEnable && !paRe.isEnable && !paRe2.isEnable && !paRe3.isEnable) {
            execute(() ->
            {
                detectPA();
                blockPA(obby);
            });
        }

        if(cev.getValue())
            execute(() -> blockCEV(obby));

        Module civ = CandyMod.m_module.getModuleWithClass(CivBreaker.class);
        if(this.civ.getValue() && !civ.isEnable && !pa.isEnable && !paRe.isEnable)
            execute(() -> blockCIV(obby));

        restoreItem();
    }

    public void execute(Runnable action)
    {
        try {
            action.run();
        }
        catch (Exception e){}
    }

    @Override
    public void onRender3D()
    {
        try {
            if (pistonPos != null && piston.getValue() && render.getValue())
                for (BlockPos piston : pistonPos)
                    RenderUtil3D.drawBox(piston, 1.0F, pistonColor.getValue(), GeometryMasks.Quad.ALL);
        }
        catch (Exception e) {
            //ignore
        }
    }

    public void blockCIV(int obby) {
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);

        BlockPos[] offsets = new BlockPos[]
                {
                        new BlockPos(1, 1, 0),
                        new BlockPos(-1, 1, 0),
                        new BlockPos(0, 1, 1),
                        new BlockPos(0, 1, -1),

                        new BlockPos(1, 1, 1),
                        new BlockPos(1, 1, -1),
                        new BlockPos(-1, 1, 1),
                        new BlockPos(-1, 1, -1),
                };

        for (BlockPos offset : offsets) {
            BlockPos base = mypos.add(offset);
            List<Entity> crystals = mc.world.loadedEntityList.stream()
                    .filter(e -> e instanceof EntityEnderCrystal).collect(Collectors.toList());

            //check crystal
            if (BlockUtil.getBlock(base) == Blocks.OBSIDIAN) {
                for (Entity crystal : crystals) {
                    BlockPos crystalPos = new BlockPos(crystal.posX, crystal.posY, crystal.posZ);
                    if (base.equals(crystalPos.add(0, -1, 0))) {
                        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                        detectedPosCiv.add(crystalPos);
                    }
                }
            }
        }

        if (timerCiv.passedX(crystalDelayCIV.getValue())) {
            timerCiv.reset();
            Iterator<BlockPos> poses = detectedPosCiv.iterator();
            while (poses.hasNext()) {
                BlockPos pos = poses.next();
                if (BlockUtil.getBlock(pos) == Blocks.AIR) {
                    setItem(obby);
                    if (BlockUtil.getBlock(pos.add(0, -1, 0)) == Blocks.AIR)
                        BlockUtil.placeBlock(pos.add(0, -1, 0), packetPlace.getValue());
                    BlockUtil.placeBlock(pos, packetPlace.getValue());
                    poses.remove();
                }
            }
        }
    }

    public void blockCEV(int obby)
    {
        //detect
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
        BlockPos ceilPos = mypos.add(0 , 2 , 0);
        if(placeTimer.passedX(crystalDelayCEV.getValue()))
        {
            if(stage == 1)
            {
                crystalTimerCEV.reset();
                setItem(obby);
                BlockUtil.placeBlock(ceilPos.add(0 , 1 , 0) , packetPlace.getValue());
                stage = 0;
            }

            //still ceil
            if(BlockUtil.getBlock(ceilPos) == Blocks.OBSIDIAN
                    && CrystalUtil.hasCrystal(ceilPos) && teleportCEV.getValue() && limitCounterCEV < limitCEV.getValue())
            {
                crystalTimerCEV.reset();
                limitCounterCEV++;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX , mypos.getY() + 0.2 , mc.player.posZ , false));
                breakCrystal(CrystalUtil.getCrystal(ceilPos));
                swingArm();

                stage = 1;
            }
            else limitCounterCEV = 0;
        }

    }

    public void blockPA(int obby)
    {
        if(crystalTimerPA.passedX(crystalDelayPA.getValue())
                    && piston.getValue() && breakCrystalPA.getValue() && PAcrystal != null)
        {
            crystalTimerPA.reset();

            if(PAcrystal.entityId == oldCrystal) limitCounterPA++;
            else limitCounterPA = 0;

            //tp(?)
            BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
            BlockPos crystalPos = new BlockPos(PAcrystal.posX , PAcrystal.posY , PAcrystal.posZ);
            if(mypos.getY() + 2 == crystalPos.getY()
                        && BlockUtil.getBlock(mypos.add(0 , 2 , 0)) == Blocks.OBSIDIAN && teleportPA.getValue() && limitCounterPA <= limitPA.getValue())
            {
                double offsetx = (mypos.getX() - crystalPos.getX()) * 0.4;
                double offsetz = (mypos.getZ() - crystalPos.getZ()) * 0.4;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mypos.getX() + 0.5 + offsetx , mypos.getY() + 0.2 , mypos.getZ() + 0.5 + offsetz , false));
            }

            breakCrystal(PAcrystal);
            swingArm();
            oldCrystal = PAcrystal.entityId;
            PAcrystal = null;
        }

        if(placeTimer.passedX(placeDelay.getValue()) && piston.getValue())
        {
            placeTimer.reset();
            Iterator<BlockPos> pistons = pistonPos.iterator();
            while(pistons.hasNext()) {
                BlockPos pos = pistons.next();
                if(BlockUtil.getBlock(pos) == Blocks.AIR) {
                    setItem(obby);
                    if(BlockUtil.hasNeighbour(pos)){
                        BlockUtil.placeBlock(pos , packetPlace.getValue());
                    }
                    else{
                        //place -1
                        BlockUtil.placeBlock(pos.add(0 , -1 , 0) , packetPlace.getValue());
                        BlockUtil.rightClickBlock(pos.add(0 , -1 , 0) , EnumFacing.UP , packetPlace.getValue());
                    }
                    pistons.remove();
                }
            }
        }
    }

    public void detectPA()
    {
        //fix list
        List<BlockPos> tmp = new ArrayList<>();
        Iterator<BlockPos> iterator = pistonPos.iterator();
        while(iterator.hasNext())
        {
            BlockPos pos = iterator.next();
            if(tmp.contains(pos) || PlayerUtil.getDistance(pos) > range.getValue()) iterator.remove();
            tmp.add(pos);
        }

        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
        BlockPos[] offsets = new BlockPos[]
                {
                        new BlockPos(1 , 0 , 0) ,
                        new BlockPos(-1 , 0 , 0) ,
                        new BlockPos(0 , 0 , 1) ,
                        new BlockPos(0 , 0 , -1)
                };

        for(int y = 0; y <= maxY.getValue(); y++) {
            for (BlockPos offset : offsets) {
                //check crystal
                BlockPos crystalPos = mypos.add(offset.getX(), y , offset.getZ());
                if(!CrystalUtil.hasCrystal(crystalPos)
                        && crystalSync.getValue()) continue;

                List<BlockPos> pistonPos = new ArrayList<>();
                //find Piston
                //normal
                BlockPos noOldCandy = crystalPos.add(offset);
                //side
                BlockPos sidePos0 = crystalPos.add(offset.getZ() , 0 , offset.getX());
                BlockPos sidePos1 = crystalPos.add(offset.getZ() * -1 , 0 , offset.getX() * -1);
                //1 block
                BlockPos noSushi0 = noOldCandy.add(offset);
                //normal side
                BlockPos noSushi1 = noOldCandy.add(offset.getZ() , 0 , offset.getX());
                BlockPos noSushi2 = noOldCandy.add(offset.getZ() * -1 , 0 , offset.getX() * -1);
                //1 block normal side
                BlockPos noSushi3 = noSushi1.add(offset);
                BlockPos noSushi4 = noSushi2.add(offset);

                add(pistonPos , noOldCandy);
                add(pistonPos , sidePos0);
                add(pistonPos , sidePos1);
                add(pistonPos , noSushi0);
                add(pistonPos , noSushi1);
                add(pistonPos , noSushi2);
                add(pistonPos , noSushi3);
                add(pistonPos , noSushi4);
                //ahhhh this is bad
                List<BlockPos> imNoob = new ArrayList<>();
                pistonPos.forEach(b -> imNoob.add(b.add(0 , 1 , 0)));
                imNoob.forEach(b -> pistonPos.add(b));


                for(BlockPos piston : pistonPos)
                    if(isPiston(piston)) {
                        this.pistonPos.add(piston);
                        if(CrystalUtil.hasCrystal(crystalPos))
                            PAcrystal = CrystalUtil.getCrystal(crystalPos);
                    }
            }
        }
    }

    public void add(List<BlockPos> target , BlockPos base)
    {
        target.add(base.add(0  ,1 , 0));
    }

    public boolean isPiston(BlockPos pos)
    {
        return BlockUtil.getBlock(pos) == Blocks.PISTON
                || BlockUtil.getBlock(pos) == Blocks.STICKY_PISTON;
    }

    public void swingArm()
    {
        EnumHand arm = swingArm.getValue() == Arm.Offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        if(swingArm.getValue() != Arm.None) mc.player.swingArm(arm);
    }

    public void breakCrystal(Entity crystal)
    {
        if(!(crystal instanceof EntityEnderCrystal)) return;

        if (packetBreak.getValue())
            mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        else
            mc.playerController.attackEntity(mc.player, crystal);
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
}
