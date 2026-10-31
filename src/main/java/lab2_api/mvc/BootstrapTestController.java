
package lab2_api.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BootstrapTestController {

    @GetMapping("/bootstrap-test")
    public String showBootstrapTest() {
        return "bootstrap-test";
    }
}
