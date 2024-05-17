package amazonrev.recommend;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import amazonrev.config.Constants;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

@Component
public class PythonApiClient {

  final int timeout = 5000;

  WebClient webClient;

  public PythonApiClient() {
    // Custom http client underlying web client in order to set shorter timeouts
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
        .responseTimeout(Duration.ofMillis(timeout))
        .doOnConnected(con -> 
            con.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
               .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS)));
    webClient = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(Constants.getPythonEndpoint())
        .build();
  }

  <T> T post(String path, Object body) throws WebClientException {
    @SuppressWarnings("unchecked")
    T resp = (T) webClient.post()
        .uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .accept(MediaType.APPLICATION_JSON)
        .acceptCharset(StandardCharsets.UTF_8)
        .retrieve()
        .bodyToMono(Object.class)
        .block();
    return resp;
  }
}
