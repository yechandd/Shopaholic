package uk.joshiejack.shopaholic.world.shop.input;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum InputMethod implements StringRepresentable {
    RIGHT_CLICK, SHIFT_RIGHT_CLICK, SCRIPT, COMMAND;

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
