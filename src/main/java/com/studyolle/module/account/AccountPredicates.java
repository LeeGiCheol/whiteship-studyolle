package com.studyolle.module.account;


import com.studyolle.module.tag.Tag;
import com.studyolle.module.zone.Zone;

import com.querydsl.core.types.Predicate;
import java.util.Set;

public class AccountPredicates {
    
    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
    
}
