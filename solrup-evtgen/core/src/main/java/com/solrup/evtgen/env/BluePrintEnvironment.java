package com.solrup.evtgen.env;

import com.solrup.evtgen.Environment;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

public class BluePrintEnvironment implements Environment<BundleContext> {

    @Override
    public BundleContext context() {
        return null;
    }

    @Override
    public Logger logger() {
        return null;
    }

    @Override
    public Configurations configurations() {
        return null;
    }
}
