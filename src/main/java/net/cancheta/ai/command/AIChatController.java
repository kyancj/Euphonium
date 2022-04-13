package net.cancheta.ai.command;

import com.google.common.base.Function;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

import java.util.List;

public class AIChatController {
	private static final CommandRegistry registry = new CommandRegistry();
	
	private static final int PER_PAGE = 8;
	
	private AIChatController() {
		
	}
	
	public static void addChatLine(String message) { addToChat("[Euphonium] " + message); }
	
	public static CommandRegistry getRegistry() {
		return registry;
	}
	
	public static void addToChat(String string) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.player.sendMessage(new LiteralText(string), false);
		return;
	}
	
	public static <T> void addToChatPaged(String title, int page, List<T> data, Function<T, String> convert) {
		int totalPages = (int) Math.ceil(1.0f + data.size() / PER_PAGE);
		AIChatController.addChatLine(title + " " + page + " / " + totalPages);
		
		int start = Math.max(0,  page - 1) * PER_PAGE;
		int end = Math.min(page * PER_PAGE, data.size());
		for (int i = start; i < end; i++) {
			final String line = convert.apply(data.get(i));
			addChatLine(line);
		}
	}
}
