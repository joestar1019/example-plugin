package Joseph.Testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import java.awt.Shape;

public class WealthyCitizenOverlay extends Overlay {
    private final Client client;
    private final WealthyCitizenPlugin plugin;
    private final WealthyCitizenConfig config;

    @Inject
    private WealthyCitizenOverlay(Client client, WealthyCitizenPlugin plugin, WealthyCitizenConfig config) {

        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (!config.drawTile() && !config.drawHitbox() || plugin.distracted_citizen == null) {

            return null;

        }

        Polygon tile_poly = plugin.distracted_citizen.getCanvasTilePoly();
        Shape hitbox = plugin.distracted_citizen.getConvexHull();
        Color color = config.highlightColor();

        //stops rl crashing if citizen is on screen but behind something
        if (config.drawTile() && tile_poly != null) {
            OverlayUtil.renderPolygon(graphics, tile_poly, color);
        }

        if (config.drawHitbox() && hitbox != null) {
            OverlayUtil.renderPolygon(graphics, hitbox, color);
        }

        return null;
    }
}
