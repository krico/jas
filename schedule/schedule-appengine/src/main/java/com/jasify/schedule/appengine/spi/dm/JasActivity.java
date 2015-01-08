package com.jasify.schedule.appengine.spi.dm;

import java.util.Date;

/**
 * @author krico
 * @since 07/01/15.
 */
public class JasActivity implements JasEndpointEntity {
    private String id;

    private Date start;

    private Date finish;

    private Double price;

    private JasActivityType activityType;

    private String currency;

    private String location;

    private int maxSubscriptions;

    private int subscriptionCount;

    private String description;

    private String bookItUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public JasActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(JasActivityType activityType) {
        this.activityType = activityType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxSubscriptions() {
        return maxSubscriptions;
    }

    public void setMaxSubscriptions(int maxSubscriptions) {
        this.maxSubscriptions = maxSubscriptions;
    }

    public int getSubscriptionCount() {
        return subscriptionCount;
    }

    public void setSubscriptionCount(int subscriptionCount) {
        this.subscriptionCount = subscriptionCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookItUrl() {
        return bookItUrl;
    }

    public void setBookItUrl(String bookItUrl) {
        this.bookItUrl = bookItUrl;
    }
}
