package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public final class AppTest {
    private static Javalin app;
    private MockWebServer mockServer;

    @BeforeEach
    public void beforeEach() throws SQLException, IOException {
        app = App.getApp();
        mockServer = new MockWebServer();
        mockServer.start();
    }
    @AfterEach
    public void afterEach() throws IOException {
        mockServer.shutdown();
        app.stop();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "name=https://some-domain.org/example/path";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://some-domain.org");
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        var url = new Url("https://some-domain.org", new Timestamp(System.currentTimeMillis()));
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testMakeCheck() throws SQLException, IOException {
        String page = Files.readString(Paths.get("./src/test/resources/testPage.html"));
        MockResponse mockResponse = new MockResponse().setResponseCode(200).setBody(page);
        mockServer.enqueue(mockResponse);
        String urlString = mockServer.url("/").toString();
        Url testUrl = new Url(urlString, new Timestamp(System.currentTimeMillis()));
        UrlRepository.save(testUrl);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + testUrl.getId() + "/checks");
            assertThat(response.code()).isEqualTo(200);
            var lastCheck = testUrl.getLastCheck();
            assertThat(lastCheck).isNotNull();
            assertThat(lastCheck.getStatusCode()).isEqualTo(200);
            assertThat(lastCheck.getTitle()).isEqualTo("Sample title");
            assertThat(lastCheck.getH1()).isEqualTo("Sample header");
            assertThat(lastCheck.getDescription()).contains("Sample description");
        });
    }
}
