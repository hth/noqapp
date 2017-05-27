package com.noqapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noqapp.domain.InviteEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.repository.InviteManager;

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

    @Mobile
    public int getRemoteJoinCount(String rid) {
        return inviteManager.getRemoteJoinCount(rid);
    }

    @Mobile
    public boolean deductRemoteJoinCount(String rid) {
        return inviteManager.deductRemoteJoinCount(rid);
    }
}
