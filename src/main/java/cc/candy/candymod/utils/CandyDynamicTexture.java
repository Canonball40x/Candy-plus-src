package cc.candy.candymod.utils;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

/*
* From Salhack
* */
public class CandyDynamicTexture extends DynamicTexture{
    private int Height;
    private int Width;
    private BufferedImage m_BufferedImage;
    private ResourceLocation m_TexturedLocation;
    private ImageFrame m_Frame;

    public CandyDynamicTexture(BufferedImage bufferedImage, int p_Height, int p_Width)
    {
        super(bufferedImage);
        m_Frame = null;
        m_BufferedImage = bufferedImage;

        Height = p_Height;
        Width = p_Width;
    }

    public int GetHeight()
    {
        return Height;
    }

    public int GetWidth()
    {
        return Width;
    }

    public final DynamicTexture GetDynamicTexture()
    {
        return (DynamicTexture)this;
    }

    public final BufferedImage GetBufferedImage()
    {
        return m_BufferedImage;
    }

    public void SetResourceLocation(ResourceLocation dynamicTextureLocation)
    {
        m_TexturedLocation = dynamicTextureLocation;
    }

    public final ResourceLocation GetResourceLocation()
    {
        return m_TexturedLocation;
    }

    public void SetImageFrame(final ImageFrame p_Frame)
    {
        m_Frame = p_Frame;
    }

    /// used for gifs
    public final ImageFrame GetFrame()
    {
        return m_Frame;
    }
}
