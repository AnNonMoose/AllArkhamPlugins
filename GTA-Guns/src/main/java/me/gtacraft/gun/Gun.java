package me.gtacraft.gun;

import me.gtacraft.attribute.Attribute;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Connor on 4/27/14. Designed for the GTA-Guns project.
 */

public class Gun {

    private LinkedHashMap<String, Attribute> attributes = new LinkedHashMap<String, Attribute>();

    public Attribute getAttribute(String label) {
        return attributes.get(label);
    }

    public void addAttribute(String label, Object value) {
        attributes.put(label, new Attribute(value));
    }

    public Map<String, Attribute> attributes() {
        return attributes;
    }
}