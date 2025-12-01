package com.example.webhookstarter.service;

import com.example.webhookstarter.dto.GenerateWebhookRequest;
import com.example.webhookstarter.dto.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class StartupRunner implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(StartupRunner.class);

    private final WebClient webClient;
    private final SQLSolver solver;

    @Value("${app.name}")
    private String name;

    @Value("${app.regNo}")
    private String regNo;

    @Value("${app.email}")
    private String email;

    @Value("${app.generateWebhookUrl}")
    private String generateWebhookUrl;

    @Value("${app.testWebhookPath}")
    private String testWebhookUrl;

    public StartupRunner(WebClient.Builder webClientBuilder, SQLSolver solver) {
        this.webClient = webClientBuilder.build();
        this.solver = solver;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting webhook flow on startup — contacting generateWebhook endpoint");

        GenerateWebhookRequest req = new GenerateWebhookRequest(name, regNo, email);

        GenerateWebhookResponse resp = webClient.post()
                .uri(generateWebhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(GenerateWebhookResponse.class)
                .onErrorResume(ex -> {
                    log.warn("Could not reach generateWebhook endpoint: {}. Using mocked response for testing.", ex.getMessage());
                    GenerateWebhookResponse mock = new GenerateWebhookResponse();
                    mock.setWebhook(testWebhookUrl);
                    mock.setAccessToken("MOCKED_TOKEN_123");
                    return Mono.just(mock);
                })
                .block();

        if (resp == null) {
            log.error("No response from generateWebhook; aborting.");
            return;
        }

        log.info("Received webhook: {} and accessToken (truncated): {}", resp.getWebhook(), tokenPreview(resp.getAccessToken()));

        // Solve the assigned SQL question
        String finalQuery = solver.finalQueryForRegNo(regNo);

        // Save locally
        saveResult(finalQuery);

        // Send the final query back
        sendFinalQuery(resp.getWebhook(), resp.getAccessToken(), finalQuery);

        log.info("Startup webhook flow complete.");
    }

    private void saveResult(String finalQuery) {
        try {
            Path out = Path.of("final_query.sql");
            Files.writeString(out, finalQuery);
            log.info("Saved final query to {}", out.toAbsolutePath());
        } catch (IOException ex) {
            log.warn("Could not save final query file: {}", ex.getMessage());
        }
    }

    private void sendFinalQuery(String webhookUrl, String accessToken, String finalQuery) {
        try {
                ClientResponse clientResponse = webClient.post()
                    .uri(webhookUrl)
                    // pass the raw accessToken as the Authorization header per challenge spec
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(java.util.Map.of("finalQuery", finalQuery))
                    .exchange()
                    .block();

            if (clientResponse != null && clientResponse.statusCode().is2xxSuccessful()) {
                log.info("Final query posted successfully to webhook.");
            } else {
                log.warn("Posting final query returned non-2xx response: {}", clientResponse != null ? clientResponse.statusCode() : "null");
            }
        } catch (Exception ex) {
            log.warn("Failed to post final query to webhook: {}", ex.getMessage());
        }
    }

    private String tokenPreview(String t) {
        if (t == null) return "<null>";
        if (t.length() <= 10) return t;
        return t.substring(0, 6) + "..." + t.substring(t.length() - 4);
    }
}
