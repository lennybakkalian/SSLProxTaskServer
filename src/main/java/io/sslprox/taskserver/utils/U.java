package io.sslprox.taskserver.utils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;

public class U {

	public static interface Async {
		public void exec() throws Exception;
	}

	public static void async(Async a) {
		new Thread(() -> {
			try {
				a.exec();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	public static void asyncfx(Async a) {
		Platform.runLater(() -> {
			try {
				a.exec();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void async(Async a, EmptyCallback onComplete) {
		new Thread(() -> {
			try {
				a.exec();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (onComplete != null)
				onComplete.callback();
		}).start();
	}

	public static void popup(String title, String message, AlertType alertType) {
		asyncfx(() -> {
			Alert alert = new Alert(alertType);
			alert.setTitle(title);
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.show();
		});
	}

	public static void inputPopup(String title, String message, ObjCallback<String> callback) {
		inputPopup(title, message, null, callback);
	}

	public static void inputPopup(String title, String message, String fillWithText, ObjCallback<String> callback) {
		asyncfx(() -> {
			TextInputDialog dialog = new TextInputDialog(fillWithText);
			dialog.setTitle(title);
			dialog.setHeaderText(null);
			dialog.setContentText(message);
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(text -> callback.callback(text));
		});
	}

	public static void openWebsite(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e) {
			e.printStackTrace();
			popup("Error", "Can't open website: " + e.getMessage(), AlertType.ERROR);
		}
	}

	public static <T> List<T> merge(List<T> old, List<T> updated, MatchInterface<T> m, UpdateInterface<T> u) {
		List<T> result = new ArrayList<T>();
		// remove missing items from updated array
		old.forEach(i -> {
			updated.forEach(i2 -> {
				if (m.match(i, i2)) {
					if (u != null)
						u.update(i2, i);
					result.add(i);
				}
			});
		});
		// add missing items
		updated.forEach(i -> {
			boolean contains = false;
			for (T i2 : result)
				if (m.match(i, i2))
					contains = true;
			if (!contains)
				result.add(i);
		});
		return result;
	}

	public static interface MatchInterface<T> {
		public boolean match(T a, T b);
	}

	public static interface UpdateInterface<T> {
		public void update(T src, T target);
	}

	public static interface ObjCallback<T> {
		public void callback(T t);
	}

	public static void exec(String cmd, Callback closeCallback, Callback outputCallback) {
		U.async(() -> {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while (true) {
				line = r.readLine();
				if (line == null) {
					break;
				}
				if (outputCallback != null)
					outputCallback.callback(line);
			}
			if (closeCallback != null)
				closeCallback.callback();
		});
	}

	public static interface Callback {
		public void callback(Object... args);
	}

	public static interface EmptyCallback {
		public void callback();
	}

	public static String formatPrice(float price) {
		return new DecimalFormat("#0.00").format(price);
	}
}
