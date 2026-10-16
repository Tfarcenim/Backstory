package tfar.backstory.attachments;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import tfar.backstory.PlayerBackstory;
import tfar.backstory.platform.Services;

import java.util.*;

public class CommonDataAttachments {

    private static final Map<ResourceLocation,CommonDataAttachment<?>> MAP =new HashMap<>();

    public static final CommonDataAttachment<Unit> CREATED_BACKSTORY = register(CommonDataAttachment
            .<Unit>create(o -> null)
            .codec(Unit.CODEC)
            .sync(StreamCodec.unit(Unit.INSTANCE))
            .build("created_backstory")
    );

    public static final CommonDataAttachment<Integer> BOOK_COLOR = register(CommonDataAttachment
            .create(o -> (int) Math.floor(Math.random()*3))
            .codec(Codec.intRange(0,3))
            .sync(ByteBufCodecs.INT)
            .build("book_color")
    );

    public static final CommonDataAttachment<Unit> CAN_CHANGE_BACKSTORY = register(CommonDataAttachment
            .<Unit>create(o -> null)
            .codec(Unit.CODEC)
            .sync(StreamCodec.unit(Unit.INSTANCE))
            .build("can_change_backstory")
    );

    public static final CommonDataAttachment<List<UUID>> CAN_VIEW = register(
            CommonDataAttachment.<List<UUID>>create(o -> List.of())
                    .codec(UUIDUtil.CODEC.listOf())
                    .build("can_view"));

    public static final CommonDataAttachment<PlayerBackstory> BACKSTORY = register(
            CommonDataAttachment.create(o -> PlayerBackstory.NONE)
                    .sync(new SyncHandler<>() {
                        @Override
                        public void write(RegistryFriendlyByteBuf buf, PlayerBackstory attachment, boolean initialSync) {
                            PlayerBackstory.STREAM_CODEC.encode(buf, attachment);
                        }

                        @Override
                        public @Nullable PlayerBackstory read(Object holder, RegistryFriendlyByteBuf buf, @Nullable PlayerBackstory previousValue) {
                            PlayerBackstory backstory = PlayerBackstory.STREAM_CODEC.decode(buf);
                            if (holder instanceof Player player) {
                                Minecraft.getInstance().execute(() -> Services.PLATFORM.refreshName(player));
                            }
                            return backstory;
                        }
                    })
                    .codec(PlayerBackstory.CODEC)
                    .build("backstory")
    );

    public static CommonDataAttachment<?> lookup(ResourceLocation location) {
        return MAP.get(location);
    }

    static <T> CommonDataAttachment<T> register(CommonDataAttachment<T> type) {
        Services.PLATFORM.registerDataAttachment(type);
        Objects.requireNonNull(type.getAttachment());
        MAP.put(type.name,type);
        return type;
    }

    public static void init() {

    }
}