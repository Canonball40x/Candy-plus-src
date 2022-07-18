package cc.candy.candymod.event.events.network;

import cc.candy.candymod.event.CandyEvent;
import net.minecraft.network.Packet;

public class PacketEvent extends CandyEvent {
    public final Packet<?> packet;

    public PacketEvent(final Packet<?> packet) {
        this.packet = packet;
    }

    public static class Receive extends PacketEvent {
        public Receive(final Packet<?> packet) {
            super(packet);
        }
    }

    public static class Send extends PacketEvent {
        public Send(final Packet<?> packet) {
            super(packet);
        }
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
