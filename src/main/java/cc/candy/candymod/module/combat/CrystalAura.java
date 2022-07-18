package cc.candy.candymod.module.combat;

import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.tools.nsc.Global;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CrystalAura extends Module {

    public Setting<Boolean> place = register(new Setting("Place" , true));
    public Setting<Float> placeDelay = register(new Setting("PlaceDelay" , 6.0F , 16.0F , 0.0F , v -> place.getValue()));
    public Setting<Float> placeRange = register(new Setting("PlaceRange" , 7.0F , 16.0F , 1.0F , v -> place.getValue()));
    public Setting<Float> wallRangePlace = register(new Setting("WallRangePlace" , 4.0F , 16.0F , 1.0F , v -> place.getValue()));
    public Setting<Boolean> placeSwing = register(new Setting("Swing" , false));
    public Setting<Boolean> autoSwitch = register(new Setting("Switch" , true));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , false , v -> autoSwitch.getValue()));
    public Setting<Boolean> opPlace = register(new Setting("1.13" , false , v -> place.getValue()));

    public Setting<Boolean> explode = register(new Setting("Explode" , true));
    public Setting<Boolean> predict = register(new Setting("Predict" , true));
    public Setting<Float> explodeDelay = register(new Setting("ExplodeDelay" , 6.0F , 16.0F , 0.0F , v -> explode.getValue()));
    public Setting<Float> breakRange = register(new Setting("ExplodeRange" , 6.0F , 16.0F , 1.0F , v -> explode.getValue()));
    public Setting<Float> wallRangeBreak = register(new Setting("WallRangeBreak" , 3.0F , 16.0F , 1.0F , v -> explode.getValue()));
    public Setting<Boolean> packetBreak = register(new Setting("PacketBreak" , true));
    public Setting<swingArm> swing = register(new Setting("SwingArm" , swingArm.Mainhand));
    public Setting<Boolean> packetSwing = register(new Setting("PacketSwing" , true , v -> swing.getValue() != swingArm.None));

    public Setting<Boolean> predictHit = register(new Setting("PredictHit" , false));
    public Setting<Integer> amount = register(new Setting("Amount" , 1 , 15 , 1 , v -> predictHit.getValue()));
    public Setting<Integer> amountOffset = register(new Setting("Offset" , 1 , 10 , 0 , v -> predictHit.getValue()));
    public Setting<Boolean> checkOtherEntity = register(new Setting("OtherEntity" , false , v -> predictHit.getValue()));

    public Setting<Boolean> ignoreSelfDmg = register(new Setting("IgnoreSelfDamage" , false));
    public Setting<Float> maxSelf = register(new Setting("MaxSelfDamage" , 5.0F , 36.0F , 0F , v -> !ignoreSelfDmg.getValue()));
    public Setting<Float> minDmg = register(new Setting("MinDamage" , 3.0F , 36.0F , 0F));
    public Setting<Boolean> smartMode = register(new Setting("SmartMode" , true));
    public Setting<Float> dmgError = register(new Setting("DamageError" , 3.0F , 15.0F , 1.0F , v -> smartMode.getValue()));
    public Setting<Boolean> antiSuicide = register(new Setting("AntiSuicide" , true));
    public Setting<Float> pauseHealth = register(new Setting("RequireHealth" , 3.0F , 36.0F , 0.0F));

    public Setting<Boolean> betterFps = register(new Setting("BetterFps" , true));

    public EntityPlayer target;
    public int lastEntityID = -1;
    public Timer placeTimer , breakTimer = new Timer();

    public enum swingArm{
        Mainhand ,
        Offhand ,
        None
    }

    public CrystalAura(){
        super("CrystalAura" , Categories.COMBAT , false , false);
    }

    @Override
    public void onTick(){
        doCrystalAura();
    }

    public void doCrystalAura(){
        try{
            if(nullCheck()) return;
            target = PlayerUtil.getNearestPlayer(35.0F);
            if(target == null) return;
            if(placeTimer == null || breakTimer == null) {
                placeTimer = new Timer();
                breakTimer = new Timer();
            }

            if(pauseHealth.getValue() > mc.player.getHealth()) return;
            if(place.getValue()) doPlace();
            if(explode.getValue()) doBreak();
        }
        catch (Exception e){

        }

    }

    public void doPlace(){
        if(placeTimer.passedDms(placeDelay.getValue())){
            placeTimer.reset();

            EnumHand hand;
            if(autoSwitch.getValue()){
                if(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                    hand = EnumHand.OFF_HAND;
                }
                else{
                    int crystalSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
                    if(crystalSlot == -1) return;
                    setItem(crystalSlot);
                    hand = EnumHand.MAIN_HAND;
                }
            }
            else{
                if(mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) hand = EnumHand.MAIN_HAND;
                else if(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) hand = EnumHand.OFF_HAND;
                else return;
            }

            double maxDmg = 0.0;
            List<CrystalPos> posList = new ArrayList<>();
            List<BlockPos> possiblePlacePositions = CrystalUtil.possiblePlacePositions(placeRange.getValue() , true , opPlace.getValue());
            for(BlockPos pos : possiblePlacePositions){
                if(!CrystalUtil.canSeePos(pos) && PlayerUtil.getDistance(pos) > wallRangePlace.getValue()) continue;
                double selfDamage = CrystalUtil.calculateDamage(pos.getX() + 0.5 , pos.getY() + 1 , pos.getZ() + 0.5 , mc.player);
                if(selfDamage > maxSelf.getValue() && !ignoreSelfDmg.getValue()) continue;
                double enemyDamage = CrystalUtil.calculateDamage(pos.getX() + 0.5 , pos.getY() + 1 , pos.getZ() + 0.5 , target);
                if(enemyDamage < minDmg.getValue()) continue;
                if(selfDamage > enemyDamage && (Math.abs(selfDamage - enemyDamage) >= dmgError.getValue()) && smartMode.getValue()) continue;
                if(enemyDamage > maxDmg) maxDmg = enemyDamage;

                posList.add(new CrystalPos(pos , enemyDamage));
            }
            double finalMaxDmg = maxDmg;
            CrystalPos _bestPos = posList.stream().filter(e -> finalMaxDmg <= e.dmg)
                    .min(Comparator.comparing(e -> PlayerUtil.getDistance(e.pos))).orElse(null);
            if(_bestPos == null) return;
            BlockPos bestPos = _bestPos.pos;
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bestPos , EnumFacing.UP , hand , 0.5F , 0.5F , 0.5F));
            if(placeSwing.getValue()) mc.player.connection.sendPacket(new CPacketAnimation(hand));

            //predicthit
            if(predictHit.getValue() && packetBreak.getValue() && lastEntityID != -1){
                for(int i = amountOffset.getValue(); i < amount.getValue(); i++){
                    CPacketUseEntity packet = new CPacketUseEntity();
                    packet.entityId = lastEntityID + i + 1;
                    packet.action = CPacketUseEntity.Action.ATTACK;
                    mc.player.connection.sendPacket(packet);
                }
            }

            restoreItem();
        }
    }

    public void doBreak(){
        if(breakTimer.passedDms(explodeDelay.getValue())){
            breakTimer.reset();
            List<Entity> crystalList = new ArrayList<>();
            for(Entity entity : new ArrayList<>(mc.player.world.loadedEntityList)){
                if(!(entity instanceof EntityEnderCrystal)) continue;
                if(entity.isDead) continue;

                if(isValidCrystal(entity.posX , entity.posY , entity.posZ)) crystalList.add(entity);
            }
            Entity bestCrystal = crystalList.stream().max(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
            if(bestCrystal == null) return;
            if(packetBreak.getValue()) mc.player.connection.sendPacket(new CPacketUseEntity(bestCrystal));
            else mc.playerController.attackEntity(mc.player , bestCrystal);
            EnumHand hand = swing.getValue() == swingArm.Mainhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
            if(swing.getValue() != swingArm.None){
                if(packetSwing.getValue()) mc.player.connection.sendPacket(new CPacketAnimation(hand));
                else mc.player.swingArm(hand);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPacketReceive(PacketEvent.Receive event){
        SPacketSpawnObject packet;
        if((event.getPacket() instanceof SPacketSpawnObject) && (packet = (SPacketSpawnObject) event.getPacket()).getType() == 51
                && explode.getValue() && predict.getValue() && packetBreak.getValue() && target != null){
            lastEntityID = packet.getEntityID();
            if(!isValidCrystal(packet.getX() , packet.getY() , packet.getZ())) return;

            CPacketUseEntity cpacket = new CPacketUseEntity();
            cpacket.entityId = packet.getEntityID();
            cpacket.action = CPacketUseEntity.Action.ATTACK;
            mc.player.connection.sendPacket(cpacket);
            EnumHand hand = swing.getValue() == swingArm.Mainhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
            if(swing.getValue() != swingArm.None){
                if(packetSwing.getValue()) mc.player.connection.sendPacket(new CPacketAnimation(hand));
                else mc.player.swingArm(hand);
            }
        }
        if(checkOtherEntity.getValue()){
            if(event.getPacket() instanceof SPacketSpawnExperienceOrb){
                lastEntityID = ((SPacketSpawnExperienceOrb)event.getPacket()).getEntityID();
            }
            if(event.getPacket() instanceof SPacketSpawnMob){
                lastEntityID = ((SPacketSpawnMob)event.getPacket()).getEntityID();
            }
            if(event.getPacket() instanceof SPacketSpawnPainting){
                lastEntityID = ((SPacketSpawnPainting)event.getPacket()).getEntityID();
            }
            if(event.getPacket() instanceof SPacketSpawnPlayer){
                lastEntityID = ((SPacketSpawnPlayer)event.getPacket()).getEntityID();
            }
        }
    }

    public boolean isValidCrystal(double posX , double posY , double posZ){
        BlockPos pos = new BlockPos(posX , posY , posZ);
        if(PlayerUtil.getDistance(pos) > breakRange.getValue()) return false;
        if(!CrystalUtil.canSeePos(pos) && PlayerUtil.getDistance(pos) > wallRangeBreak.getValue()) return false;

        double selfDamage = CrystalUtil.calculateDamage(posX , posY , posZ , mc.player);
        if(selfDamage > maxSelf.getValue() && !ignoreSelfDmg.getValue()) return false;
        if(mc.player.getHealth() - selfDamage <= 0 && antiSuicide.getValue()) return false;
        double enemyDamage = CrystalUtil.calculateDamage(posX , posY , posZ , target);
        if(enemyDamage < minDmg.getValue()) return false;
        if(selfDamage > enemyDamage && (Math.abs(selfDamage - enemyDamage) >= dmgError.getValue()) && smartMode.getValue()) return false;
        return true;
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

    public class CrystalPos{
        public BlockPos pos;
        public double dmg;

        public CrystalPos(BlockPos pos , double dmg){
            this.pos = pos;
            this.dmg = dmg;
        }
    }
}
