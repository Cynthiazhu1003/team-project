package api.news;

/**
 * Interface for fetching top headlines from NewsAPI.org.
 */
public interface NewsApiGateway {

    /**
     * Fetches top headlines using a customizable request.
     *
     * @param request the search parameters
     * @return a list of headline strings
     * @throws NewsApiException if any errors occur during the API call
     */
    NewsApiResponse getTopHeadlines(TopHeadlinesRequest request) throws NewsApiException;

    /**
     * Customizable request container for top headlines.
     */
    class TopHeadlinesRequest {
        public String country;
        public String category;
        public String query;
        public Integer pageSize;
        public Integer page;
    }

    /**
     * Exception for the News API.
     */
    class NewsApiException extends Exception {
        public NewsApiException(String message) {
            super(message);
        }

        public NewsApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}