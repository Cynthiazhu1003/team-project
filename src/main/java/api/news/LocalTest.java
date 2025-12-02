package api.news;

/**
 * Local test class for the News API.
 */
public class LocalTest {
    /**
     * Main for the local test class.
     */
    public static void main(String[] args) throws NewsApiGateway.NewsApiException {

        NewsApiGatewayImpl gateway = new NewsApiGatewayImpl();

        NewsApiGateway.TopHeadlinesRequest req = new NewsApiGateway.TopHeadlinesRequest();
        req.category = "business";    // optionally filter by category
        req.country = "us";        // optionally filter by country
        // req.query = "inflation";   // optionally keyword search
        // req.pageSize = 20;         // optional
        // req.page = 1;              // optional

        NewsApiResponse resp = gateway.getTopHeadlines(req);

        System.out.println("Status: " + resp.status);
        System.out.println("Total Results: " + resp.totalResults);

        for (NewsApiResponse.Article a : resp.articles) {
            System.out.println("-----------------");
            System.out.println("Title: " + a.title);
            System.out.println("Author: " + a.author);
            System.out.println("Source: " + (a.source != null ? a.source.name : "Unknown"));
            System.out.println("Published: " + a.publishedAt);
            System.out.println("URL: " + a.url);
            System.out.println();
        }
    }
}