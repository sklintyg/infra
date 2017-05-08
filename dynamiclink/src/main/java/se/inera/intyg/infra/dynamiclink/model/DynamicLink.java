package se.inera.intyg.infra.dynamiclink.model;

/**
 * Created by eriklupander on 2017-05-03.
 */
public class DynamicLink {

    private String key;

    private String url;

    private String text;

    private String tooltip;

    private String target = null;

    public DynamicLink() {
    }

    public DynamicLink(String key, String url, String text, String tooltip, String target) {
        this.key = key;
        this.url = url;
        this.text = text;
        this.tooltip = tooltip;
        this.target = target;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DynamicLink)) {
            return false;
        }

        DynamicLink that = (DynamicLink) o;

        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
