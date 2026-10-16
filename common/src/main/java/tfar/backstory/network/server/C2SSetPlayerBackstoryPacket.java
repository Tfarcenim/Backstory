package tfar.backstory.network.server;

import net.minecraft.commands.Commands;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.backstory.PlayerBackstory;
import tfar.backstory.attachments.DataAttachmentUtil;
import tfar.backstory.network.PacketHandler;

import java.util.UUID;

public record C2SSetPlayerBackstoryPacket(PlayerBackstory backstory, UUID playerUUID) implements C2SModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSetPlayerBackstoryPacket> STREAM_CODEC =
            StreamCodec.composite(PlayerBackstory.STREAM_CODEC, C2SSetPlayerBackstoryPacket::backstory,
                    UUIDUtil.STREAM_CODEC, C2SSetPlayerBackstoryPacket::playerUUID,
                    C2SSetPlayerBackstoryPacket::new);

    public static final CustomPacketPayload.Type<C2SSetPlayerBackstoryPacket> TYPE = new CustomPacketPayload.Type<>(
            PacketHandler.packet(C2SSetPlayerBackstoryPacket.class));

    public void handleServer(ServerPlayer player) {
        boolean isEditingSelf = player.getUUID().equals(playerUUID);
        if (backstory.isValid() && isAllowedToEdit(player,isEditingSelf)) {
            DataAttachmentUtil.setBackstory(player, backstory);
            if (isEditingSelf) {
                DataAttachmentUtil.markCreatedBackstory(player);
                DataAttachmentUtil.setCanEditBackStory(player,false);
            }
        }
    }

    boolean isAllowedToEdit(ServerPlayer player, boolean isEditingSelf) {
        boolean sufficientPermission = player.hasPermissions(Commands.LEVEL_MODERATORS);
        if (isEditingSelf) {//editing self
            return sufficientPermission || !DataAttachmentUtil.hasCreatedBackstory(player) ||
                    DataAttachmentUtil.canChangeBackstory(player);
        }
        return sufficientPermission;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

