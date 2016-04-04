package de.steuerungc.mrtp;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Martin on 03.04.2016.
 */
public class Cooldown {
    private static HashMap<UUID, Long> times;
    private static double cooldown;

    protected Cooldown(int ticks)
    {
        times = new HashMap();
        cooldown = ticks * 1000.0D;
    }

    protected static void start(UUID player)
    {
        times.put(player, System.currentTimeMillis());
    }

    protected static Long test(UUID player)
    {
        if (times.containsKey(player)) {
            long start = times.get(player);
            long current = System.currentTimeMillis();
            if (current - start < cooldown) {
                return (long)(cooldown - (current - start));
            }
            times.remove(player);
        }
        return 0L;
    }
}
