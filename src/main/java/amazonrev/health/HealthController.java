package amazonrev.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/v1/healthz")
  public String healthz() {
    return String.format("OK");
  }
}
