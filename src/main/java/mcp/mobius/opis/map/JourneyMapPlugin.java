package mcp.mobius.opis.map;

import java.util.EnumSet;
import javax.annotation.ParametersAreNonnullByDefault;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import mcp.mobius.opis.OpisMod;

import static journeymap.client.api.event.ClientEvent.Type.*;

@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class JourneyMapPlugin implements IClientPlugin {

    private static IClientAPI jmAPI = null;

    @Override
    public void initialize(final IClientAPI jmAPI) {
        JourneyMapPlugin.jmAPI = jmAPI;

        // Subscribe to desired ClientEvent types from JourneyMap
        JourneyMapPlugin.jmAPI.subscribe(getModId(), EnumSet.of(MAPPING_STARTED, MAPPING_STOPPED));

        OpisMod.mappingEnabled = true;
        OpisMod.LOGGER.info("Initialized " + getClass().getName());
    }

    @Override
    public String getModId() {
        return OpisMod.MODID;
    }
    
    public static IClientAPI getAPI() {
        return jmAPI;
    }

    @Override
    public void onEvent(ClientEvent event) {
        try {
            switch (event.type) {
                case MAPPING_STARTED:
                    onMappingStarted(event);
                    break;

                case MAPPING_STOPPED:
                    onMappingStopped(event);
                    break;
            }
        } catch (Throwable t) {
            OpisMod.LOGGER.error(t.getMessage(), t);
        }
    }

    private void onMappingStarted(ClientEvent event) {

    }

    private void onMappingStopped(ClientEvent event) {
        // Clear everything
        jmAPI.removeAll(OpisMod.MODID);
    }

}
