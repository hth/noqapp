package com.noqapp.search.elastic.repository;

import java.util.List;

/**
 * hitender
 * 5/30/21 9:18 PM
 */
public interface IncidentEventElasticManager<IncidentEventElastic> {
    /** Save single object. */
    void save(IncidentEventElastic incidentEventElastic);

    /** Bulk save operation. */
    void save(List<IncidentEventElastic> incidentEventElastics);
}
