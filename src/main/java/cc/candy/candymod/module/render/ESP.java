package cc.candy.candymod.module.render;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.GeometryMasks;
import cc.candy.candymod.utils.RenderUtil3D;
import net.minecraft.block.BlockBarrier;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;
import java.util.stream.Collectors;

public class ESP extends Module {
    public Setting<Float> width = register(new Setting("Width" , 1.5F , 5.0F , 0.5F));
    public Setting<Color> color = register(new Setting("Color" , new Color(255 , 255 , 255 , 255)));
    public ESP(){
        super("ESP" , Categories.RENDER , false , false );
    }

    @Override
    public void onRender3D(){
        if(nullCheck()) return;

        for(EntityPlayer player : mc.world.playerEntities
                .stream().filter(e -> e.entityId != mc.player.entityId).collect(Collectors.toList())){
            drawESP(player);
        }
    }

    public void drawESP(EntityPlayer player){
        GlStateManager.pushMatrix();
        AxisAlignedBB bb = player.boundingBox;
        double z = bb.minZ + (bb.maxZ - bb.minZ) / 2.0;
        RenderUtil3D.drawLine(bb.minX , bb.maxY , z , bb.maxX , bb.maxY , z , color.getValue() , width.getValue());
        RenderUtil3D.drawLine(bb.minX , bb.minY , z , bb.maxX , bb.minY , z , color.getValue() , width.getValue());
        RenderUtil3D.drawLine(bb.minX , bb.minY , z , bb.minX , bb.maxY , z , color.getValue() , width.getValue());
        RenderUtil3D.drawLine(bb.maxX , bb.minY , z , bb.maxX , bb.maxY , z , color.getValue() , width.getValue());
        //hp render
        double x = bb.minX - 0.28;
        double y = bb.minY;
        double width = 0.04;
        double height = bb.maxY - bb.minY;
        RenderUtil3D.drawRect(x , y , z , width , height , new Color(0 , 0 , 0 , 255) , 255 , GeometryMasks.Quad.ALL);
        RenderUtil3D.drawRect(x , y , z , width , height * (player.getHealth() / 36.0) , getHealthColor((int) player.getHealth()) , 255 , GeometryMasks.Quad.ALL);
        GlStateManager.popMatrix();
    }

    private static Color getHealthColor(int health) {
        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }

        int red = 0;
        int green = 0;
        if(health > 18){
            red = (int) ((36 - health) * 14.1666666667);
            green = 255;
        }
        else{
            red = 255;
            green = (int) (255 - ((18 - health) * 14.1666666667));
        }

        return new Color(red, green, 0, 255);
    }
}
