package com.solrup.gora.osgi.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class GoraActivator implements BundleActivator {

    public void start(BundleContext ctx) {
        System.out.println("Hello World.");
    }

    public void stop(BundleContext bundleContext) {
        System.out.println("Goodbye World.");
    }

}