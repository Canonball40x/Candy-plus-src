package cc.candy.candymod.managers;

import cc.candy.candymod.gui.font.CFont;
import cc.candy.candymod.gui.font.CFontRenderer;

import java.awt.*;

public class FontManager extends Manager{
    public CFontRenderer iconFont;
    public CFontRenderer fontRenderer;

    @Override
    public void load() {
        //iconFont = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/fonts/Icon.ttf", 22f, Font.PLAIN), true, false);
        fontRenderer = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/fonts/Geomanist-Regular.ttf", 18f, Font.PLAIN), true, false);
    }

    public int getWidth(String str){
        return fontRenderer.getStringWidth(str);
    }

    public int getHeight(){
        return fontRenderer.getHeight() + 2;
    }

    public void draw(String str, int x, int y, int color , float scale) {
        fontRenderer.drawString(str, x, y, color , scale);
    }

    public void draw(String str, int x, int y, Color color , float scale) {
        fontRenderer.drawString(str, x, y, color.getRGB() , scale);
    }

    public int getIconWidth(){
        return iconFont.getStringWidth("q");
    }

    public int getIconHeight(){
        return iconFont.getHeight();
    }

    public void drawIcon(int x, int y, int color , float scale) {
        iconFont.drawString("q", x, y, color , scale);
    }

    public void drawIcon(int x, int y, Color color , float scale) {
        iconFont.drawString("+", x, y, color.getRGB() , scale);
    }
}
