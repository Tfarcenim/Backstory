package tfar.backstory;


import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import tfar.backstory.attachments.DataAttachmentUtil;
import tfar.backstory.datagen.BackstoryDatagen;
import tfar.backstory.network.PacketHandler;
import tfar.backstory.network.client.S2CBackstoryScreenPacket;
import tfar.backstory.platform.NeoForgePlatformHelper;
import tfar.backstory.platform.Services;

@Mod(Backstory.MOD_ID)
public class BackstoryNeoForge {

    public BackstoryNeoForge(IEventBus eventBus) {
        eventBus.addListener(this::register);
        eventBus.addListener(this::network);
        eventBus.addListener(BackstoryDatagen::gather);
        NeoForge.EVENT_BUS.addListener(this::commands);
        NeoForge.EVENT_BUS.addListener(this::playerTick);
        NeoForge.EVENT_BUS.addListener(this::editTabList);
        NeoForge.EVENT_BUS.addListener(this::editDisplayName);
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        Backstory.init();

    }

    void playerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer && !DataAttachmentUtil.hasCreatedBackstory(player)) {
            int tick = player.tickCount;
            if (tick == Backstory.WAIT_TIME) {
                Services.PLATFORM.sendToClient(
                        new S2CBackstoryScreenPacket(DataAttachmentUtil.getBackstory(player),player.getUUID(),
                                DataAttachmentUtil.getOrCreateBookColor(player),
                                S2CBackstoryScreenPacket.Manage.EDIT), serverPlayer);
            }
        }
    }

    void register(RegisterEvent event) {
        Backstory.register();
    }

    void network(RegisterPayloadHandlersEvent event) {
        NeoForgePlatformHelper.registrar = event.registrar(Backstory.MOD_ID);
        PacketHandler.registerPackets();
    }

    void commands(RegisterCommandsEvent event) {
        BackstoryCommands.register(event.getDispatcher());
    }

    void editDisplayName(PlayerEvent.NameFormat event) {
        Player player = event.getEntity();
        PlayerBackstory backstory = DataAttachmentUtil.getBackstory(player);

        if (backstory.pages().isEmpty()) {
            event.setDisplayname(player.getName());
        } else {
            event.setDisplayname(Component.literal(backstory.chosenName()));
        }
    }

    void editTabList(PlayerEvent.TabListNameFormat event) {
        Player player = event.getEntity();
        PlayerBackstory backstory = DataAttachmentUtil.getBackstory(player);

        if (backstory.pages().isEmpty()) {
            event.setDisplayName(player.getName());
        } else {
            event.setDisplayName(Component.literal(player.getName().getString() + " (" + backstory.chosenName() + ")"));
        }
    }
}