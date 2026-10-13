package tfar.backstory.network;

import net.minecraft.resources.ResourceLocation;
import tfar.backstory.Backstory;
import tfar.backstory.network.client.S2COpenBackstoryScreenPacket;
import tfar.backstory.platform.Services;


import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {

        Services.PLATFORM.registerClientPacket(S2COpenBackstoryScreenPacket.TYPE, S2COpenBackstoryScreenPacket.STREAM_CODEC);

        ///////server to client
    }

    public static ResourceLocation packet(Class<?> clazz) {
        return Backstory.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
