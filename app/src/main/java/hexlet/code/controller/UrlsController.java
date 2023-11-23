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
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getUrls();
        var page = new UrlsPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setColor(ctx.consumeSessionAttribute("color"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));
        var urlChecks = UrlCheckRepository.getUrlChecks(id);
        var page = new UrlPage(url, urlChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setColor(ctx.consumeSessionAttribute("color"));
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void build(Context ctx) {
        var page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setColor(ctx.consumeSessionAttribute("color"));
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

    public static void makeCheck(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        var url = UrlRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + urlId + " not found"));
        String name = url.getName();
        try {
            HttpResponse<String> response = Unirest.get(name).asString();
            var statusCode = response.getStatus();
            var document = Jsoup.parse(response.getBody());
            var createdAt = new Timestamp(System.currentTimeMillis());
            var title = document.title().isEmpty() ? null : document.title();
            var h1Element = document.selectFirst("h1");
            var h1 = h1Element == null ? null : h1Element.ownText();
            var descriptionElement = document.selectFirst("meta[name=description]");
            var description = descriptionElement == null ? null : descriptionElement.attr("content");
            var urlCheck = new UrlCheck(statusCode, title, h1, description, urlId, createdAt);
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("color", "success");
            ctx.redirect(NamedRoutes.urlPath(urlId));
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("color", "danger");
            ctx.redirect(NamedRoutes.urlPath(urlId));
        }
    }
}
