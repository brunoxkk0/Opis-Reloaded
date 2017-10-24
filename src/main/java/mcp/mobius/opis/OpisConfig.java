package mcp.mobius.opis;

import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.util.UUID;

public class OpisConfig {

    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_ACCESSRIGHTS = "access_rights";

    public static String commentPrivileged = "List of players with PRIVILEGED access level";

    public static int profilerMaxTicks = 250;
    public static boolean enableDebugBlocks = false;

    public static void readConfig() {
        Configuration cfg = OpisMod.config;
        try {
            cfg.load();
            initGeneralConfig(cfg);
            initAccessRightsConfig(cfg);
        } catch (Exception e1) {
            OpisMod.LOGGER.log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void initGeneralConfig(Configuration cfg) {
        profilerMaxTicks = cfg.getInt( "profilerMaxTicks", CATEGORY_GENERAL, profilerMaxTicks, 0, 9000, "Maximum amount of ticks Opis is allowed to run");
        enableDebugBlocks = cfg.getBoolean("enableDebugBlocks", CATEGORY_GENERAL, enableDebugBlocks, "Enable debug block registration");
    }

    private static void initAccessRightsConfig(Configuration cfg) {
        String[] users = cfg.getStringList("privilegedUsers", CATEGORY_ACCESSRIGHTS, new String[]{}, commentPrivileged);

        AccessLevel minTables = AccessLevel.PRIVILEGED;
        AccessLevel openOpis = AccessLevel.PRIVILEGED;

        try {
            openOpis = AccessLevel.valueOf(cfg.getString("openOpis", CATEGORY_ACCESSRIGHTS, "NONE", "Minimum access level to be able to open the Opis interface. Valid values: NONE, PRIVILEGED, ADMIN"));
        } catch (IllegalArgumentException e) {
        }

        try {
            minTables = AccessLevel.valueOf(cfg.getString("viewTables", CATEGORY_ACCESSRIGHTS, "NONE", "Minimum access level to be able to view tables. Valid values: NONE, PRIVILEGED, ADMIN"));
        } catch (IllegalArgumentException e) {
        }

        Message.setTablesMinimumLevel(minTables);
        Message.setOpisMinimumLevel(openOpis);

        for (String user : users) {
            PlayerTracker.INSTANCE.addPrivilegedPlayer(UUID.fromString(user), false);
        }
    }
}