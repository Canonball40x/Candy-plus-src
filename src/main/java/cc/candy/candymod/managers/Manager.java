package cc.candy.candymod.managers;


import cc.candy.candymod.utils.Util;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Manager implements Util {
    public void load()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void unload()
    {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public boolean nullCheck()
    {
        return mc.player == null;
    }

    public void sendMessage(String str)
    {
        mc.player.sendMessage(new ChatMessage(ChatFormatting.RED + "[Candy] " + ChatFormatting.GRAY + str));
    }

    public static class ChatMessage
            extends TextComponentBase {
        private final String text;

        public ChatMessage(String text) {
            Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
            Matcher matcher = pattern.matcher(text);
            StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                String replacement = matcher.group().substring(1);
                matcher.appendReplacement(stringBuffer, replacement);
            }
            matcher.appendTail(stringBuffer);
            this.text = stringBuffer.toString();
        }

        public String getUnformattedComponentText() {
            return this.text;
        }

        public ITextComponent createCopy() {
            return null;
        }

        public ITextComponent shallowCopy() {
            return new ChatMessage(this.text);
        }
    }
}
