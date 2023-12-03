package IndirektniPSF.backend.frontend_routing;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouteForwardingController {

//    @RequestMapping(value = "/{path:^(?!.*\\..*$).*$}/**")
    @RequestMapping(value = "/**/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}


