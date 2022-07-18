package cc.candy.candymod.module.combat;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.exploit.InstantMine;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.BlockUtil;
import cc.candy.candymod.utils.InventoryUtil;
import cc.candy.candymod.utils.PlayerUtil;
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

public class AutoCity extends Module {
    public Setting<Target> targetType = register(new Setting("Target" , Target.Nearest));
    public Setting<Float> targetRange = register(new Setting("Target Range" , 10F , 20F , 0F));
    public Setting<Float> range = register(new Setting("Range" , 10F , 20F , 0F));
    public Setting<Boolean> instantBreak = register(new Setting("InstantBreak" , true));
    public Setting<Boolean> noSwing = register(new Setting("NoSwing" , false));
    public Setting<Boolean> switcH = register(new Setting("Switch" , false));
    public Setting<Boolean> noSuicide = register(new Setting("NoSuicide" , true));

    public EntityPlayer target = null;
    public BlockPos breakPos = null;

    public enum Target
    {
        Nearest ,
        Looking ,
        Best
    }

    public AutoCity()
    {
        super("AutoCity" , Categories.COMBAT , false , false);
    }

    @Override
    public void onEnable()
    {
        if(nullCheck()) return;

        target = findTarget();
        if(target == null) {
            sendMessage("Cannot find target! disabling...");
            disable();
			return;
        }
        if(findSpace(target) == -1) {
            sendMessage("Cannot find space! disabling...");
            disable();
			return;
        }

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

    public int findSpace(final EntityPlayer target)
    {
        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);
        BlockPos base = new BlockPos(target.posX , target.posY , target.posZ);

        BlockPos[] offsets = new BlockPos[]
                {
                        new BlockPos(1 , 0 , 0) ,
                        new BlockPos(-1 , 0 , 0) ,
                        new BlockPos(0 , 0 ,1) ,
                        new BlockPos(0 , 0 , -1)
                };

        List<CitySpace> spaces = new ArrayList<>();
        for(BlockPos offset : offsets)
        {
            CitySpace pos = new CitySpace();

            //get pos
            BlockPos breakPos = base.add(offset);
            if(BlockUtil.getBlock(breakPos) != Blocks.OBSIDIAN
                    && BlockUtil.getBlock(breakPos) != Blocks.ENDER_CHEST)
            {
                continue;
            }
            if(noSuicide.getValue())
            {
                boolean shouldSkip = false;
                for(BlockPos s : offsets)
                {
                    BlockPos spos = mypos.add(s);
                    if(spos.equals(breakPos)) shouldSkip = true;
                }

                if(shouldSkip) continue;
            }

            pos.setPos(breakPos);

            //get level
            BlockPos levelPos = breakPos.add(offset);
            if(BlockUtil.getBlock(levelPos) != Blocks.AIR)
                pos.setLevel(0);
            else if(BlockUtil.getBlock(levelPos.add(0 , 1 , 0)) != Blocks.AIR)
                pos.setLevel(1);
            else
                pos.setLevel(2);

            spaces.add(pos);
        }

        CitySpace space = spaces.stream()
                .filter(s -> PlayerUtil.getDistance(s.pos) <= range.getValue())
                .max(Comparator.comparing(s -> s.level + (range.getValue() - PlayerUtil.getDistance(s.pos)))).orElse(null);
        if(space == null) return -1;
        breakPos = space.pos;
        return space.level;
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
                    .max(Comparator.comparing(this::findSpace)).orElse(null);
        }
        return target;

    }

    public class CitySpace
    {
        public BlockPos pos;
        public int level = -1;

        public void setPos(BlockPos pos)
        {
            this.pos = pos;
        }

        public void setLevel(int level)
        {
            this.level = level;
        }
    }

}
