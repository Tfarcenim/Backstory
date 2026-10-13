package tfar.backstory;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.spongepowered.asm.mixin.MixinEnvironment;
import tfar.backstory.attachments.CommonDataAttachments;
import tfar.backstory.attachments.DataAttachmentUtil;
import tfar.backstory.network.PacketHandler;
import tfar.backstory.network.client.S2COpenBackstoryScreenPacket;
import tfar.backstory.platform.NeoForgePlatformHelper;

@Mod(Backstory.MOD_ID)
public class BackstoryNeoForge {

    public BackstoryNeoForge(IEventBus eventBus) {
        eventBus.addListener(this::register);
        eventBus.addListener(this::network);
        NeoForge.EVENT_BUS.addListener(this::commands);
        NeoForge.EVENT_BUS.addListener(this::playerTick);
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        Backstory.init();

    }

    void playerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer && !DataAttachmentUtil.hasBackstory(player)) {
            int tick = player.tickCount;
            if (tick == Backstory.WAIT_TIME) {
                S2COpenBackstoryScreenPacket.EDIT.send(serverPlayer);
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
}