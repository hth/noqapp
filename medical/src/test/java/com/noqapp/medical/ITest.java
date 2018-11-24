package com.noqapp.medical;

import com.noqapp.medical.repository.MasterLabManager;
import com.noqapp.medical.repository.MasterLabManagerImpl;

import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

/**
 * hitender
 * 11/18/18 1:55 PM
 */
public class ITest extends RealMongoForITest {

    protected MasterLabManager masterLabManager;

    @BeforeAll
    public void globalISetup() throws IOException {
        MockitoAnnotations.initMocks(this);

        masterLabManager = new MasterLabManagerImpl(getMongoTemplate());
    }
}
