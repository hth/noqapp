package com.noqapp.view.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * User: hitender
 * Date: 11/18/16 2:53 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class NoQueueServletContextListener implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(NoQueueServletContextListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        //TODO make clean shutdown for quartz. This prevent now from tomcat shutdown
        deregisterJDBCDriver();
        LOG.info("NoQApp context destroyed");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        LOG.info("NoQueue context initialized");
    }

    /**
     * This manually de-registers JDBC driver, which prevents Tomcat 7 from complaining about
     * memory leaks with respect to this class.
     */
    private void deregisterJDBCDriver() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                LOG.info("De-registering jdbc driver={}", driver);
            } catch (SQLException e) {
                LOG.error("Error de-registering driver={} reason={}", driver, e.getLocalizedMessage(), e);
            }
        }
    }
}
