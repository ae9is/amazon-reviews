package amazonrev.recommend;

import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import amazonrev.config.Constants;
import amazonrev.util.exception.NotFoundException;

@Component
public class ModelApiClient {

  final int timeout = Constants.getModelApiTimeout();
  WebClient webClient;
  private final NotFoundException modelApiDisabledException = new NotFoundException("Model API is not enabled");

  public ModelApiClient() {
    // Custom http client underlying web client in order to set shorter timeouts
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
        .responseTimeout(Duration.ofMillis(timeout))
        .doOnConnected(con -> 
            con.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
              .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS)));
    webClient = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(Constants.getModelApiUrl())
        .build();
  }

  HashMap<String, ?> post(String path, Object body) throws WebClientException {
    if (!Constants.isModelApiEnabled()) {
      throw modelApiDisabledException;
    }
    HashMap<String, ?> resp = webClient.post()
        .uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .accept(MediaType.APPLICATION_JSON)
        .acceptCharset(StandardCharsets.UTF_8)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<HashMap<String, ?>>() {})
        .block();
    return resp;
  }
}
