package com.solrup.evtgen;

import java.util.logging.Logger;
import org.apache.commons.configuration2.builder.fluent.Configurations;

public interface Environment<C> {
    C context();
    Logger logger();
    default Configurations configurations() {
        return new Configurations();
    }
}
