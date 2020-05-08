package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.UserLevelEnum;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/19/16 12:36 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public final class UserProfileManagerImpl implements UserProfileManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        UserProfileEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    /**
     * When OptimisticLockingFailureException happen, ignore and re-create record.
     */
    private boolean ignoreOptimisticLockingFailureException = false;

    @Autowired
    public UserProfileManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserProfileEntity object) {
        try {
            if (object.getId() != null) {
                if (!ObjectId.isValid(object.getId())) {
                    LOG.error("UserProfileId is not valid id={} qid={}", object.getId(), object.getQueueUserId());
                }
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (OptimisticLockingFailureException e) {
            //TODO may be remove this condition in future. This is annoying temporary condition.
            if (ignoreOptimisticLockingFailureException) {
                /* This will re-create user profile with same details every time when there is a failure. */
                LOG.error("UserProfile saving optimistic locking failure, override optimistic locking qid={} reason={}",
                    object.getQueueUserId(), e.getLocalizedMessage(), e);

                DeleteResult deleteResult = mongoTemplate.remove(
                    query(where("QID").is(object.getQueueUserId())),
                    UserProfileEntity.class,
                    TABLE);
                if (deleteResult.getDeletedCount() > 0) {
                    LOG.info("Deleted optimistic locking data issue for qid={}", object.getQueueUserId());
                    object.setId(null);
                    object.setVersion(null);
                    mongoTemplate.save(object, TABLE);
                } else {
                    LOG.error("Delete failed on locking issue for qid={}", object.getQueueUserId());
                    throw e;
                }
            } else {
                throw e;
            }
        } catch (DataIntegrityViolationException e) {
            LOG.error("Found existing userProfile qid={} email={}", object.getQueueUserId(), object.getEmail());
            throw e;
        }
    }

    @Override
    public UserProfileEntity findByQueueUserId(String qid) {
        return mongoTemplate.findOne(byQueueUserId(qid, true), UserProfileEntity.class, TABLE);
    }

    private Query byQueueUserId(String qid, boolean activeProfile) {
        if (activeProfile) {
            return query(where("QID").is(qid).andOperator(isActive()));
        } else {
            return query(where("QID").is(qid));
        }
    }

    @Override
    public UserProfileEntity getById(String id) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(query(where("id").is(id)), UserProfileEntity.class, TABLE);
    }

    @Override
    public void deleteHard(UserProfileEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public List<UserProfileEntity> searchAllByName(String name) {
        //TODO look into PageRequest for limit data
        //PageRequest request = new PageRequest(0, 1, new Sort("created", Directions.DESC));

        //Can add "^" + to force search only the names starting with
        return mongoTemplate.find(
            query(new Criteria()
                .orOperator(
                    where("FN").regex(name, "i"),
                    where("LN").regex(name, "i")
                )
            ),
            UserProfileEntity.class, TABLE
        );
    }

    /**
     * Find any user matching with email; ignore active or not active.
     *
     * @param mail
     * @return
     */
    @Override
    public UserProfileEntity findOneByMail(String mail) {
        return mongoTemplate.findOne(query(where("EM").is(mail)), UserProfileEntity.class, TABLE);
    }

    @Override
    public UserProfileEntity findOneByPhone(String phone) {
        String changedPhone = Formatter.phoneCleanup(phone);
        if (phone.length() != changedPhone.length()) {
            //TODO make sure everything is filtered and clean before sending phone here. Remove all other than numbers.
            LOG.warn("Found phone number {} starting with + and now changedPhone={}", phone, changedPhone);
        }
        return mongoTemplate.findOne(query(where("PH").is(changedPhone)), UserProfileEntity.class, TABLE);
    }

    @Override
    public UserProfileEntity getProfileUpdateSince(String qid, Date since) {
        return mongoTemplate.findOne(
            query(where("QID").is(qid).and("U").gte(since)),
            UserProfileEntity.class,
            TABLE
        );
    }

    @Override
    public void updateCountryShortName(String countryShortName, String qid) {
        Assert.isTrue(countryShortName.equals(countryShortName.toUpperCase()), "Country short name has to be upper case " + countryShortName);

        mongoTemplate.updateFirst(
            query(where("QID").is(qid)),
            entityUpdate(update("CS", countryShortName)),
            UserProfileEntity.class,
            TABLE
        );
    }

    @Override
    public UserProfileEntity inviteCodeExists(String inviteCode) {
        Assert.hasLength(inviteCode, "Invite code cannot be empty");
        return mongoTemplate.findOne(
            query(where("IC").is(inviteCode)),
            UserProfileEntity.class,
            TABLE
        );
    }

    @Override
    public List<UserProfileEntity> findDependentProfilesByPhone(String phone) {
        return mongoTemplate.find(
            query(where("GP").is(phone)),
            UserProfileEntity.class,
            TABLE
        );
    }

    @Override
    public Set<String> findDependentQIDByPhone(String phone) {
        Query query = query(where("GP").is(phone));
        query.fields().include("QID");
        List<UserProfileEntity> userProfileEntities = mongoTemplate.find(query, UserProfileEntity.class, TABLE);

        Set<String> dependentsQID = new HashSet<>();
        for (UserProfileEntity userProfile : userProfileEntities) {
            dependentsQID.add(userProfile.getQueueUserId());
        }
        return dependentsQID;
    }

    @Override
    public long countDependentProfilesByPhone(String phone) {
        return mongoTemplate.count(
            query(where("GP").is(phone)),
            UserProfileEntity.class,
            TABLE
        );
    }

    @Override
    public void addUserProfileImage(String qid, String profileImage) {
        mongoTemplate.updateFirst(
            query(where("QID").is(qid)),
            entityUpdate(update("PI", profileImage)),
            UserProfileEntity.class,
            TABLE);
    }

    @Override
    public void unsetUserProfileImage(String qid) {
        mongoTemplate.updateFirst(
            query(where("QID").is(qid)),
            entityUpdate(new Update().unset("PI")),
            UserProfileEntity.class,
            TABLE);
    }

    @Override
    public boolean updateDependentDetailsOnPhoneMigration(String qid, String newPhone, String countryShortName, String timeZone) {
        UpdateResult updateResult = mongoTemplate.updateFirst(
            query(where("QID").is(qid).and("PH").is(qid)),
            entityUpdate(update("GP", newPhone).set("CS", countryShortName).set("TZ", timeZone)),
            UserProfileEntity.class,
            TABLE);

        return updateResult.getModifiedCount() == 1;
    }

    @Override
    public void unsetMailOTP(String id) {
        Assert.hasText(id, "Id is empty");
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(new Update().unset("MO")),
            UserProfileEntity.class,
            TABLE);
    }

    @Override
    public List<UserProfileEntity> findAll() {
        return mongoTemplate.findAll(UserProfileEntity.class);
    }

    @Override
    public Stream<UserProfileEntity> findAllPhoneOwners() {
        return mongoTemplate.find(
            query(where("GP").exists(false)),
            UserProfileEntity.class,
            TABLE
        ).stream();
    }

    @Override
    public boolean dependentExists(String qid, String guardianPhone) {
        return mongoTemplate.exists(
            query(where("QID").is(qid).and("GP").is(guardianPhone)),
            UserProfileEntity.class,
            TABLE
        );
    }

    @Override
    public void updateName(String firstName, String lastName, String qid) {
        mongoTemplate.updateFirst(
            query(where("QID").is(qid)),
            update("FN", firstName).set("LN", lastName),
            UserProfileEntity.class,
            TABLE
        );
    }

    @Override
    public void changeUserLevel(String qid, UserLevelEnum userLevel) {
        mongoTemplate.updateFirst(
            query(where("QID").is(qid)),
            update("UL", userLevel),
            UserProfileEntity.class,
            TABLE
        );
    }
}
