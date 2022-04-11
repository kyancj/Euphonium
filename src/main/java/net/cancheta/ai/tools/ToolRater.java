package net.cancheta.ai.tools;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import com.google.gson.Gson;

import net.cancheta.settings.EuphoniumSettings;
import net.minecraft.item.ItemStack;

public class ToolRater {
	
	public ToolRater() {
	}
	
	private final ArrayList<Rater> raters = new ArrayList<Rater>();
	
	public float rateTool(ItemStack stack, int forBlockAndMeta) {
		float f = 1;
		for (Rater rater : raters) {
			f *= rater.rate(stack, forBlockAndMeta);
		}
		return f;
	}
	
	public static ToolRater createDefaultRater() {
		InputStream res = ToolRater.class.getResourceAsStream("tools.json");
		return createToolRaterByJson(new InputStreamReader(res));
	}
	
	private static ToolRater createToolRaterByJson(Reader reader) {
		Gson gson = EuphoniumSettings.getGson();
		return gson.fromJson(reader, ToolRater.class);
	}
}
