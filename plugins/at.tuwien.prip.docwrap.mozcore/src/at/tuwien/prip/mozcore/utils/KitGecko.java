package at.tuwien.prip.mozcore.utils;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class KitGecko extends Browser {

    public KitGecko(Composite parent, int style) {
        super(parent,style);
    }

    private final List<KitGecko> children = new LinkedList<KitGecko>();

    public List<KitGecko> getChildGeckos() {
        return children;
    }

    @Override
    protected void checkSubclass() {
    }

}