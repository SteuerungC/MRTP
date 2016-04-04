package de.steuerungc.mrtp.world;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;

/**
 * Created by Martin on 04.04.2016.
 */
public class WBMode extends Mode {

    public WBMode(List<String> biomes, List<String> blocks, String world, int top, int minimal) {
        super(biomes, blocks, world, top, minimal);
    }

    @Override
    public Location generateLocation() {
        World w = Bukkit.getServer().getWorld(world);
        BorderData bd = Config.Border(world);

        if(bd != null) {
            int l;
            if (bd.getRadiusX() > bd.getRadiusZ()) {
                l = bd.getRadiusX();
            } else {
                l = bd.getRadiusZ();
            }

            int x, z;
            Location loc;
            do {
                double ex = -1.0D + Math.random() * 2.0D;
                double ez = -1.0D + Math.random() * 2.0D;

                x = (int) (ex * (l / 2));
                z = (int) (ez * (l / 2));

                x += bd.getX();
                z += bd.getZ();

                loc = new Location(w, x, 0.0D, z);

            } while (!bd.insideBorder(loc));

            if (minimal > -1) {
                Location target = new Location(w, x, 0.0D, z);
                Location spawn = new Location(w, bd.getX(), 0.0D, bd.getZ());
                if (target.distance(spawn) < minimal) {
                    return null;
                }
            }
            if (top == -1) {
                Block b = w.getHighestBlockAt(x, z).getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
                if ((!biomes.contains(b.getBiome().name())) && (!blocks.contains(b.getType().name()))) {
                    return b.getLocation().add(0.5D, 1.0D, 0.5D);
                }
                b.getChunk().unload(true);
            } else {
                int choosen = top;
                if (w.getHighestBlockAt(x, z).getY() > top) {
                    choosen = w.getHighestBlockYAt(x, z);
                }
                Block b = w.getBlockAt(x, choosen, z).getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
                if ((!biomes.contains(b.getBiome().name())) && (!blocks.contains(b.getType().name()))) {
                    return b.getLocation().add(0.5D, 1.0D, 0.5D);
                }
                b.getChunk().unload(true);
            }
            return null;
        } else {
            System.out.println("[MRTP] The Border in world " + world + " was removed. Defaulting to 0,0,0!");
            return new Location(w, 0.0D, w.getHighestBlockYAt(0,0), 0.0D);
        }
    }
}
