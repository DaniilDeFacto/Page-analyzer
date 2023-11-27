package hexlet.code.controller;

import hexlet.code.NamedRoutes;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;

import java.sql.SQLException;
import java.sql.Timestamp;

public class UrlChecksController {
    public static void makeCheck(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        var url = UrlRepository.findById(urlId)
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
