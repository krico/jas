package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.util.BeanUtil;

import java.util.List;

/**
 * @author krico
 * @since 17/05/15.
 */
public class JasItemDetails extends ShoppingCart.Item {
    private List<Key> subItemIds;
    private List<String> subItems;
    private List<String> subItemTypes;

    public JasItemDetails(ShoppingCart.Item item) {
        BeanUtil.copyProperties(this, item);
    }

    public JasItemDetails(ShoppingCart.Item item, List<Key> subItemIds, List<String> subItems, List<String> subItemTypes) {
        this(item);
        this.subItemIds = subItemIds;
        this.subItems = subItems;
        this.subItemTypes = subItemTypes;
    }

    public List<Key> getSubItemIds() {
        return subItemIds;
    }

    public void setSubItemIds(List<Key> subItemIds) {
        this.subItemIds = subItemIds;
    }

    public List<String> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<String> subItems) {
        this.subItems = subItems;
    }

    public List<String> getSubItemTypes() {
        return subItemTypes;
    }

    public void setSubItemTypes(List<String> subItemTypes) {
        this.subItemTypes = subItemTypes;
    }
}
