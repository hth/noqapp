package com.noqapp.view.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.noqapp.service.ShowHTMLService;
import com.noqapp.utils.ScrubbedInput;

/**
 * User: hitender
 * Date: 1/16/17 8:12 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
public class ShowStoreController {
    private ShowHTMLService showHTMLService;

    @Autowired
    public ShowStoreController(ShowHTMLService showHTMLService) {
        this.showHTMLService = showHTMLService;
    }

    /**
     * Loads biz store page when code scanned is not from our app but some other code scanning app.
     *
     * @return
     */
    @RequestMapping (value = "/{codeQR}/c", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String showStoreByCodeQR(@PathVariable ("codeQR") ScrubbedInput codeQR) {
        return showHTMLService.showStoreByCodeQR(codeQR.getText());
    }

    /**
     * Loads biz store page when code scanned is not from our app but some other code scanning app.
     *
     * @return
     */
    @RequestMapping (value = "/{webLocation}/q", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String showStoreByWebLocation(@PathVariable ("webLocation") ScrubbedInput webLocation) {
        return showHTMLService.showStoreByWebLocation(webLocation.getText());
    }
}
