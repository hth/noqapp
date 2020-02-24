package com.noqapp.view.controller.open;

import com.noqapp.service.nlp.NLPService;
import com.noqapp.view.form.ChatConversationForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * hitender
 * 2/23/20 1:21 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/open/chat")
public class ChatController {
    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);

    private NLPService nlpService;

    @Autowired
    public ChatController(NLPService nlpService) {
        this.nlpService = nlpService;
    }

    @GetMapping(value = "/conversation")
    public String conversation(
        @ModelAttribute("chatConversationForm")
        ChatConversationForm chatConversationForm
    ) {
        LOG.info("Chat page invoked");
        return "chatConversation";
    }

    @PostMapping(value = "/conversation")
    public String conversation(
        @ModelAttribute("chatConversationForm")
        ChatConversationForm chatConversationForm,

        HttpServletRequest request
    ) {
        LOG.info("Chat page invoked");
        List<String> nouns = nlpService.lookupNoun(chatConversationForm.getConversation().getText());
        if (0 != nouns.size()) {
            chatConversationForm.getChatNouns().addAll(nouns);
        } else {
            //TODO query hard
        }
        chatConversationForm.addChatMessage();
        chatConversationForm.repopulate();
        chatConversationForm.getChatMessagesAsString();
        chatConversationForm.getChatNounsAsString();
        chatConversationForm.setConversation(null);
        return "chatConversation";
    }
}
