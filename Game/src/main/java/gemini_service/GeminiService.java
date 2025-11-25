package gemini_service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GeminiService {

    private final Client client;

    public GeminiService() {
        client = Client.builder().apiKey("AIzaSyClBGYuYrMjJ4Y1ThQlm-ANwaVIPx_PKUc").build();
    }

    /**
     * Llamado desde un hilo EN SEGUNDO PLANO.
     * NO llames esto directamente desde JavaFX Application Thread.
     */
    public String generateDialogue(String prompt) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        // En tu ejemplo usas response.text()
        System.out.println(response.text());
        return response.text();
    }
}

