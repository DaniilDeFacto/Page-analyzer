package hexlet.code.controller;

import hexlet.code.NamedRoutes;
import hexlet.code.dto.BasePage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var page = new UrlsPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setColor(ctx.sessionAttribute("color"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
        var page = new UrlPage(url);
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void build(Context ctx) {
        var page = new BasePage();
        page.setFlash(ctx.sessionAttribute("flash"));
        page.setColor(ctx.sessionAttribute("color"));
        ctx.render("main.jte", Collections.singletonMap("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var inputName = ctx.formParamAsClass("name", String.class).getOrDefault(null);
        URL inputUrl = null;
        try {
            inputUrl = new URL(inputName);
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("color", "danger");
            ctx.redirect(NamedRoutes.mainPath());
        }
        if (inputUrl != null) {
            String protocol = inputUrl.getProtocol();
            String authority = inputUrl.getAuthority();
            var name = String.format("%s://%s", protocol, authority);
            var createdAt = new Timestamp(System.currentTimeMillis());
            var url = new Url(name, createdAt);
            var uniqueness = UrlRepository.getEntities().stream()
                    .noneMatch(entity -> entity.getName().equals(name));
            if (uniqueness) {
                UrlRepository.save(url);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("color", "success");
                ctx.redirect(NamedRoutes.urlsPath());
            } else {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("color", "info");
                ctx.redirect(NamedRoutes.urlsPath());
            }
        }
    }
}
