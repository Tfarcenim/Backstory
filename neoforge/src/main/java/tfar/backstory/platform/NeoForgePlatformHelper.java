package tfar.backstory.platform;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;
import tfar.backstory.attachments.CommonDataAttachment;
import tfar.backstory.attachments.SyncHandler;
import tfar.backstory.network.client.S2CModPacket;
import tfar.backstory.network.server.C2SModPacket;
import tfar.backstory.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.util.function.Function;

public class NeoForgePlatformHelper implements IPlatformHelper {

    public static PayloadRegistrar registrar;

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> void registerDataAttachment(CommonDataAttachment<T> attachment) {
        AttachmentType.Builder<T> builder = AttachmentType.builder((Function<IAttachmentHolder, T>) (Object) attachment.getDefaultValueSupplier());
        if (attachment.getCodec() != null) {
            builder.serialize(attachment.getCodec());
        }
        if (attachment.isCopyOnDeath()) {
            builder.copyOnDeath();
        }
        if (attachment.canSync()) {
            SyncHandler<T> syncHandler = attachment.getSyncHandler();
            builder.sync(new AttachmentSyncHandler<>() {
                @Override
                public boolean sendToPlayer(IAttachmentHolder holder, ServerPlayer to) {
                    return syncHandler.sendToPlayer(holder, to);
                }

                @Override
                public void write(RegistryFriendlyByteBuf buf, T attachment, boolean initialSync) {
                    syncHandler.write(buf, attachment, initialSync);
                }

                @Override
                public @Nullable T read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable T previousValue) {
                    return syncHandler.read(holder, buf, previousValue);
                }
            });
        }
        AttachmentType<T> type = builder.build();
        Registry.register(NeoForgeRegistries.ATTACHMENT_TYPES, attachment.getName(), type);
        attachment.setAttachment(type);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    @Nullable
    public <T> T getAttachedValue(Object object, CommonDataAttachment<T> attachment) {
        AttachmentType<T> type = (AttachmentType<T>) attachment.getAttachment();
        if (object instanceof IAttachmentHolder attachmentHolder) {
            return attachmentHolder.getExistingDataOrNull(type);
        } else {
            throw new IllegalStateException("Cannot attach data to " + object);
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> void setAttachedValue(Object object, CommonDataAttachment<T> attachment, @Nullable T value) {
        AttachmentType<T> type = (AttachmentType<T>) attachment.getAttachment();
        if (object instanceof IAttachmentHolder attachmentHolder) {
            if (value == null) {
                attachmentHolder.removeData(type);
            } else {
                attachmentHolder.setData(type, value);
            }
        } else {
            throw new IllegalStateException("Cannot attach data to " + object);
        }
    }

    @Override
    public <MSG extends S2CModPacket> void registerClientPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf,MSG> streamCodec) {
        registrar.playToClient(type, streamCodec, (p, t) -> p.handleClient());
    }

    @Override
    public <MSG extends C2SModPacket> void registerServerPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf, MSG> streamCodec) {
        registrar.playToServer(type, streamCodec, (p, t) -> p.handleServer((ServerPlayer) t.player()));
    }


    @Override
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    @Override
    public void sendToServer(C2SModPacket msg) {
        PacketDistributor.sendToServer(msg);
    }

    @Override
    public void setShadow(EditBox editBox, boolean shadow) {
        editBox.setTextShadow(shadow);
    }

    @Override
    public void refreshName(Player player) {
        player.refreshDisplayName();
    }
}