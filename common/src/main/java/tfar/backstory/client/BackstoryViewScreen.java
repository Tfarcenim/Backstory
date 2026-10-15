package tfar.backstory.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tfar.backstory.PlayerBackstory;

import java.util.UUID;

public class BackstoryViewScreen extends Screen {
    private final UUID playerUUID;
    private final PlayerBackstory backstory;

    protected BackstoryViewScreen(Component title, UUID playerUUID, PlayerBackstory backstory) {
        super(title);
        this.playerUUID = playerUUID;
        this.backstory = backstory;
    }
}
