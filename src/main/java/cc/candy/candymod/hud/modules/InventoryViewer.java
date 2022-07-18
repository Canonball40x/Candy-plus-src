package cc.candy.candymod.hud.modules;

import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.RenderUtil;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class InventoryViewer extends Hud {
    public InventoryViewer(){
        super("InventoryViewer" , 150 , 100);
    }

    @Override
    public void onRender(){
        if(nullCheck()) return;

        RenderUtil.drawRect(this.x.getValue() - 6 , this.y.getValue() - 6 , 162 + 5 + 5 + 8 , 54 + 6 + 6 + 6 , ColorUtil.toRGBA(0,0,0));
        RenderUtil.drawRect(this.x.getValue() - 5 , this.y.getValue() -5 , 162 + 5 + 5 + 8 , 54 + 5 + 5 + 5 , ColorUtil.toRGBA(40,40,40));

        float _x = 0;
        float _y = 0;
        int c = 0;
        int scale = 19;
        InventoryPlayer inv = mc.player.inventory;
        for(int i = 9; i < 36; i++){
            ItemStack item = inv.getStackInSlot(i);
            RenderUtil.renderItem(item , x.getValue() + _x + 3 , y.getValue() + _y + 3);
            _x += scale;
            c++;
            if(c == 9) {
                _x = 0;
                _y += scale;
                c = 0;
            }
        }

        this.width = 162 + 3 + 3;
        this.height = 54 + 3 + 3;
    }
}
