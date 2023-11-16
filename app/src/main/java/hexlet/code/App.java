package hexlet.code;

import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class App {

    public static Javalin getApp() {
        var app = Javalin.create(config -> config.plugins.enableDevLogging());
        Logger logger = LoggerFactory.getLogger(App.class);
        logger.info("Test logger");
        app.get("/", ctx -> ctx.result("Hello World"));
        return app;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    public static void main(String[] args) {
        var app = getApp();
        app.start(getPort());
    }
}
