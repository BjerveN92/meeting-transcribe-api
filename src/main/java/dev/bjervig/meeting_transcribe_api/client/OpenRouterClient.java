package dev.bjervig.meeting_transcribe_api.client;

import dev.bjervig.meeting_transcribe_api.dto.openrouter.ChatRequest;
import dev.bjervig.meeting_transcribe_api.dto.openrouter.ChatResponse;
import dev.bjervig.meeting_transcribe_api.dto.openrouter.SttRequest;
import dev.bjervig.meeting_transcribe_api.dto.openrouter.SttResponse;
import dev.bjervig.meeting_transcribe_api.exception.OpenRouterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OpenRouterClient {

    private final WebClient openRouterWebClient;

    public SttResponse stt(SttRequest request) {
        return openRouterWebClient.post()
            .uri("/audio/transcriptions")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, this::handleError)
            .bodyToMono(SttResponse.class)
            .block();
    }

    public ChatResponse chat(ChatRequest request) {
        return openRouterWebClient.post()
            .uri("/chat/completions")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, this::handleError)
            .bodyToMono(ChatResponse.class)
            .block();
    }

    private Mono<? extends Throwable> handleError(ClientResponse response) {
        return response.bodyToMono(String.class)
            .map(body -> new OpenRouterException(
                response.statusCode().value(), body));
    }
}
