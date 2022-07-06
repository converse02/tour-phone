package li.dic.tourphone.domain.auth;

import androidx.annotation.Keep;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Partner implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("ORGN")
    @Expose
    private Object orgn;
    @SerializedName("INN")
    @Expose
    private Integer inn;
    @SerializedName("excursion")
    @Expose
    private Integer excursion;
    @SerializedName("user")
    @Expose
    private Integer user;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("credit_limit")
    @Expose
    private String creditLimit;
    @SerializedName("credit_limit_val")
    @Expose
    private Integer creditLimitVal;
    @SerializedName("balance")
    @Expose
    private String balance;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    private final static long serialVersionUID = 1603903345645948704L;

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

    public Object getOrgn() {
        return orgn;
    }

    public void setOrgn(Object orgn) {
        this.orgn = orgn;
    }

    public Integer getInn() {
        return inn;
    }

    public void setInn(Integer inn) {
        this.inn = inn;
    }

    public Integer getExcursion() {
        return excursion;
    }

    public void setExcursion(Integer excursion) {
        this.excursion = excursion;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Integer getCreditLimitVal() {
        return creditLimitVal;
    }

    public void setCreditLimitVal(Integer creditLimitVal) {
        this.creditLimitVal = creditLimitVal;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
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