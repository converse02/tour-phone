package li.dic.tourphone.domain.auth;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Excursion implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("guide")
    @Expose
    private Guide guide;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("ListenersPlaned")
    @Expose
    private Integer listenersPlaned;
    @SerializedName("Start")
    @Expose
    private String start;
    @SerializedName("SosContact")
    @Expose
    private String sosContact;
    @SerializedName("ListenersFact")
    @Expose
    private Integer listenersFact;
    @SerializedName("partner")
    @Expose
    private Object partner;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    private final static long serialVersionUID = 7634399468160890263L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Guide getGuide() {
        return guide;
    }

    public void setGuide(Guide guide) {
        this.guide = guide;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getListenersPlaned() {
        return listenersPlaned;
    }

    public void setListenersPlaned(Integer listenersPlaned) {
        this.listenersPlaned = listenersPlaned;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getSosContact() {
        return sosContact;
    }

    public void setSosContact(String sosContact) {
        this.sosContact = sosContact;
    }

    public Integer getListenersFact() {
        return listenersFact;
    }

    public void setListenersFact(Integer listenersFact) {
        this.listenersFact = listenersFact;
    }

    public Object getPartner() {
        return partner;
    }

    public void setPartner(Object partner) {
        this.partner = partner;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}