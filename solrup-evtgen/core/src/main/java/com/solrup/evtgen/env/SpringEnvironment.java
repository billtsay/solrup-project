package com.solrup.evtgen.env;

import com.solrup.evtgen.Environment;
import com.solrup.evtgen.core.TemplateReflect;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.springframework.context.ApplicationContext;

import java.util.UUID;
import java.util.logging.Logger;

public class SpringEnvironment implements Environment<ApplicationContext> {
    UUID uuid;
    TemplateReflect template;

    @Override
    public ApplicationContext context() {
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
