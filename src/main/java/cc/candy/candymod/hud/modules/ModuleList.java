package cc.candy.candymod.hud.modules;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleList extends Hud {
    public Setting<Boolean> shadow = register(new Setting("Shadow" , false));
    public Setting<Color> color = register(new Setting("Color" , new Color(255 , 255 , 255 , 255)));
    public Setting<Boolean> background = register(new Setting("Background" , true));
    public Setting<Color> backcolor = register(new Setting("BGColor" , new Color(40 , 40 , 40 , 70) , v -> background.getValue()));
    public Setting<Boolean> edge = register(new Setting("Edge" , true));
    public Setting<Color> edgeColor = register(new Setting("EGColor" , new Color(255 , 255 , 255 , 150) , v -> edge.getValue()));

    public ModuleList(){
        super("ModuleList" , 0 , 110);
    }

    @Override
    public void onRender(){
        float y = this.y.getValue();
        float size = 1.0F;
        float _width = 0.0F;
        float height = RenderUtil.getStringHeight(size);

        List<Module> sortedModuleList = new ArrayList<>(CandyMod.m_module.modules);
        Collections.sort(sortedModuleList, (c1, c2) -> c1.name.compareToIgnoreCase(c2.name));

        for(Module module : sortedModuleList){
            if(!module.isEnable) continue;

            String name = module.name;
            float width = RenderUtil.getStringWidth(name , size);
            if(width > _width) _width = width;

            if(background.getValue()) {
                RenderUtil.drawRect(x.getValue(), y, width + (20 * size), height + (10 * size), ColorUtil.toRGBA(backcolor.getValue()));
                if (edge.getValue())
                    RenderUtil.drawRect(x.getValue(), y, (2 * size), height + (10 * size), ColorUtil.toRGBA(edgeColor.getValue()));
            }

            RenderUtil.drawString(name , x.getValue() + 10 , y + 5 , ColorUtil.toRGBA(color.getValue()) , shadow.getValue() , size);

            y += height + 10;
        }
        y -= height + 11;

        this.width = _width + 20;
        this.height = y - this.y.getValue();
    }
}
