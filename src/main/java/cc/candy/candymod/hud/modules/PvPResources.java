package cc.candy.candymod.hud.modules;

import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class PvPResources extends Hud {
    public Setting<Boolean> crystal = register(new Setting("Crystal" , true));
    public Setting<Boolean> xp = register(new Setting("Xp" , true));
    public Setting<Boolean> gap = register(new Setting("Gap" , true));
    public Setting<Boolean> totem = register(new Setting("Totem" , true));
    public Setting<Boolean> obby = register(new Setting("Obsidian" , true));
    public Setting<Boolean> piston = register(new Setting("Piston" , true));
    public Setting<Boolean> redstone = register(new Setting("RedStone" , true));
    public Setting<Boolean> torch = register(new Setting("Torch" , true , v -> redstone.getValue()));
    public Setting<Boolean> block = register(new Setting("Block" , true , v -> redstone.getValue()));

    public Setting<Boolean> shadow = register(new Setting("Shadow" , false));
    public Setting<Color> color = register(new Setting("Color" , new Color(255 , 255 , 255 , 255)));
    public Setting<Boolean> background = register(new Setting("Background" , false));
    public Setting<Color> backcolor = register(new Setting("BGColor" , new Color(40 , 40 , 40 , 60) , v -> background.getValue()));

    public PvPResources(){
        super("PvPResources" , 300 , 100);
    }

    @Override
    public void onRender(){
        float x = this.x.getValue();
        float y = this.y.getValue();

        if(this.crystal.getValue()) {
            renderItem(Items.END_CRYSTAL, getItemCount(Items.END_CRYSTAL), x, y);
            y += RenderUtil.getStringHeight(1.0F) + 13;
        }
        if(this.xp.getValue()) {
            renderItem(Items.EXPERIENCE_BOTTLE, getItemCount(Items.EXPERIENCE_BOTTLE), x, y);
            y += RenderUtil.getStringHeight(1.0F) + 13;
        }
        if(this.gap.getValue()) {
            renderItem(Items.GOLDEN_APPLE, getItemCount(Items.GOLDEN_APPLE), x, y);
            y += RenderUtil.getStringHeight(1.0F) + 13;
        }
        if(this.totem.getValue()) {
            renderItem(Items.TOTEM_OF_UNDYING, getItemCount(Items.TOTEM_OF_UNDYING), x, y);
            y += RenderUtil.getStringHeight(1.0F) + 13;
        }
        if(this.obby.getValue()) {
            renderBlock(Blocks.OBSIDIAN, getBlockCount(Blocks.OBSIDIAN), x, y);
            y += RenderUtil.getStringHeight(1.0F) + 13;
        }
        if(this.piston.getValue()) {
            renderBlock(Blocks.PISTON, getBlockCount(Blocks.PISTON) + getBlockCount(Blocks.STICKY_PISTON), x, y);
            y += RenderUtil.getStringHeight(1.0F) + 13;
        }
        if(this.redstone.getValue()) {
            if(this.block.getValue()) {
                renderBlock(Blocks.REDSTONE_BLOCK, getBlockCount(Blocks.REDSTONE_BLOCK), x, y);
                y += RenderUtil.getStringHeight(1.0F) + 13;
            }
            if(this.torch.getValue()) {
                renderBlock(Blocks.REDSTONE_TORCH, getBlockCount(Blocks.REDSTONE_TORCH), x, y);
                y += RenderUtil.getStringHeight(1.0F) + 13;
            }
        }

        y -= RenderUtil.getStringHeight(1.0F) + 13;
        width = (x + 20 + RenderUtil.getStringWidth(" : 64" , 1.0F)) - this.x.getValue();
        height = y - this.y.getValue();
    }

    public void renderItem(Item item , int count , float x , float y){
        RenderUtil.renderItem(new ItemStack(item) , x , y - 8 , false);
        RenderUtil.drawString(" : " + count , x + 20 , y , ColorUtil.toRGBA(color.getValue()) , shadow.getValue() , 1.0F);
    }

    public void renderBlock(Block block , int count , float x , float y){
        RenderUtil.renderItem(new ItemStack(block) , x , y - 10 , false);
        RenderUtil.drawString(" : " + count , x + 20 , y , ColorUtil.toRGBA(color.getValue()) , shadow.getValue() , 1.0F);
    }

    public int getItemCount(Item item){
        int count = 0;
        for(int i = 0; i < mc.player.inventory.getSizeInventory(); i++){
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if(itemStack.getItem() == item){
                count += itemStack.getCount();
            }
        }
        return count;
    }

    public int getBlockCount(Block block){
        int count = 0;
        for(int i = 0; i < mc.player.inventory.getSizeInventory(); i++){
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if(itemStack.getItem() instanceof ItemBlock)
                if(((ItemBlock)itemStack.getItem()).block == block) count += itemStack.getCount();
        }
        return count;
    }
}
