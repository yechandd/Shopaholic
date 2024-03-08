package uk.joshiejack.shopaholic.client.gui.widget;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MultiTooltip {
    private static final int MAX_WIDTH = 170;
    //private final Component message;
    private final Component[] messages;
    @Nullable
    private List<FormattedCharSequence> cachedTooltip;
    private int msDelay;
    private long hoverOrFocusedStartTime;
    private boolean wasHoveredOrFocused;

    private MultiTooltip(Component... components) {
        this.messages = components;
    }

    public void setDelay(int p_306340_) {
        this.msDelay = p_306340_;
    }

    public static MultiTooltip create(Component... components) {
        return new MultiTooltip(components);
    }

    public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {
        if (this.cachedTooltip == null) {
            this.cachedTooltip = new ArrayList<>();
            for (Component message : messages) {
                this.cachedTooltip.addAll(splitTooltip(minecraft, message));
            }
        }

        return this.cachedTooltip;
    }

    public static List<FormattedCharSequence> splitTooltip(Minecraft minecraft, Component component) {
        return minecraft.font.split(component, 170);
    }

    public void refreshTooltipForNextRenderPass(Minecraft minecraft, boolean isHovered, boolean isFocused, ScreenRectangle rectangle) {
        boolean flag = isHovered || isFocused && Minecraft.getInstance().getLastInputType().isKeyboard();
        if (flag != this.wasHoveredOrFocused) {
            if (flag) {
                this.hoverOrFocusedStartTime = Util.getMillis();
            }

            this.wasHoveredOrFocused = flag;
        }

        if (flag && Util.getMillis() - this.hoverOrFocusedStartTime > (long)this.msDelay) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen != null) {
                screen.setTooltipForNextRenderPass(toCharSequence(minecraft), this.createTooltipPositioner(isHovered, isFocused, rectangle), isFocused);
            }
        }
    }

    protected ClientTooltipPositioner createTooltipPositioner(boolean isHovered, boolean isFocused, ScreenRectangle rectangle) {
        return !isHovered && isFocused && Minecraft.getInstance().getLastInputType().isKeyboard()
                ? new BelowOrAboveWidgetTooltipPositioner(rectangle)
                : new MenuTooltipPositioner(rectangle);
    }
}
