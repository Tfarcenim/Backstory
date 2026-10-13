package tfar.backstory.network.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.backstory.network.PacketHandler;

public record C2SSetPlayerBackstoryPacket(int entityID) implements C2SModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSetPlayerBackstoryPacket> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, C2SSetPlayerBackstoryPacket::entityID, C2SSetPlayerBackstoryPacket::new);

    public static final CustomPacketPayload.Type<C2SSetPlayerBackstoryPacket> TYPE = new CustomPacketPayload.Type<>(
            PacketHandler.packet(C2SSetPlayerBackstoryPacket.class));

    public void handleServer(ServerPlayer player) {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

