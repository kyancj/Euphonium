package net.cancheta;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This sends a list of all visible players to a server, e.g. to display it on a
 * map. The server is configured in the minebot preferences.
 * 
 * @author michael
 * 
 */
public class PlayerUpdateHandler {
	private final class SendToServerTask implements Runnable {
		private final String json;

		private SendToServerTask(String json) {
			this.json = json;
		}

		@Override
		public void run() {
			HttpURLConnection connection = null;
			try {
				final String urlParameters = "players="
						+ URLEncoder.encode(json, java.nio.charset.StandardCharsets.UTF_8.toString());
				if (!toLoaded) {
//					to = new MinebotSettings().get("report_position_to",
//							null);
					//FIXME: Allow something here.
					to = null;
					toLoaded = true;
				}
				if (to == null) {
					return;
				}
				final URL url = new URL(to);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length",
						Integer.toString(urlParameters.getBytes().length));
				connection.setRequestProperty("Content-Language", "en-US");
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				final DataOutputStream wr = new DataOutputStream(connection
						.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
				final InputStream is = connection.getInputStream();
				final BufferedReader rd = new BufferedReader(
						new InputStreamReader(is));
				while (rd.readLine() != null) {
				}
				rd.close();
			} catch (final Throwable t) {
				t.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
	}

	private final ExecutorService sendThread;
	private final Hashtable<String, Long> blockTimes = new Hashtable<String, Long>();
	private boolean toLoaded;
	private String to;

	public PlayerUpdateHandler() {
		sendThread = Executors.newFixedThreadPool(2);
	}
	
	public void onPlayerTick(MinecraftClient client) {
//		if (toLoaded && to == null) {
//			return;
//		}
//		final PlayerEntity player = client.player;
//		final String name = player.getDisplayName().getString();
//		final Long blocked = blockTimes.get(name);
//		if (blocked != null && blocked > System.currentTimeMillis()) {
//			return;
//		}
//		blockTimes.put(name, System.currentTimeMillis() + 2000);
//
//		final String json = String
//				.format("{\"players\":[{\"username\": \"%s\", \"x\": %d, \"y\" : %d, \"z\": %d, \"world\" : \"world\"}]}",
//						StringEscapeUtils.escapeJava(name), (int) player.getX(),
//						(int) player.getY(), (int) player.getZ());
//		sendThread.execute(new SendToServerTask(json));
	}
}

