package tfar.backstory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;

import java.util.Optional;

public record PlayerBackstory(String chosenName, Filterable<String> page) {

    public static final int CHAR_LIMIT = 1024;

    public static final PlayerBackstory NONE = new PlayerBackstory("",new Filterable<>("",Optional.empty()));

    public static final Codec<PlayerBackstory> CODEC = RecordCodecBuilder.create(playerBackstoryInstance ->
            playerBackstoryInstance.group(
                    Codec.STRING.fieldOf("chosen_name")
                    .forGetter(PlayerBackstory::chosenName),Filterable.codec(Codec.string(0,CHAR_LIMIT))
                                    .fieldOf("page").forGetter(PlayerBackstory::page))
                    .apply(playerBackstoryInstance, PlayerBackstory::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,PlayerBackstory> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,PlayerBackstory::chosenName,
            Filterable.streamCodec(ByteBufCodecs.stringUtf8(CHAR_LIMIT)),
            PlayerBackstory::page
    ,PlayerBackstory::new);

    public boolean isEmpty() {
        return this == NONE;
    }
}
