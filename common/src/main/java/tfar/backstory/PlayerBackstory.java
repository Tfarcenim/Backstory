package tfar.backstory;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.WritableBookContent;

import java.util.List;

public record PlayerBackstory(String chosenName,String gender, List<Filterable<String>> pages) {

    public static final int CHAR_LIMIT = 1024;
    public static final int PAGE_LIMIT = 100;

    public static final PlayerBackstory NONE = new PlayerBackstory("","",List.of());

    private static final Codec<Filterable<String>> PAGE_CODEC = Filterable.codec(Codec.string(0, CHAR_LIMIT));
    public static final Codec<List<Filterable<String>>> PAGES_CODEC = PAGE_CODEC.sizeLimitedListOf(PAGE_LIMIT);

    public static final Codec<PlayerBackstory> CODEC = RecordCodecBuilder.create(playerBackstoryInstance ->
            playerBackstoryInstance.group(
                    Codec.STRING.fieldOf("chosen_name")
                    .forGetter(PlayerBackstory::chosenName),
                            Codec.STRING.fieldOf("gender")
                                    .forGetter(PlayerBackstory::gender),
                            PAGES_CODEC.fieldOf("pages").forGetter(PlayerBackstory::pages))
                    .apply(playerBackstoryInstance, PlayerBackstory::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,PlayerBackstory> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,PlayerBackstory::chosenName,
            ByteBufCodecs.STRING_UTF8,PlayerBackstory::gender,
            Filterable.streamCodec(ByteBufCodecs.stringUtf8(CHAR_LIMIT)).apply(ByteBufCodecs.list(PAGE_LIMIT)),
            PlayerBackstory::pages,PlayerBackstory::new);

    public boolean isEmpty() {
        return this == NONE;
    }

    public boolean isValid() {
        return !chosenName.isBlank() && pages.size() <= PAGE_LIMIT;
    }

    public List<String> getPages(boolean filtered) {
        return Lists.transform(this.pages, filterable -> filterable.get(filtered));
    }

    public boolean useNickname(Player player) {
        return !chosenName.isBlank() && !player.getName().getString().equals(chosenName);
    }
}
