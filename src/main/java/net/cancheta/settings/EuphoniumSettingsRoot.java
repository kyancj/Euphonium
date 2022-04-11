package net.cancheta.settings;

import net.cancheta.ai.tools.ToolRater;

@EuphoniumSettingObject
public class EuphoniumSettingsRoot {
	private PathfindingSettings pathfinding = new PathfindingSettings();
	
	private SaferuleSettings saferules = new SaferuleSettings();

	public PathfindingSettings getPathfinding() {
		return pathfinding;
	}

	public SaferuleSettings getSaferules() {
		return saferules;
	}
	
	public ToolRater toolRater = ToolRater.createDefaultRater();

	public ToolRater getToolRater() {
		return toolRater;
	}
}
