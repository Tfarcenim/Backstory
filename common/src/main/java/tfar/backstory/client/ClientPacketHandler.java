package tfar.backstory.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import tfar.backstory.network.client.S2COpenBackstoryScreenPacket;

public class ClientPacketHandler {
    public static void handle(S2COpenBackstoryScreenPacket packet) {
        Minecraft.getInstance().setScreen(new BackstoryEditScreen(Component.literal("Create Backstory")));
    }
}
