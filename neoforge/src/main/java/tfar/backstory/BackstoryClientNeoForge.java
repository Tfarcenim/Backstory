package tfar.backstory;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;

@Mod(value = Backstory.MOD_ID,dist = Dist.CLIENT)
public class BackstoryClientNeoForge {
    public BackstoryClientNeoForge() {
        if (!FMLEnvironment.production) {
            NeoForge.EVENT_BUS.addListener(this::alwaysRenderNames);
        }
    }

    void alwaysRenderNames(RenderNameTagEvent event) {
        event.setCanRender(TriState.TRUE);
    }
}
