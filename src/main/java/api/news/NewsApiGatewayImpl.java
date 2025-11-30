package api.news;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of NewsApiGateway with optional query parameters,
 * loading NEWS_API_KEY from a .env file.
 */
public class NewsApiGatewayImpl implements NewsApiGateway {

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    public NewsApiGatewayImpl() throws NewsApiException {
        // Load .env
        Dotenv dotenv = Dotenv.configure()
                .directory("./")     // looks in project root
                .ignoreIfMalformed()
                .ignoreIfMissing()   // change if you want to enforce existence
                .load();

        this.apiKey = dotenv.get("NEWS_API_KEY");

        // 🔴 DEBUG LINE GOES RIGHT HERE
        System.out.println("DEBUG: NEWS_API_KEY from .env = " + this.apiKey);

        if (apiKey == null || apiKey.isEmpty()) {
            throw new NewsApiException(
                    "Missing NEWS_API_KEY in .env file. Add NEWS_API_KEY=your_key to your .env"
            );
        }
    }

    @Override
    public NewsApiResponse getTopHeadlines(TopHeadlinesRequest req) throws NewsApiException {
        try {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://newsapi.org/v2/top-headlines"))
                    .newBuilder();

            if (req.country != null) urlBuilder.addQueryParameter("country", req.country);
            if (req.category != null) urlBuilder.addQueryParameter("category", req.category);
            if (req.query != null) urlBuilder.addQueryParameter("q", req.query);
            if (req.pageSize != null) urlBuilder.addQueryParameter("pageSize", req.pageSize.toString());
            if (req.page != null) urlBuilder.addQueryParameter("page", req.page.toString());

            urlBuilder.addQueryParameter("apiKey", apiKey);

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .addHeader("Accept", "application/json")
                    .build();

            /*Response response = client.newCall(request).execute();

            if (response.body() == null) {
                throw new NewsApiException("Empty NewsAPI response body");
            }

            return parseResponse(response.body().string());*/
            Response response = client.newCall(request).execute();

            if (response.body() == null) {
                throw new NewsApiException("Empty NewsAPI response body");
            }

            // Read body *once* into a String
            String body = response.body().string();

            // TEMP: print for debugging
            System.out.println("HTTP status code = " + response.code());
            System.out.println("Raw JSON from NewsAPI = " + body);

            return parseResponse(body);

        } catch (Exception e) {
            throw new NewsApiException("Failed to fetch headlines from NewsAPI", e);
        }
    }

    // Parses response (check NewsApiResponse.java for more details about the format of the output)
    private NewsApiResponse parseResponse(String json) {

        JSONObject obj = new JSONObject(json);

        NewsApiResponse response = new NewsApiResponse();
        response.status = obj.optString("status");
        response.totalResults = obj.optInt("totalResults");

        JSONArray arr = obj.optJSONArray("articles");
        List<NewsApiResponse.Article> list = new ArrayList<>();

        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject a = arr.getJSONObject(i);

                NewsApiResponse.Article article = new NewsApiResponse.Article();
                article.author = a.optString("author", null);
                article.title = a.optString("title", null);
                article.description = a.optString("description", null);
                article.url = a.optString("url", null);
                article.urlToImage = a.optString("urlToImage", null);
                article.publishedAt = a.optString("publishedAt", null);
                article.content = a.optString("content", null);

                JSONObject s = a.optJSONObject("source");
                NewsApiResponse.Source src = new NewsApiResponse.Source();
                if (s != null) {
                    src.id = s.optString("id", null);
                    src.name = s.optString("name", null);
                }
                article.source = src;

                list.add(article);
            }
        }

        response.articles = list;
        return response;
    }
}