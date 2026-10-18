package tfar.backstory.network.client;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.ResolvableProfile;
import tfar.backstory.MiscHelpers;
import tfar.backstory.PlayerBackstory;
import tfar.backstory.client.ClientPacketHandler;
import tfar.backstory.network.PacketHandler;
import tfar.backstory.platform.Services;

import java.util.UUID;

public record S2CBackstoryScreenPacket(PlayerBackstory backstory, ResolvableProfile player, int book_color, Manage manage) implements S2CModPacket {

    public enum Manage {
        EDIT,VIEW
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CBackstoryScreenPacket> STREAM_CODEC =
            StreamCodec.composite(PlayerBackstory.STREAM_CODEC, S2CBackstoryScreenPacket::backstory,
                    ResolvableProfile.STREAM_CODEC, S2CBackstoryScreenPacket::player,
                    ByteBufCodecs.INT, S2CBackstoryScreenPacket::book_color,
                    MiscHelpers.enumStreamCodec(Manage.class), S2CBackstoryScreenPacket::manage,
                    S2CBackstoryScreenPacket::new);

    public static final Type<S2CBackstoryScreenPacket> TYPE = new Type<>(
            PacketHandler.packet(S2CBackstoryScreenPacket.class));

    @Override
    public void handleClient() {
        ClientPacketHandler.handle(this);
    }

    public void send(ServerPlayer player) {
        Services.PLATFORM.sendToClient(this, player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
