/**
 *
 */
package at.porscheinformatik.seleniumcomponents;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 * A selector as used by {@link SeleniumComponent}s.
 *
 * @author Daniel Furtlehner
 */
public interface WebElementSelector
{

    /**
     * Creates a selector that always uses the specified element. This selector ignores the search context.
     *
     * @param element the element to return
     * @return the selector
     */
    static WebElementSelector selectElement(WebElement element)
    {
        return new WebElementSelector()
        {
            @Override
            public WebElement find(SearchContext context)
            {
                return element;
            }

            @Override
            public List<WebElement> findAll(SearchContext context)
            {
                return Arrays.asList(element);
            }

            @Override
            public String toString()
            {
                return element.toString();
            }
        };
    }

    /**
     * This is a shortcut method to use a Selenium selector as selector.
     *
     * @param by the Selenium selector
     * @return the {@link WebElementSelector} for the given Selenium selector
     */
    static WebElementSelector selectBy(By by)
    {
        return new WebElementSelector()
        {
            @Override
            public WebElement find(SearchContext context)
            {
                return context.findElement(by);
            }

            @Override
            public List<WebElement> findAll(SearchContext context)
            {
                return context.findElements(by);
            }

            @Override
            public String toString()
            {
                return by.toString();
            }
        };
    }

    /**
     * A selector that uses the id of an element. This selector usually does not ignore the hierarchy of components.
     *
     * @param id the id of the element
     * @return the selector
     */
    static WebElementSelector selectById(String id)
    {
        return selectBy(By.id(id));
    }

    /**
     * A selector that uses the value of the "name" attribute of an element. This selector respects the hierarchy of
     * components.
     *
     * @param name the expected value of the "name" attribute of the element
     * @return the selector
     */
    static WebElementSelector selectByName(String name)
    {
        return selectBy(By.name(name));
    }

    /**
     * A selector that uses the tag name of an element. This selector respects the hierarchy of components.
     *
     * @param tagName the tag name of the element
     * @return the selector
     */
    static WebElementSelector selectByTagName(String tagName)
    {
        return selectBy(By.tagName(tagName));
    }

    /**
     * A selector that uses the value of the "class" attribute of an element. If the "class" attribute contains multiple
     * classes, the selector will test each. This selector respects the hierarchy of components.
     *
     * @param className the tag class of the element
     * @return the selector
     */
    static WebElementSelector selectByClassName(String className)
    {
        return selectBy(By.className(className));
    }

    /**
     * A selector that uses a CSS selector query. This selector respects the hierarchy of components.
     *
     * @param css the CSS selector query
     * @return the selector
     */
    static WebElementSelector selectByCss(String css)
    {
        return selectBy(By.cssSelector(css));
    }

    /**
     * A selector that uses the value of the "selenium-key" attribute of an element. This selector respects the
     * hierarchy of components. The implementation is bases on a CSS selector query.
     *
     * @param key the expected value of the "selenium-key" attribute of the element
     * @return the selector
     */
    static WebElementSelector selectBySeleniumKey(String key)
    {
        return selectBySeleniumKey("*", key);
    }

    /**
     * A selector that uses a tagName and the value of the "selenium-key" attribute of an element. This selector
     * respects the hierarchy of components. The implementation is bases on a CSS selector query.
     *
     * @param tagName the tag name of the element
     * @param key the expected value of the "selenium-key" attribute of the element
     * @return the selector
     */
    static WebElementSelector selectBySeleniumKey(String tagName, String key)
    {
        return selectBy(By.cssSelector(String.format("%s[selenium-key='%s']", tagName, key)));
    }

    /**
     * A selector that uses an XPath query. If this selector starts with a "/", it will ignore the hierarchy of
     * components.
     *
     * @param xpath the XPath query
     * @return the selector
     */
    static WebElementSelector selectByXPath(String xpath)
    {
        return selectBy(By.xpath(xpath));
    }

    /**
     * Returns the element that represents the specified index or column. Look a all direct children of the search
     * context. If the element has a "colspan" attribute it is assumed, that the element spans over multiple indices.
     *
     * @param index the index
     * @return the selector
     */
    static WebElementSelector selectByIndex(int index)
    {
        return new WebElementSelector()
        {
            @Override
            public WebElement find(SearchContext context)
            {
                List<WebElement> elements = context.findElements(By.cssSelector("*"));
                int currentIndex = 0;

                for (WebElement element : elements)
                {
                    currentIndex += getColspan(element);

                    if (currentIndex > index)
                    {
                        return element;
                    }
                }

                return null;
            }

            @Override
            public List<WebElement> findAll(SearchContext context)
            {
                return Arrays.asList(find(context));
            }

            private int getColspan(WebElement element)
            {
                String colspan = element.getAttribute("colspan");

                if (SeleniumUtils.isEmpty(colspan))
                {
                    return 1;
                }

                try
                {
                    return Integer.parseInt(colspan);
                }
                catch (NumberFormatException e)
                {
                    throw new SeleniumException("Failed to parse colspan: " + colspan, e);
                }
            }
        };
    }

    /**
     * A selector that selects the component itself.
     *
     * @return the selector
     */
    static WebElementSelector selectSelf()
    {
        return selectByXPath(".");
    }

    /**
     * Searches for the element.
     *
     * @param context the context to search in
     * @return the element returned by this selector.
     */
    WebElement find(SearchContext context);

    /**
     * Searches for all the elements.
     *
     * @param context the context to search in
     * @return a list of elements, never null
     */
    List<WebElement> findAll(SearchContext context);

    /**
     * Describe the selector to simplify debugging.
     *
     * @return a string representation
     */
    @Override
    String toString();

    /**
     * Chains the specified selector after this selector.
     *
     * @param selector the selector
     * @return the new selector instance
     */
    default WebElementSelector descendant(WebElementSelector selector)
    {
        // I could never imagine a situation for needing the following in Java
        WebElementSelector that = this;

        return new WebElementSelector()
        {
            @Override
            public WebElement find(SearchContext context)
            {
                return selector.find(that.find(context));
            }

            @Override
            public List<WebElement> findAll(SearchContext context)
            {
                return selector.findAll(that.find(context));
            }

            @Override
            public String toString()
            {
                return String.format("%s -> %s", that, selector);
            }
        };
    }

    /**
     * @param xpath the xpath to the parent
     * @return the parent
     * @deprecated I think this is too complex. It should not be used.
     */
    @Deprecated
    default WebElementSelector ancestor(String xpath)
    {
        WebElementSelector parent = selectByXPath("ancestor::" + xpath);
        WebElementSelector that = this;

        return new WebElementSelector()
        {
            @Override
            public WebElement find(SearchContext context)
            {
                return parent.find(that.find(context));
            }

            @Override
            public List<WebElement> findAll(SearchContext context)
            {
                return parent.findAll(that.find(context));
            }

            @Override
            public String toString()
            {
                return String.format("%s -> %s", that, parent);
            }
        };
    }
}
