package tfar.backstory.network;

import net.minecraft.resources.ResourceLocation;
import tfar.backstory.Backstory;
import tfar.backstory.network.client.S2CBackstoryScreenPacket;
import tfar.backstory.network.server.C2SSetPlayerBackstoryPacket;
import tfar.backstory.platform.Services;


import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {

        Services.PLATFORM.registerClientPacket(S2CBackstoryScreenPacket.TYPE, S2CBackstoryScreenPacket.STREAM_CODEC);
        Services.PLATFORM.registerServerPacket(C2SSetPlayerBackstoryPacket.TYPE, C2SSetPlayerBackstoryPacket.STREAM_CODEC);

        ///////server to client
    }

    public static ResourceLocation packet(Class<?> clazz) {
        return Backstory.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
