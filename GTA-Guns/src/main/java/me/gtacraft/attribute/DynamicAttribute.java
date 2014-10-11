package me.gtacraft.attribute;

/**
 * Created by Connor on 4/28/14. Designed for the GTA-Guns project.
 */

public class DynamicAttribute extends Attribute {

    public DynamicAttribute(Object value) {
        super(value);
    }

    public void setValue(Object newValue) {
        value = newValue;
    }
}
