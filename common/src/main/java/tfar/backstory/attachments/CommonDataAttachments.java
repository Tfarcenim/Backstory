package tfar.backstory.attachments;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import tfar.backstory.Backstory;
import tfar.backstory.PlayerBackstory;
import tfar.backstory.platform.Services;

import javax.swing.text.html.Option;
import java.util.*;

public class CommonDataAttachments {

    private static final Map<ResourceLocation,CommonDataAttachment<?>> MAP =new HashMap<>();

    public static final CommonDataAttachment<Integer> SANITY =
            register(CommonDataAttachment.create(o -> 0)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .codec(Codec.INT)
                    .build("sanity"));

    public static final CommonDataAttachment<PlayerBackstory> BACKSTORY = register(
            CommonDataAttachment.<PlayerBackstory>create(o -> PlayerBackstory.NONE)
                    .networkSynchronized(PlayerBackstory.STREAM_CODEC)
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