package com.solrup.commons.feature.itest;

import com.solrup.commons.itest.KarafTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class InstallationTest extends KarafTestSupport {

    @Test
    public void test() throws Exception {
        assertBundleStarted("org.apache.cxf.cxf-core");
        assertBlueprintNamespacePublished("http://cxf.apache.org/blueprint/core", 1000);
        assertBlueprintNamespacePublished("http://cxf.apache.org/configuration/beans", 1000);
        assertBlueprintNamespacePublished("http://cxf.apache.org/configuration/parameterized-types", 1000);
        assertBlueprintNamespacePublished("http://cxf.apache.org/configuration/security", 1000);
        assertBlueprintNamespacePublished("http://schemas.xmlsoap.org/wsdl/", 1000);

        assertBundleStarted("org.apache.cxf.cxf-rt-frontend-jaxws");
        assertBlueprintNamespacePublished("http://cxf.apache.org/blueprint/jaxws", 1000);
        assertBlueprintNamespacePublished("http://cxf.apache.org/blueprint/simple", 1000);
    }

    @Configuration
    public Option[] config() {
        return new Option[]{
                cxfBaseConfig(),

                features(cxfUrl, "aries-blueprint", "cxf-core", "cxf-jaxws"),
                logLevel(LogLevel.INFO)};
    }
}
