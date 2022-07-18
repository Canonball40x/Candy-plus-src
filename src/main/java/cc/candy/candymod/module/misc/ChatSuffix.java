package cc.candy.candymod.module.misc;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;

public class ChatSuffix extends Module {
    public Setting<Boolean> floppaGod = register(new Setting("FloppaGod" , false));

    public ChatSuffix()
    {
        super("ChatSuffix" , Categories.MISC , false , false);
    }

    @Override
    public void onPacketSend(PacketEvent.Send event)
    {
        if(event.packet instanceof CPacketChatMessage)
        {
            if (((CPacketChatMessage) event.getPacket()).getMessage().startsWith("/"))
                return;

            CPacketChatMessage p = (CPacketChatMessage) event.packet;
            String msg = p.getMessage();
            String newMsg = msg + toUnicode(" || " + (floppaGod.getValue() ? "FloppaGod" : CandyMod.NAME.toLowerCase()));

            if(newMsg.length() > 255)
                return;

            p.message = newMsg;
        }
    }

    //Thanks to boap for this bit.
    public String toUnicode(String s) {
        return s.toLowerCase()
                .replace("a", "\u1d00")
                .replace("b", "\u0299")
                .replace("c", "\u1d04")
                .replace("d", "\u1d05")
                .replace("e", "\u1d07")
                .replace("f", "\ua730")
                .replace("g", "\u0262")
                .replace("h", "\u029c")
                .replace("i", "\u026a")
                .replace("j", "\u1d0a")
                .replace("k", "\u1d0b")
                .replace("l", "\u029f")
                .replace("m", "\u1d0d")
                .replace("n", "\u0274")
                .replace("o", "\u1d0f")
                .replace("p", "\u1d18")
                .replace("q", "\u01eb")
                .replace("r", "\u0280")
                .replace("s", "\ua731")
                .replace("t", "\u1d1b")
                .replace("u", "\u1d1c")
                .replace("v", "\u1d20")
                .replace("w", "\u1d21")
                .replace("x", "\u02e3")
                .replace("y", "\u028f")
                .replace("z", "\u1d22");
    }

}
