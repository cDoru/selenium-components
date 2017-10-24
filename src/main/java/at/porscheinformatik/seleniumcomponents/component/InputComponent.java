package at.porscheinformatik.seleniumcomponents.component;

import org.openqa.selenium.WebElement;

import at.porscheinformatik.seleniumcomponents.SeleniumComponent;
import at.porscheinformatik.seleniumcomponents.WebElementSelector;

/**
 * An input field.
 *
 * @author cet
 */
public class InputComponent extends HtmlComponent
{

    public static InputComponent byName(SeleniumComponent parent, String name)
    {
        return new InputComponent(parent, WebElementSelector.selectByName(name));
    }

    public static InputComponent bySeleniumKey(SeleniumComponent parent, String seleniumKey)
    {
        return new InputComponent(parent, WebElementSelector.selectBySeleniumKey(seleniumKey));
    }

    public InputComponent(SeleniumComponent parent)
    {
        this(parent, WebElementSelector.selectByTagName("input"));
    }

    public InputComponent(SeleniumComponent parent, WebElementSelector selector)
    {
        super(parent, selector);
    }

    /**
     * Clears the input field an types all specified values.
     * 
     * @param values one or more values to enter
     */
    public void enter(CharSequence... values)
    {
        WebElement element = element();

        element.clear();

        for (CharSequence value : values)
        {
            if (value != null)
            {
                element.sendKeys(value);
            }
        }
    }

    public String getValue()
    {
        return getAttribute("value");
    }

}
