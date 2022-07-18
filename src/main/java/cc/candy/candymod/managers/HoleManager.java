package cc.candy.candymod.managers;

import cc.candy.candymod.utils.BlockUtil;
import cc.candy.candymod.utils.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//from oyvey
public class HoleManager extends Manager {
    private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(PlayerUtil.getOffsets(0, true));
    private final List<BlockPos> midSafety = new ArrayList<BlockPos>();
    private List<BlockPos> holes = new ArrayList<BlockPos>();

    public void update() {
        if (!nullCheck()) {
            this.holes = this.calcHoles();
        }
    }

    public List<BlockPos> getHoles() {
        return this.holes;
    }

    public List<BlockPos> getMidSafety() {
        return this.midSafety;
    }

    public List<BlockPos> getSortedHoles() {
        this.holes.sort(Comparator.comparingDouble(hole -> mc.player.getDistanceSq(hole)));
        return this.getHoles();
    }

    public List<BlockPos> calcHoles() {
        ArrayList<BlockPos> safeSpots = new ArrayList<BlockPos>();
        this.midSafety.clear();
        List<BlockPos> positions = BlockUtil.getSphere(new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ), 6.0f, 6, false, true, 0);
        for (BlockPos pos : positions) {
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
                continue;
            boolean isSafe = true;
            boolean midSafe = true;
            for (BlockPos offset : surroundOffset) {
                Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
                if (BlockUtil.isBlockUnSolid(block)) {
                    midSafe = false;
                }
                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                    isSafe = false;
                }
            }
            if (isSafe) {
                safeSpots.add(pos);
            }
            if (!midSafe) continue;
            this.midSafety.add(pos);
        }
        return safeSpots;
    }

    public List<BlockPos> calcHoles(float range) {
        ArrayList<BlockPos> safeSpots = new ArrayList<BlockPos>();
        this.midSafety.clear();
        List<BlockPos> positions = BlockUtil.getSphere(new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ), range, 6, false, true, 0);
        for (BlockPos pos : positions) {
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
                continue;
            boolean isSafe = true;
            boolean midSafe = true;
            for (BlockPos offset : surroundOffset) {
                Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
                if (BlockUtil.isBlockUnSolid(block)) {
                    midSafe = false;
                }
                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                    isSafe = false;
                }
            }
            if (isSafe) {
                safeSpots.add(pos);
            }
            if (!midSafe) continue;
            this.midSafety.add(pos);
        }
        return safeSpots;
    }

    public boolean isSafe(BlockPos pos) {
        boolean isSafe = true;
        for (BlockPos offset : surroundOffset) {
            Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
            if (!BlockUtil.isBlockUnSafe(block)) continue;
            isSafe = false;
            break;
        }
        return isSafe;
    }

    public boolean inHole()
    {
        if(mc.player == null) return false;
        return isSafe(new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ));
    }

    public boolean inHole(EntityPlayer player)
    {
        if(player == null) return false;
        return isSafe(new BlockPos(player.posX , player.posY , player.posZ));
    }
}
