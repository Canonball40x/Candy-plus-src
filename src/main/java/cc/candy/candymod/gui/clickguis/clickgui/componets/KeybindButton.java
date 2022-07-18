package cc.candy.candymod.gui.clickguis.clickgui.componets;

import cc.candy.candymod.gui.clickguis.clickgui.Component;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.render.ClickGUI;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;
import org.lwjgl.input.Keyboard;

public class KeybindButton extends Component {
    private Module module;
    private boolean isWaiting = false;

    public KeybindButton(Module m , int x , int width , int height)
    {
        this.module = m;
        this.x = x;
        this.width = width;
        this.height = height;
    }

    @Override
    public void onRender2D(int y)
    {
        this.visible = true;
        this.y = y;
        float centeredY = y + (this.height - RenderUtil.getStringHeight(1.0F)) / 2;
        if(isWaiting)
        {
            RenderUtil.drawRect(x , y, width , height , enabledColor);
            RenderUtil.drawString("Key : ..." , x + 5, centeredY, whiteColor, false, 1.0f);
        }
        else {
            RenderUtil.drawRect(x , y, width , height , buttonColor);
            RenderUtil.drawString("Key : " + (module.key.getKey() != -1 ? Keyboard.getKeyName(module.key.getKey()) : "NONE")
                    , x + 5, centeredY, whiteColor, false, 1.0f);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(isMouseHovering(mouseX , mouseY) && mouseButton == MouseUtil.MOUSE_LEFT)
            isWaiting = !isWaiting;
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        if(isWaiting)
        {
            if(keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_ESCAPE) {
                if(!(module instanceof ClickGUI)){
                    module.setKey(-1);
                }
            }
            else {
                module.setKey(keyCode);
            }
            isWaiting = false;
        }
    }
}
