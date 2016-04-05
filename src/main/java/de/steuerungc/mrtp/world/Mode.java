package de.steuerungc.mrtp.world;

import org.bukkit.Location;

import java.util.List;

/**
 * Created by Martin on 03.04.2016.
 */
public abstract class Mode {

    public List<String> biomes;
    public List<String> blocks;
    public String world;
    public int top;
    public int minimal;

    public Mode(List<String> bimoes, List<String> blocks, String world, int top, int minimal) {
        this.biomes = bimoes;
        this.blocks = blocks;
        this.world = world;
        this.top = top;
        this.minimal = minimal;
        blocks.add("STATIONARY_LAVA");
        blocks.add("LAVA");
    }

    public abstract Location generateLocation ();

}
