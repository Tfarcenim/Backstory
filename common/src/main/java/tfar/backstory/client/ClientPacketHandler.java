package tfar.backstory.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import tfar.backstory.network.client.S2CBackstoryScreenPacket;

public class ClientPacketHandler {
    public static void handle(S2CBackstoryScreenPacket packet) {
        switch (packet.manage()) {
            case EDIT -> Minecraft.getInstance().setScreen(new BackstoryEditScreen(
                                Component.literal("Create Backstory"), packet.player(), packet.backstory(),packet.book_color()));
            case VIEW -> Minecraft.getInstance().setScreen(new BackstoryViewScreen(
                    Component.literal("Create Backstory"), packet.player(), packet.backstory(),packet.book_color()));

        }
    }
}
