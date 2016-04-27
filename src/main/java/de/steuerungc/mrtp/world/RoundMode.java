package de.steuerungc.mrtp.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;

/**
 * Created by Martin on 03.04.2016.
 */
public class RoundMode extends Mode {

    int radius, ovcx, ovcz;

    public RoundMode (List<String> biomes, List<String> blocks, String world, int radius, int top, int minimal) {
        super(biomes, blocks, world, top, minimal);
        this.radius = radius;
        ovcx = Bukkit.getServer().getWorld(world).getSpawnLocation().getBlockX();
        ovcz = Bukkit.getServer().getWorld(world).getSpawnLocation().getBlockZ();
    }

    public RoundMode (List<String> biomes, List<String> blocks, String world, int radius, int ovcx, int ovcz, int top, int minimal) {
        super(biomes, blocks, world, top, minimal);
        this.radius = radius;
        this.ovcx = ovcx;
        this.ovcz = ovcz;
    }

    @Override
    public Location generateLocation() {
        World w = Bukkit.getWorld(world);
        double ex;
        double ez;
        double er;
        do {
            ex = -1.0D + Math.random() * 2.0D;
            ez = -1.0D + Math.random() * 2.0D;
            er = ex * ex + ez * ez;
        } while (er > 1.0D);
        int x = (int)(ex * this.radius);
        int z = (int)(ez * this.radius);

        x += ovcx;
        z += ovcz;

        if(minimal > -1) {
            Location target = new Location(w, x, 0.0D, z);
            Location spawn = new Location(w, ovcx, 0.0D, ovcz);
            if(target.distance(spawn) < minimal) {
                return null;
            }
        }
        if(top == -1) {
            Block b = w.getHighestBlockAt(x, z).getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
            if ((!biomes.contains(b.getBiome().name())) && (!blocks.contains(b.getType().name()))) {
                return b.getLocation().add(0.5D, 2.0D, 0.5D);
            }
            b.getChunk().unload(true);
        } else {
            int choosen = top;
            if (w.getHighestBlockAt(x, z).getY() > top) {
                choosen = w.getHighestBlockYAt(x, z);
            }
            Block b = w.getBlockAt(x, choosen, z).getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
            if ((!biomes.contains(b.getBiome().name())) && (!blocks.contains(b.getType().name()))) {
                return b.getLocation().add(0.5D, 2.0D, 0.5D);
            }
            b.getChunk().unload(true);
        }

        return null;
    }
}
