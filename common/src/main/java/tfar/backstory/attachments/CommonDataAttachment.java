package tfar.backstory.attachments;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import tfar.backstory.Backstory;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class CommonDataAttachment<T> {

    protected final Function<Object, T> defaultValueSupplier;
    protected final ResourceLocation name;
    protected final boolean copyOnDeath;
    protected final Codec<T> codec;
    protected Object attachment;
    @Nullable protected final SyncHandler<T> syncHandler;

    public CommonDataAttachment(Function<Object, T> defaultValueSupplier, ResourceLocation name, boolean copyOnDeath, Codec<T> codec,
                                @Nullable SyncHandler<T> syncHandler) {
        this.defaultValueSupplier = defaultValueSupplier;
        this.name = name;
        this.copyOnDeath = copyOnDeath;
        this.codec = codec;
        this.syncHandler = syncHandler;
    }

    public static <T> Builder<T> create(Function<Object, T> defaultValueSupplier) {
        return new Builder<>(defaultValueSupplier);
    }

    public static <T> Builder<T> create() {
        return new Builder<>(o -> null);
    }

    public Function<Object, T> getDefaultValueSupplier() {
        return defaultValueSupplier;
    }

    public ResourceLocation getName() {
        return name;
    }

    public Codec<T> getCodec() {
        return codec;
    }

    public SyncHandler<T> getSyncHandler() {
        return syncHandler;
    }

    public boolean isCopyOnDeath() {
        return copyOnDeath;
    }

    public boolean canSync() {
        return syncHandler != null;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        Objects.requireNonNull(attachment);
        this.attachment = attachment;
    }


    public static class Builder<T> {
        protected final Function<Object, T> defaultValueSupplier;
        protected boolean copyOnDeath;
        protected Codec<T> codec;
        @Nullable
        protected SyncHandler<T> syncHandler;

        public Builder(Function<Object, T> defaultValueSupplier) {
            this.defaultValueSupplier = defaultValueSupplier;
        }

        public Builder<T> codec(Codec<T> codec) {
            Objects.requireNonNull(codec);
            this.codec = codec;
            return this;
        }

        public Builder<T> sync(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            return sync((object, serverPlayer) -> true, streamCodec);
        }

        public Builder<T> sync(BiPredicate<Object, ServerPlayer> sendToPlayer, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            Objects.requireNonNull(sendToPlayer);
            Objects.requireNonNull(streamCodec);
            return sync(new SyncHandler<T>() {
                @Override
                public boolean sendToPlayer(Object holder, ServerPlayer to) {
                    return sendToPlayer.test(holder, to);
                }

                @Override
                public void write(RegistryFriendlyByteBuf buf, T attachment, boolean initialSync) {
                    streamCodec.encode(buf, attachment);
                }

                @Override
                public T read(Object holder, RegistryFriendlyByteBuf buf, @Nullable T previousValue) {
                    return streamCodec.decode(buf);
                }
            });
        }

        public Builder<T> sync(SyncHandler<T> syncHandler) {
            Objects.requireNonNull(syncHandler);
            this.syncHandler = syncHandler;
            return this;
        }

        public Builder<T> copyOnDeath() {
            copyOnDeath = true;
            return this;
        }


        public CommonDataAttachment<T> build(String name) {
            return build(Backstory.id(name));
        }

        public CommonDataAttachment<T> build(ResourceLocation name) {
            Objects.requireNonNull(name);
            return new CommonDataAttachment<>(defaultValueSupplier, name, copyOnDeath, codec, syncHandler);
        }
    }
}
