package cc.candy.candymod.module.render;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.*;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;

public class HoleESP extends Module {
    public Setting<Float> range = register(new Setting("Range" , 10.0F , 12.0F , 1.0F));
    public Setting<Color> obby = register(new Setting("ObbyColor" , new Color(230 , 50 , 50 , 100)));
    public Setting<Color> bedrock = register(new Setting("BedrockColor" , new Color(230 , 150 , 50 , 100)));
    public Setting<type> renderType = register(new Setting("RenderType" , type.Down));
    public Setting<Boolean> outline = register(new Setting("Outline" , false));
    public Setting<Float> width = register(new Setting("Width" , 3.0F , 6.0F , 0.1F , v -> outline.getValue()));

    public enum type{
        Full ,
        Down
    }

    public HoleESP(){
        super("HoleESP" , Categories.RENDER , false , false);
    }

    @Override
    public void onRender3D() {
        try {
            List<BlockPos> holes = CandyMod.m_hole.getHoles();
            for (BlockPos hole : holes) {
                if (PlayerUtil.getDistance(hole) > range.getValue()) continue;

                Color color = obby.getValue();
                if(HoleUtil.isBedrockHole(hole)) color = bedrock.getValue();

                if (renderType.getValue() == type.Full) {
                    RenderUtil3D.drawBox(hole, 1.0, color, GeometryMasks.Quad.ALL);
                } else {
                    RenderUtil3D.drawBox(hole, 1.0, color, GeometryMasks.Quad.DOWN);
                }

                if(outline.getValue()){
                    RenderUtil3D.drawBoundingBox(hole , 1.0 , width.getValue() , color);
                }
            }
        } catch (Exception ignored) {

        }

    }
}
