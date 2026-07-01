package Joseph.Testing;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface WealthyCitizenConfig extends Config {
    @ConfigItem(
            position = 1,
            keyName = "greeting",
            name = "Varlamore Thieving Description",
            description = "The message to show to the user when they login"
    )
    default String greeting() {
        return "This plugin is aimed to help thieving wealthy citizens in Varlamore.";
    }

    @ConfigItem(
            position = 2,
            keyName = "drawTile",
            name = "Draw Floor Tile",
            description = "Highlights the floor tile beneath the citizen."
    )
    default boolean drawTile() {
        return true;
    }
    @ConfigItem(
            position = 3,
            keyName = "drawHitbox",
            name = "Draw Hitbox",
            description = "Highlights the citizen's hitbox."
    )
    default boolean drawHitbox() {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 4,
            keyName = "highlightColor",
            name = "Highlight Color",
            description = "A color picker for the citizens' highlights"
    )
    default Color highlightColor() {
        return Color.GREEN; // Default Color
    }
}
