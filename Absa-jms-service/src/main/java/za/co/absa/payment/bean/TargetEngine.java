/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.absa.payment.bean;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "targetEngine")
@XmlAccessorType(XmlAccessType.FIELD)
public enum TargetEngine {

    NONE(1, "None"), WHATCHAMAKALIT(2, "Whatchamakalit");

    @XmlElement
    private long id;
    @XmlElement
    private String description;

    private static final Map<Long, TargetEngine> byId = new HashMap<Long, TargetEngine>();

    TargetEngine(long id, String description) {

        this.id = id;
        this.description = description;
    }

    static {
        for (TargetEngine e : TargetEngine.values()) {
            if (byId.put(e.getId(), e) != null) {
                throw new IllegalArgumentException("duplicate id: " + e.getId());
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static TargetEngine getById(Long id) {
        return byId.get(id);
    }

}
