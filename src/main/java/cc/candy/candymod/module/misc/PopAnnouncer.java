package cc.candy.candymod.module.misc;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.combat.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.common.MinecraftForge;

public class PopAnnouncer extends Module {
    public PopAnnouncer()
    {
        super("PopAnnouncer" , Categories.MISC , false , false);
    }

    @Override
    public void onTotemPop(EntityPlayer player)
    {
        if(player == null || nullCheck()) return;

        //PistonAura
        Module piston = CandyMod.m_module.getModuleWithClass(PistonAura.class);
		Module pistonRe = CandyMod.m_module.getModuleWithClass(PistonAuraRewrite.class);
        Module pistonRe2 = CandyMod.m_module.getModuleWithClass(PistonAuraRewrite2.class);
        Module pistonRe3 = CandyMod.m_module.getModuleWithClass(PistonAuraRewrite3.class);

        if((piston.isEnable || pistonRe.isEnable || pistonRe2.isEnable || pistonRe3.isEnable)
                && (player.equals(PistonAura.target) || player.equals(PistonAuraRewrite.target) || player.equals(PistonAuraRewrite2.target) || player.equals(PistonAuraRewrite3.target)))
        {
            sendChat("ez pop " + player.getName());
            return;
        }
        Module cev = CandyMod.m_module.getModuleWithClass(CevBreaker.class);
        if(cev.isEnable && player.equals(CevBreaker.target))
        {
            sendChat("keep popping " + player.getName());
            return;
        }
        Module civ = CandyMod.m_module.getModuleWithClass(CivBreaker.class);
        if(civ.isEnable && player.equals(CivBreaker.target))
        {
            sendChat("just keep popping " + player.getName());
            return;
        }
    }

    public void sendChat(String str)
    {
        mc.player.connection.sendPacket(new CPacketChatMessage(str));
    }
}
