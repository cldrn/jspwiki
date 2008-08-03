/* Copyright 2005-2006 Tim Fennell
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
package com.ecyrd.jspwiki.action;

import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.providers.ProviderException;

/**
 * Stripes type converter that converts a WikiPage name, expressed as a String, into an
 * {@link com.ecyrd.jspwiki.WikiPage} object. This converter is looked up
 * and returned by {@link WikiTypeConverterFactory} for HTTP request parameters
 * that need to be bound to ActionBean properties of type WikiPage. Stripes
 * executes this TypeConverter during the
 * {@link net.sourceforge.stripes.controller.LifecycleStage#BindingAndValidation}
 * stage of request processing.
 * 
 * @author Andrew Jaquith
 */
public class WikiPageTypeConverter implements TypeConverter<WikiPage>
{
    /**
     * Converts a named wiki page into a valid WikiPage object by retrieving it
     * via the WikiEngine. If the exact page is not found, plural variations
     * will be tried. If the page cannot be found (perhaps because it does not
     * exist), this method will add a validation error to the supplied
     * Collection of errors and return <code>null</code>. The error will be
     * of type {@link net.sourceforge.stripes.validation.LocalizableError} and
     * will have a message key of <code>pageNotFound</code> and a single
     * parameter (equal to the value passed for <code>pageName</code>).
     * 
     * @param pageName
     *            the name of the WikiPage to retrieve
     * @param targetType
     *            the type to return, which will always be of type
     *            {@link com.ecyrd.jspwiki.WikiPage}
     * @param errors
     *            the current Collection of validation errors for this field
     * @return the
     */
    public WikiPage convert(String pageName, Class<? extends WikiPage> targetType, Collection<ValidationError> errors)
    {
        WikiRuntimeConfiguration config = (WikiRuntimeConfiguration)StripesFilter.getConfiguration();
        WikiEngine engine = config.getEngine();
        WikiPage page = engine.getPage(pageName);
        if (page == null)
        {
            try
            {
                String finalName = engine.getWikiActionBeanFactory().getFinalPageName(pageName);
                if (finalName == null || engine.getPage(finalName) == null)
                {
                    errors.add(new LocalizableError("pageNotFound", pageName));
                }
            }
            catch (ProviderException e)
            {
                errors.add(new SimpleError(e.getMessage()));
            }
        }
        return page;
    }

    public void setLocale(Locale locale)
    {
    }
}
