package api.news;

import java.util.List;

/**
 * News Api response class for formatting a response from the API gateway.
 */
public class NewsApiResponse {
    public String status;
    public int totalResults;
    public List<Article> articles;

    /**
     * A class for a formatted article from the API gateway.
     */
    public static class Article {
        public Source source;
        public String author;
        public String title;
        public String description;
        public String url;
        public String urlToImage;
        public String publishedAt;
        public String content;
    }

    /**
     * The source of these articles, null for multiple sources.
     */
    public static class Source {
        public String id;
        public String name;
    }
}