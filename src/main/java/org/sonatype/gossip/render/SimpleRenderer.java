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

package org.sonatype.gossip.render;

import org.sonatype.gossip.Event;

/**
 * A simple event renderer.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 1.0
 */
public class SimpleRenderer
    implements Renderer
{
    private boolean includeName = false;

    private boolean shortName = false;

    private int nameWidth = -1;

    public SimpleRenderer() {}
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "includeName=" + includeName +
                ", shortName=" + shortName +
                ", nameWidth=" + nameWidth +
                '}';
    }

    public void setIncludeName(final boolean flag) {
        this.includeName = flag;
    }

    public void setIncludeName(final String flag) {
        setIncludeName(Boolean.valueOf(flag).booleanValue());
    }

    public boolean isIncludeName() {
        return includeName;
    }

    public void setShortName(final boolean flag) {
        this.shortName = flag;
    }

    public void setShortName(final String flag) {
        setShortName(Boolean.valueOf(flag));
    }

    public boolean isShortName() {
        return shortName;
    }

    public void setNameWidth(final int width) {
        this.nameWidth = width;
    }

    public void setNameWidth(final String width) {
        setNameWidth(Integer.parseInt(width));
    }

    public int getNameWidth() {
        return nameWidth;
    }

    public String render(final Event event) {
        assert event != null;

        StringBuilder buff = new StringBuilder();

        buff.append("[");
        appendLevel(event, buff);
        buff.append("]");

        switch (event.level) {
            case INFO:
            case WARN:
                buff.append(" ");
        }

        buff.append(" ");

        if (isIncludeName()) {
            appendLogger(event, buff);
            buff.append("- ");
        }

        appendMessage(event, buff);

        buff.append(NEWLINE);

        if (event.cause != null) {
            appendCause(event, buff);
        }

        return buff.toString();
    }

    protected void appendLevel(final Event event, final StringBuilder buff) {
        assert event != null;
        assert buff != null;

        buff.append(event.level.name());
    }

    protected void appendLogger(final Event event, final StringBuilder buff) {
        assert event != null;
        assert buff != null;

        String name = event.logger.getName();

        if (isShortName()) {
            int i = name.lastIndexOf(".");

            if (i != -1) {
                name = name.substring(i + 1, name.length());
            }
        }

        int w = getNameWidth();
        if (w > 0) {
            name = String.format("%-" + w + "s", name);
        }

        buff.append(name);
    }

    protected void appendMessage(final Event event, final StringBuilder buff) {
        assert event != null;
        assert buff != null;

        buff.append(event.message);
    }

    protected void appendCause(final Event event, final StringBuilder buff) {
        assert event != null;
        assert buff != null;

        Throwable cause = event.cause;

        buff.append(cause);
        buff.append(NEWLINE);

        while (cause != null) {
            for (StackTraceElement e : cause.getStackTrace()) {
                buff.append("    at ").append(e.getClassName()).append(".").append(e.getMethodName());
                buff.append(" (").append(getLocation(e)).append(")");
                buff.append(NEWLINE);
            }

            cause = cause.getCause();
            if (cause != null) {
                buff.append("Caused by ").append(cause.getClass().getName()).append(": ");
                buff.append(cause.getMessage());
                buff.append(NEWLINE);
            }
        }
    }

    protected String getLocation(final StackTraceElement e) {
        assert e != null;

        if (e.isNativeMethod()) {
            return "Native Method";
        }
        else if (e.getFileName() == null) {
            return "Unknown Source";
        }
        else if (e.getLineNumber() >= 0) {
            return String.format("%s:%s", e.getFileName(), e.getLineNumber());
        }
        else {
            return e.getFileName();
        }
    }
}