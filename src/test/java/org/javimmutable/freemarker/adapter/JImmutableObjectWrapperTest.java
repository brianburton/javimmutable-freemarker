///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.freemarker.adapter;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.util.JImmutables;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.javimmutable.collections.util.JImmutables.*;

public class JImmutableObjectWrapperTest
    extends TestCase
{
    private final JImmutableMap<String, Object> empty = JImmutables.map();

    private Configuration config;
    private StringTemplateLoader templates;
    private Map<String, Object> model;


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        templates = new StringTemplateLoader();
        config.setTemplateLoader(templates);
        config.setObjectWrapper(new JImmutableObjectWrapper(config.getIncompatibleImprovements()));
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        config.setWrapUncheckedExceptions(true);
        model = new HashMap<>();
    }

    public void testList()
        throws Exception
    {
        model.put("list", list(1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals("8", render("${list?size}"));
        assertEquals("1,3,5", render("${list[0]},${list[2]},${list[4]}"));
        assertEquals("1|2|3|4|5|6|7|8|", render("<#list list as z>${z}|</#list>"));
    }

    public void testSet()
        throws Exception
    {
        model.put("set", JImmutables.insertOrderSet(1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals("1|2|3|4|5|6|7|8|", render("<#list set as z>${z}|</#list>"));
    }

    public void testMap()
        throws Exception
    {
        model.put("e", empty);
        model.put("m", empty.assign("x", "A").assign("y", "B").assign("z", "C"));
        assertEquals("C|A", render("${m['z']}|${m.x}"));

        assertEquals("C|A", render("${z}|${x}", empty.assign("x", "A").assign("y", "B").assign("z", "C")));
    }

    public void testMixed()
        throws Exception
    {
        assertEquals("2,45",
                     render("${a.ll[1]},<#list a.s as x>${x}</#list>",
                            empty.assign("a", empty.assign("ll", list(1, 2, 3)).assign("s", sortedSet(4, 5)))));
    }

    private String render(String template,
                          Object model)
        throws Exception
    {
        templates.putTemplate(template, template);
        StringWriter out = new StringWriter();
        config.getTemplate(template).process(model, out);
        return out.toString();
    }

    private String render(String template)
        throws Exception
    {
        return render(template, model);
    }
}
