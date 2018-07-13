package com.noqapp.service;

import com.noqapp.domain.InviteEntity;
import com.noqapp.repository.InviteManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 3/30/17 3:16 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class InviteService {
    private static final Logger LOG = LoggerFactory.getLogger(InviteService.class);

    private InviteManager inviteManager;

    @Autowired
    public InviteService(InviteManager inviteManager) {
        this.inviteManager = inviteManager;
    }

    public void save(InviteEntity invite) {
        inviteManager.save(invite);
    }
}
