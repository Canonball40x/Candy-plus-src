package cc.candy.candymod.gui.clickguis.clickguinew.item;

import cc.candy.candymod.gui.clickguis.clickguinew.Component;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;
import org.lwjgl.input.Keyboard;

public class BindButton extends Component {
    public Module module;
    public boolean keyWaiting = false;

    public BindButton(Module module , float x){
        this.module = module;
        this.x = x;
        this.width = 100;
        this.height = 16;
    }

    @Override
    public float doRender(int mouseX , int mouseY , float x , float y) {
        this.x = x;
        this.y = y;
        RenderUtil.drawRect(x, y, 100, 16, color0);

        if(keyWaiting)
            RenderUtil.drawRect(x , y , 100 , 16 , ColorUtil.toRGBA(color));
        if(isMouseHovering(mouseX , mouseY))
            RenderUtil.drawRect(x, y, 100, 16, hovering);

        float bindy = getCenter(y, 16, RenderUtil.getStringHeight(1.0F));
        RenderUtil.drawString("Bind : " + (keyWaiting ? "..." : (module.key.key == -1 ? "NONE" : Keyboard.getKeyName(module.key.key)))
                , x + 6, bindy, ColorUtil.toRGBA(250, 250, 250), false, 1.0F);
        return 16.0F;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == MouseUtil.MOUSE_LEFT && isMouseHovering(mouseX , mouseY))
            keyWaiting = !keyWaiting;
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        if(keyWaiting)
        {
            if(keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_ESCAPE)
                module.setKey(-1);
            else
                module.setKey(keyCode);

            keyWaiting = false;
        }
    }
}
