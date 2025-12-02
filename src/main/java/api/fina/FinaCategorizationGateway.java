package api.fina;

import java.util.List;

/**
 * Interface for the service of auto categorization a
 * list of lists of strings in the format [["name", "merchant", "amount"]].
 */
public interface FinaCategorizationGateway {
    /**
     * Adds a category to each row of the given list of lists.
     *
     * @param rows the list of rows of transactions in the given format above to auto categorize for
     * @return the same list of rows but with the category
     * generated from the api attached to each row separately
     * @throws FinaCategorizationException if any errors occur during the gateway call
     */
    List<List<String>> categorize(List<List<String>> rows) throws FinaCategorizationException;

    /**
     * Exception class for the categorization interface.
     */
    class FinaCategorizationException extends Exception {
        public FinaCategorizationException(String message) {
            super(message);
        }

        public FinaCategorizationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}