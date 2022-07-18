package cc.candy.candymod.module.render;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.BlockUtil;
import cc.candy.candymod.utils.GeometryMasks;
import cc.candy.candymod.utils.RenderUtil3D;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BurrowESP extends Module {
    public Setting<Boolean> obby = register(new Setting("Only Obby" , false));
    public Setting<Color> color = register(new Setting("Color" , new Color(255, 64, 207 , 210)));

    public BurrowESP()
    {
        super("BurrowESP" , Categories.RENDER , false , false);
    }

    @Override
    public void onRender3D()
    {
        List<EntityPlayer> players = new ArrayList(mc.world.playerEntities);

        for (EntityPlayer player:
             players) {
            BlockPos pos = new BlockPos(player.posX , player.posY , player.posZ);
            if(BlockUtil.getBlock(pos) != Blocks.AIR &&
                    (!obby.getValue() || BlockUtil.getBlock(pos) == Blocks.OBSIDIAN))
            {
                RenderUtil3D.drawBox(pos , 1 , color.getValue() , GeometryMasks.Quad.ALL);
            }
        }
    }
}
