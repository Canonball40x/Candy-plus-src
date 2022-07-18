package cc.candy.candymod.gui.clickguis.vapegui.components;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.gui.clickguis.vapegui.Component;
import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.render.HUDEditor;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;
import cc.candy.candymod.utils.StringUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Panel extends Component {
    public boolean open;
    public List<ModuleButton> moduleButtonList = new ArrayList<>();
    public Module.Categories category;
    public Panel(Module.Categories category , float x , float y){
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = 110;
        this.height = 22;
        this.open = true;
        CandyMod.m_module.getModulesWithCategories(category).forEach(m -> moduleButtonList.add(new ModuleButton(m , x)));
    }

    @Override
    public void onRender(int mouseX , int mouseY){
        RenderUtil.drawRect(x , y , width , height , panelColor);
        float namey = getCenter(y , height , RenderUtil.getStringHeight(1.0F));
        RenderUtil.drawString(StringUtil.getName(category.name()) , x + 5 , namey , white , false , 1.0F);

        AtomicReference<Float> y = new AtomicReference<>(this.y + height);
        moduleButtonList.forEach(m -> y.updateAndGet(v -> v + m.doRender(v , mouseX , mouseY)));
    }

    @Override
    public void onMouseClicked(int mouseX , int mouseY , int mouseButton){
        if(isMouseHovering(mouseX , mouseY) && MouseUtil.MOUSE_RIGHT == mouseButton) open = !open;
        if(open) moduleButtonList.forEach(m -> m.onMouseClicked(mouseX , mouseY , mouseButton));
    }
}
