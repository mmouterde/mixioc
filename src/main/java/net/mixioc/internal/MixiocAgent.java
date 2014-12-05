package net.mixioc.internal;

import net.mixioc.ServiceManager;
import net.mixioc.annotation.Service;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

public class MixiocAgent {

    static final Logger logger = LoggerFactory.getLogger(MixiocAgent.class);

    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) throws Exception {
        if (instrumentation != null) {
            return;
        }
        logger.info("Looking for injection in package: {}", args);
        instrumentation = inst;
        instrumentation.addTransformer(new MixiocTransformer(args));
    }

}