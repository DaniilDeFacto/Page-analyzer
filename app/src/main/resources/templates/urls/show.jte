@import hexlet.code.Utils
@import hexlet.code.dto.urls.UrlPage
@import hexlet.code.NamedRoutes
@param UrlPage page

@template.layout.page(
    page = page,
    content = @`
        <div class="container-lg mt-5">
            <h1>Сайт: ${page.getUrl().getName()}</h1>
            <table class="table table-bordered table-hover mt-3">
                <tbody>
                    <tr>
                        <td>ID</td>
                        <td>${page.getUrl().getId()}</td>
                    </tr>
                    <tr>
                        <td>Имя</td>
                        <td>${page.getUrl().getName()}</td>
                    </tr>
                    <tr>
                        <td>Дата создания</td>
                        <td>${Utils.timeFormat(page.getUrl().getCreatedAt())}</td>
                    </tr>
                </tbody>
            </table>
            <h2 class="mt-5">Проверки</h2>
            <form method="post" action="${NamedRoutes.urlChecksPath(page.getUrl().getId())}">
                <button type="submit" class="btn btn-primary">Запустить проверку</button>
            </form>
            <table class="table table-bordered table-hover mt-3">
                <thead>
                    <th class="col-1">ID</th>
                    <th class="col-1">Код ответа</th>
                    <th>title</th>
                    <th>h1</th>
                    <th>description</th>
                    <th class="col-2">Дата проверки</th>
                </thead>
                @if(!page.getUrlChecks().isEmpty())
                    <tbody>
                        @for(var urlCheck : page.getUrlChecks())
                            <tr>
                                <td>
                                    ${urlCheck.getId()}
                                </td>
                                <td>
                                    ${urlCheck.getStatusCode()}
                                </td>
                                <td>
                                    ${urlCheck.getTitle()}
                                </td>
                                <td>
                                    ${urlCheck.getH1()}
                                </td>
                                <td>
                                    ${urlCheck.getDescription()}
                                </td>
                                <td>
                                    ${Utils.timeFormat(urlCheck.getCreatedAt())}
                                </td>
                            </tr>
                        @endfor
                    </tbody>
                @endif
            </table>
        </div>
    `
)