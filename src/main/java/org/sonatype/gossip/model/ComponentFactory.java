/*
 * Copyright (C) 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonatype.gossip.model;

import org.sonatype.gossip.Log;
import org.sonatype.gossip.filter.Filter;
import org.sonatype.gossip.model.io.props.ConfigurationContext;
import org.sonatype.gossip.model.io.props.ConfigurationContextConfigurator;
import org.sonatype.gossip.source.Source;
import org.sonatype.gossip.trigger.Trigger;

/**
 * Constructs components from model nodes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 1.0
 */
public class ComponentFactory
{
    private static Log log = Log.getLogger(ComponentFactory.class);

    public static Source create(final SourceNode node) throws Exception {
        return (Source) build(node);
    }

    public static Trigger create(final TriggerNode node) throws Exception {
        return (Trigger) build(node);
    }

    public static Filter create(final FilterNode node) throws Exception {
        return (Filter) build(node);
    }

    //
    // Helpers
    //

    private static Class loadClass(final String className) throws ClassNotFoundException {
        assert className != null;

        Class type = Thread.currentThread().getContextClassLoader().loadClass(className);

        log.trace("Loaded class: {}", type);

        return type;
    }

    private static Object build(final FactoryNode node) throws Exception {
        assert node != null;

        return build(node.getType(), node.getConfiguration());
    }

    public static Object build(final String className, final Object config) throws Exception {
        assert className != null;

        Class type = loadClass(className);
        Object obj = type.newInstance();

        if (config != null) {
            //
            // TODO: Support the Xpp3 configuration... w/o requiring it on the classpath for this class to function
            //

            if (config instanceof ConfigurationContext) {
                new ConfigurationContextConfigurator().configure(obj, (ConfigurationContext)config);
            }
            else {
                log.error("Unsupported configuration type: " + config.getClass().getName());
            }
        }

        log.trace("Created: {}", obj);

        return obj;
    }
}