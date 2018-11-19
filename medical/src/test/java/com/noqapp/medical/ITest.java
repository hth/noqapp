package com.noqapp.medical;

import com.noqapp.medical.repository.MasterPathologyManager;
import com.noqapp.medical.repository.MasterPathologyManagerImpl;
import com.noqapp.medical.repository.MasterRadiologyManager;
import com.noqapp.medical.repository.MasterRadiologyManagerImpl;

import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

/**
 * hitender
 * 11/18/18 1:55 PM
 */
public class ITest extends RealMongoForITest {

    protected MasterPathologyManager masterPathologyManager;
    protected MasterRadiologyManager masterRadiologyManager;

    @BeforeAll
    public void globalISetup() throws IOException {
        MockitoAnnotations.initMocks(this);

        masterPathologyManager = new MasterPathologyManagerImpl(getMongoTemplate());
        masterRadiologyManager = new MasterRadiologyManagerImpl(getMongoTemplate());
    }
}
