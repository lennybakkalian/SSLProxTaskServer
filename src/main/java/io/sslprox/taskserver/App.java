package io.sslprox.taskserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import io.sslprox.taskserver.utils.CryptUtils;

public class App {

	public static final Logger logger = LoggerFactory.getLogger(App.class);

	public static File configFile = new File("taskserver.sslprox");
	public static Map<String, String> config = new HashMap<String, String>() {
		{
			put("port", "2222");
			put("key", CryptUtils.generateRandomString(35));
		}
	};

	public static Javalin server;

	public static List<Client> clients = new ArrayList<Client>();

	public static void main(String[] args) {
		try {
			logger.info("Load config...");
			loadConfig();

			for (String arg : args) {
				String[] a = arg.split("=");
				config.put(a[0], a.length == 2 ? a[1] : null);
			}
			if (args.length > 0)
				saveConfig();

			if (config.get("key").length() <= 10) {
				logger.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				logger.warn("!!! THE KEY IS NOT SECURE. PLEASE USE A KEY WITH MORE CHARACTERS !!!");
				logger.warn("!!!           ATTACKERS COULD TAKE OVER THE SERVER               !!!");
				logger.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}

			logger.info("################# CONFIG #################");
			logger.info("Key: " + config.get("key"));
			logger.info("Port: " + config.get("port"));
			logger.info("##########################################");
			server = Javalin.create(config -> {

			});

			server.ws("/:key", handler -> {
				handler.onConnect(ctx -> {
					// validate key
					String key = ctx.pathParam("key");
					if (key == null || !key.equals(config.get("key"))) {
						ctx.session.close();
						return;
					}
					Client client = new Client(ctx);
					clients.add(client);
				});
				handler.onClose(ctx -> clients.remove(clientByContext(ctx)));
				handler.onMessage(ctx -> clientByContextCallback(ctx, client -> client.onMessage(ctx)));
			});

			server.start(Integer.valueOf(config.get("port")));

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1); // close threads
		}
	}

	public static Client clientByContext(WsContext context) {
		for (int i = clients.size() - 1; i >= 0; i--)
			if (clients.get(i).getContext().getSessionId().equals(context.getSessionId()))
				return clients.get(i);
		return null;
	}

	public static void clientByContextCallback(WsContext ctx, IFoundClient foundClientCallback) {
		Client c = clientByContext(ctx);
		if (c == null) {
			ctx.session.close();
			return;
		}
		foundClientCallback.callback(c);
	}

	public static void saveConfig() {
		if (configFile.exists())
			configFile.delete();
		try {
			FileWriter fw = new FileWriter(configFile);
			fw.write(new ObjectMapper().writeValueAsString(config));
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("cant save config");
		}
	}

	public static void loadConfig() {
		if (!configFile.exists())
			saveConfig(); // save config
		try {
			BufferedReader br = new BufferedReader(new FileReader(configFile));
			String configStr = br.readLine();
			Map<String, String> newConfig = new ObjectMapper().readValue(configStr, Map.class);
			config.putAll(newConfig);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static interface IFoundClient {
		public void callback(Client client);
	}

}
