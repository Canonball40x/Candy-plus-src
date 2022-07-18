package cc.candy.candymod.setting;

import org.lwjgl.input.Keyboard;

public class Bind {
    public int key = -1;

    public int getKey()
    {
        return key;
    }

    public void setKey(int Ikey)
    {
        key = Ikey;
    }

    public String getKeyname()
    {
        return key != -1 ? "None" : Keyboard.getKeyName(key);
    }

}
