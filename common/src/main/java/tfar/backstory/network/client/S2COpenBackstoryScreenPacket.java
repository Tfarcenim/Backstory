package tfar.backstory.network.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.backstory.MiscHelpers;
import tfar.backstory.client.ClientPacketHandler;
import tfar.backstory.network.PacketHandler;
import tfar.backstory.platform.Services;

public enum S2COpenBackstoryScreenPacket implements S2CModPacket {
    EDIT,VIEW;

    public static final StreamCodec<RegistryFriendlyByteBuf, S2COpenBackstoryScreenPacket> STREAM_CODEC =
            MiscHelpers.altEnumStreamCodec(S2COpenBackstoryScreenPacket.class);

    public static final CustomPacketPayload.Type<S2COpenBackstoryScreenPacket> TYPE = new CustomPacketPayload.Type<>(
            PacketHandler.packet(S2COpenBackstoryScreenPacket.class));

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
