package de.steuerungc.mrtp.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;

/**
 * Created by Martin on 03.04.2016.
 */
public class RectMode extends Mode {

    private int distx, distz, ovcx, ovcz;

    public RectMode (List<String> biomes, List<String> blocks, String world, int distx, int distz, int top, int minimal) {
        super(biomes, blocks, world, top, minimal);
        this.distx = distx;
        this.distz = distz;
        ovcx = Bukkit.getServer().getWorld(world).getSpawnLocation().getBlockX();
        ovcz = Bukkit.getServer().getWorld(world).getSpawnLocation().getBlockZ();
    }

    public RectMode (List<String> biomes, List<String> blocks, String world, int distx, int distz, int ovcx, int ovcz, int top, int minimal) {
        super(biomes, blocks, world, top, minimal);
        this.distx = distx;
        this.distz = distz;
        this.ovcx =  ovcx;
        this.ovcz = ovcz;
    }

    @Override
    public Location generateLocation() {
        World w = Bukkit.getServer().getWorld(world);
        double l = w.getWorldBorder().getSize();
        double ex = -1.0D + Math.random() * 2.0D;
        double ez = -1.0D + Math.random() * 2.0D;

        int x = (int)(ex * (distx/2));
        int z = (int)(ez * (distz/2));

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
