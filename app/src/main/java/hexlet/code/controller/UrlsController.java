package hexlet.code.controller;

import hexlet.code.NamedRoutes;
import hexlet.code.dto.BasePage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;

public class UrlsController {
    public static void showUrlList(Context ctx) throws SQLException {
        var urlsWithLastChecks = new HashMap<Url, UrlCheck>();
        var urls = UrlRepository.getUrls();
        for (var url : urls) {
            var lastCheck = UrlCheckRepository.getLastCheck(url.getId()).isPresent()
                    ? UrlCheckRepository.getLastCheck(url.getId()).get() : null;
            urlsWithLastChecks.put(url, lastCheck);
        }
        var page = new UrlsPage(urlsWithLastChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setColor(ctx.consumeSessionAttribute("color"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void showUrl(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        var url = UrlRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));
        var urlChecks = UrlCheckRepository.getUrlChecks(id);
        var page = new UrlPage(url, urlChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setColor(ctx.consumeSessionAttribute("color"));
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void showMainPage(Context ctx) {
        var page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setColor(ctx.consumeSessionAttribute("color"));
        ctx.render("main.jte", Collections.singletonMap("page", page));
    }

    public static void createUrl(Context ctx) throws SQLException {
        var inputName = ctx.formParamAsClass("url", String.class).getOrDefault(null);
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
            var url = new Url(name);
            var uniqueness = UrlRepository.getUrls().stream()
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
