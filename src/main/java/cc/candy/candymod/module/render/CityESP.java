package cc.candy.candymod.module.render;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.BlockUtil;
import cc.candy.candymod.utils.GeometryMasks;
import cc.candy.candymod.utils.PlayerUtil;
import cc.candy.candymod.utils.RenderUtil3D;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

public class CityESP extends Module {
    public Setting<Float> range = register(new Setting("Range" , 10.0F , 12.0F , 1.0F));
    public Setting<Color> color = register(new Setting("Color" , new Color(230 , 50 , 50 , 100)));
    public Setting<Boolean> outline = register(new Setting("Outline" , false));
    public Setting<Float> width = register(new Setting("Width" , 3.0F , 6.0F , 0.1F , v -> outline.getValue()));

    public CityESP(){
        super("CityESP" , Categories.RENDER , false , false);
    }

    @Override
    public void onRender3D(){
        List<EntityPlayer> players = mc.world.playerEntities
                .stream().filter(e -> e.entityId != mc.player.entityId).collect(Collectors.toList());
        for(EntityPlayer player : players){
            BlockPos[] surroundOffset = new BlockPos[]
                    {
                            new BlockPos(1 , 0 , 0) ,
                            new BlockPos(-1 , 0 , 0) ,
                            new BlockPos(0 , 0 , 1) ,
                            new BlockPos(0 , 0 , -1)
                    };

            for(BlockPos offset : surroundOffset){
                BlockPos position = new BlockPos(player.posX , player.posY , player.posZ).add(offset);
                if(PlayerUtil.getDistance(position) > range.getValue()) continue;
                if(BlockUtil.getBlock(position) != Blocks.OBSIDIAN) continue;
                RenderUtil3D.drawBox(position, 1.0, color.getValue(), GeometryMasks.Quad.ALL);
                if(outline.getValue()){
                    RenderUtil3D.drawBoundingBox(position , 1.0 , width.getValue() , color.getValue());
                }
            }
        }
    }
}
