@import hexlet.code.Utils
@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.NamedRoutes
@param UrlsPage page

@template.layout.page(
    page = page,
    content = @`
        <div class="container-lg mt-5">
            <h1>Сайты</h1>
            <table class="table table-bordered table-hover mt-3">
                <thead>
                    <tr>
                        <th class="col-1">ID</th>
                        <th>Имя</th>
                        <th class="col-2">Последняя проверка</th>
                        <th class="col-1">Код ответа</th>
                    </tr>
                </thead>
                @if(!page.getUrlsWithLastChecks().isEmpty())
                    <tbody>
                        @for(var urlWithCheck : page.getUrlsWithLastChecks().entrySet())
                            <tr>
                                <td>
                                    ${urlWithCheck.getKey().getId()}
                                </td>
                                <td>
                                    <a href="${NamedRoutes.urlPath(urlWithCheck.getKey().getId())}">
                                        ${urlWithCheck.getKey().getName()}</a>
                                </td>
                                <td>
                                    @if(urlWithCheck.getValue() != null)
                                        ${Utils.timeFormat(urlWithCheck.getValue().getCreatedAt())}
                                    @endif
                                </td>
                                <td>
                                    @if(urlWithCheck.getValue() != null)
                                        ${urlWithCheck.getValue().getStatusCode()}
                                    @endif
                                </td>
                            </tr>
                        @endfor
                    </tbody>
                @endif
            </table>
        </div>

    `
)