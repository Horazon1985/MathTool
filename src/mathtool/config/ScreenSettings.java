package mathtool.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ScreenSettings {

    private int minWidth;
    private int minHeight;

    /**
     * @return the minWidth
     */
    public int getMinWidth() {
        return minWidth;
    }

    /**
     * @param minWidth the minWidth to set
     */
    @XmlElement
    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    /**
     * @return the minHeight
     */
    public int getMinHeight() {
        return minHeight;
    }

    /**
     * @param minHeight the minHeight to set
     */
    @XmlElement
    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }
    
}
