package com.token.view.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.token.service.ShowHTMLService;
import com.token.utils.ScrubbedInput;

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
    @RequestMapping (value = "/{codeQR}/q", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String landing(@PathVariable ("codeQR") ScrubbedInput codeQR) {
        return showHTMLService.showStore(codeQR.getText());
    }
}
