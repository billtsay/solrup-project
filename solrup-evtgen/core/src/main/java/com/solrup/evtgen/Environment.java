package com.solrup.evtgen;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.google.common.collect.ImmutableMap;

public interface Environment<C> {
    Map<String, String> EMPTY_CONF = ImmutableMap.copyOf(new HashMap<String, String>());

    C context();
    Logger logger();

    default Map<String, String> configuration() {
        return EMPTY_CONF;
    }
}
