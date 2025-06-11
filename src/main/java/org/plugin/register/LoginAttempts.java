package org.plugin.register;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

import static sun.jvm.hotspot.ci.ciObjectFactory.getMetadata;

public class LoginAttempts {
    private static final String ATTEMPTS_META = "login attempts";

    public static void resetAttemptsMeta(Player player) {
        player.setMetadata(ATTEMPTS_META, new FixedMetadataValue(Register.getInstance(), 3));
    }

    public static int getAttempts(Player player) {
        List<MetadataValue> values = player.getMetadata(ATTEMPTS_META);
        if (values.isEmpty())
            return 0;

        return values.get(0).asInt();

    }
    public static void decreaseAttempts(Player player) {
        player.setMetadata(ATTEMPTS_META, new FixedMetadataValue(Register.getInstance(),
                getAttempts(player) - 1));
    }
}
