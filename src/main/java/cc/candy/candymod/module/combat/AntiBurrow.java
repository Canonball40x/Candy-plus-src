package cc.candy.candymod.module.combat;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.exploit.InstantMine;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.BlockUtil;
import cc.candy.candymod.utils.InventoryUtil;
import cc.candy.candymod.utils.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AntiBurrow extends Module {
    public Setting<Target> targetType = register(new Setting("Target" , Target.Nearest));
    public Setting<Float> targetRange = register(new Setting("Target Range" , 10F , 20F , 0F));
    public Setting<Float> range = register(new Setting("Range" , 10F , 20F , 0F));
    public Setting<Boolean> instantBreak = register(new Setting("InstantBreak" , true));
    public Setting<Boolean> noSwing = register(new Setting("NoSwing" , false));
    public Setting<Boolean> switcH = register(new Setting("Switch" , false));
    public Setting<Boolean> obby = register(new Setting("Obby" , false));
    public Setting<Boolean> echest = register(new Setting("EChest" , false));

    public EntityPlayer target = null;
    public BlockPos breakPos = null;

    public enum Target
    {
        Nearest ,
        Looking ,
        Best
    }

    public AntiBurrow(){
        super("AntiBurrow" , Categories.COMBAT , false , false);
    }

    @Override
    public void onEnable(){
        if(nullCheck()) return;

        target = findTarget();
        if(target == null) {
            sendMessage("Cannot find target! disabling");
            disable();
            return;
        }
        if(!canBreak(getPos(target))){
            sendMessage("Target is not in block! disabling");
            return;
        }
        breakPos = getPos(target);

        sendMessage("Breaking...");
        if(!instantBreak.getValue())
        {
            if (!noSwing.getValue()) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            mc.player.connection.sendPacket(new CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos, EnumFacing.DOWN
            ));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, EnumFacing.DOWN
            ));
        }
        else
        {
            if(!noSwing.getValue()) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            CandyMod.m_module.getModuleWithClass(InstantMine.class).enable();
            InstantMine.startBreak(breakPos , EnumFacing.DOWN);
        }

        if(switcH.getValue())
        {
            int pickel = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
            if(pickel == -1) return ;
            mc.player.inventory.currentItem = pickel;
            mc.playerController.updateController();
        }

        disable();
    }

    public EntityPlayer findTarget()
    {
        EntityPlayer target = null;
        List<EntityPlayer> players = new ArrayList<>(mc.world.playerEntities);
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
        if(targetType.getValue() == Target.Best)
        {
            target = players.stream()
                    .filter(e -> canBreak(getPos(e))).min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
        }
        return target;
    }

    public BlockPos getPos(EntityPlayer player){
        return new BlockPos(player.posX , player.posY , player.posZ);
    }

    public boolean canBreak(BlockPos pos){
        Block block = BlockUtil.getBlock(pos);
        boolean can = false;
        if(block == Blocks.ANVIL) can = true;
        if(block == Blocks.ENDER_CHEST && echest.getValue()) can = true;
        if(block == Blocks.OBSIDIAN && obby.getValue()) can = true;
        return can;
    }
}
