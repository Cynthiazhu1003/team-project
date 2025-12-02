package api.fina;

import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * FinaCategorizationGateway implementation that relies on the app.fina.money categorization API.
 * Note that all failures get reported as FinaCategorizationException
 * exceptions to align with the requirements of the FinaCategorizationGateway interface.
 */
public class FinaCategorizationGatewayImpl implements FinaCategorizationGateway {

    private final OkHttpClient client = new OkHttpClient();

    /**
     * Generate a category based on the given information of a row
     * in the format ["name", "merchant", "amount"].
     *
     * @param rows the rows to get the correctly formatted individual row of data
     * @return list of the same rows param but with an auto generated category attached to each row
     * @throws FinaCategorizationException if the API call fails for any reason or code issues
     */
    @Override
    public List<List<String>> categorize(List<List<String>> rows)
            throws FinaCategorizationException {
        try {
            String jsonBody = convertRowsToJson(rows);

            Request request = new Request.Builder()
                    .url("https://app.fina.money/api/resource/categorize")
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", "fina-api-test")
                    .addHeader("x-partner-id", "your-partner-id")
                    .addHeader("x-api-model", "v2")
                    .addHeader("x-api-mapping", "true")
                    .build();

            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String responseBody = response.body().string();

            return attachCategories(rows, responseBody);

        } catch (Exception e) {
            throw new FinaCategorizationException("Failed to categorize using Fina API", e);
        }
    }

    // Parse the rows into the correct JSON format for the API
    private String convertRowsToJson(List<List<String>> rows) {
        JSONArray jsonArray = new JSONArray();

        for (List<String> row : rows) {
            JSONObject item = new JSONObject();
            item.put("name", row.get(0));
            item.put("merchant", row.get(1));
            item.put("amount", Double.parseDouble(row.get(2)));

            jsonArray.put(item);
        }

        return jsonArray.toString();
    }

    // Attaches a category to the individual rows
    private List<List<String>> attachCategories(List<List<String>> rows, String jsonResponse) {
        JSONArray jsonArray = new JSONArray(jsonResponse);
        List<List<String>> updated = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            List<String> row = new ArrayList<>(rows.get(i));

            String category = jsonArray.getString(i);

            row.add(category);
            updated.add(row);
        }

        return updated;
    }
}