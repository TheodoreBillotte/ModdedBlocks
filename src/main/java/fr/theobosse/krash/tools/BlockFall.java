package fr.theobosse.krash.tools;

import org.bukkit.Location;

import javax.swing.*;
import java.util.Map;

public class BlockFall {

    private final Map<String, Object> values;
    private final Location location;

    public BlockFall(Map<String, Object> values, Location location) {
        this.values = values;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public Map<String, Object> getValues() {
        return values;
    }
}
