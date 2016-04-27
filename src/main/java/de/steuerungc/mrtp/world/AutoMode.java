package de.steuerungc.mrtp.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;

/**
 * Created by Martin on 03.04.2016.
 */
public class AutoMode extends Mode {

    public AutoMode (List<String> biomes, List<String> blocks, String world, int top, int minimal) {
        super(biomes, blocks, world, top, minimal);
    }

    @Override
    public Location generateLocation() {
        World w = Bukkit.getServer().getWorld(super.world);
        double l = w.getWorldBorder().getSize();
        double ex = -1.0D + Math.random() * 2.0D;
        double ez = -1.0D + Math.random() * 2.0D;

        int x = (int)(ex * (l/2));
        int z = (int)(ez * (l/2));

        x += w.getWorldBorder().getCenter().getBlockX();
        z += w.getWorldBorder().getCenter().getBlockZ();

        if(minimal > -1) {
            Location target = new Location(w, x, 0.0D, z);
            Location spawn = new Location(w, w.getWorldBorder().getCenter().getBlockX(), 0.0D, w.getWorldBorder().getCenter().getBlockZ());
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
