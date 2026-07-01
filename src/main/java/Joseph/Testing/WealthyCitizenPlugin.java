package Joseph.Testing;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
		name = "Wealthy Citizen Thieving",
		description = "Highlights distracted Wealthy Citizens in Varlamore."
)
public class WealthyCitizenPlugin extends Plugin {
	public static final int LAVINIA_ID = 13312;
	public static final int VICTOR_ID = 13313;
	public static final int CAIUS_ID = 13314;
	public static final String WEALTHY_CITIZEN_NAME = "Wealthy citizen";
	private boolean distraction_alerted = false;
	public NPC distracted_citizen = null;

	@Inject
	private Client client;

	@Inject
	private WealthyCitizenConfig config;

	@Inject
	private OverlayManager overlay_manager;


	@Inject
	private WealthyCitizenOverlay wealthy_citizen_overlay;

	@Override
	protected void startUp() throws Exception {
		log.debug("Example started!");
		overlay_manager.add(wealthy_citizen_overlay);
	}

	@Override
	protected void shutDown() throws Exception {
		log.debug("Example stopped!");
		overlay_manager.remove(wealthy_citizen_overlay);
	}

	public boolean playerInActivity() {
		Player p = client.getLocalPlayer();
		WorldPoint wl = p.getWorldLocation();
		// Civitas chunk location
		if (wl.getRegionID() == 6704) {
			return true;
		}
		return false;
	}

	@Provides
	WealthyCitizenConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(WealthyCitizenConfig.class);
	}

	public List<NPC> getCachedNPCs() {
		WorldView wv = client.getTopLevelWorldView();
		return wv == null ? new ArrayList<NPC>()
				: wv.npcs()
				.stream()
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (client.getGameState() != GameState.LOGGED_IN || !playerInActivity()) {
			return;
		}
		boolean found_distracted = false;
		NPC current_distracted = null;

		for (NPC npc : getCachedNPCs()) {
			//sometimes names briefly loaded in as null - this should stop RL crashing
			if (npc == null || npc.getName() == null) {
				continue;
			}
			if (npc.getLocalLocation().distanceTo(client.getLocalPlayer().getLocalLocation()) > 2500) {
				// Only calculate the expensive math if npcs are nearby
				continue;
			}
			if (npc.getName().equals(WEALTHY_CITIZEN_NAME)) {
				if (npc.isInteracting()) {
					Actor actor = npc.getInteracting();

					if (actor != null && actor.getCombatLevel() == 0) {
						found_distracted = true;
						current_distracted = npc;
						break;
					}
				}
			}

		}
		if (found_distracted && !distraction_alerted) {
			distraction_alerted = true;
			distracted_citizen = current_distracted;

			if (config.drawTile() || config.drawHitbox()) {
				// made message in chat coloured, as black text looked out of place
				String alert_message = "A Wealthy Citizen is distracted! Run his pockets!";
				String colored_message = ColorUtil.wrapWithColorTag(alert_message, config.highlightColor());
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", colored_message, null);
			}
		} else if (!found_distracted) {
			distraction_alerted = false;
			distracted_citizen = null;
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getKey().equals("highlightColor")) {
			//this forces the plugin to refresh the overlay immediately if it is changed
		}
	}
}