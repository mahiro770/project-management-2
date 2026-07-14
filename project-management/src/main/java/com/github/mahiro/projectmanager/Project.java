package com.github.mahiro.projectmanager;

import java.sql.Timestamp;

//プライベートフィールド(カプセル化)
public class Project {
    private Integer id;
    private String title;
    private String clientName;
    private String requiredSkills;
    private String location;
    private Integer priceMin;
    private Integer priceMax;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean applied;
    private String category;


//コンストラクタ
public  Project(Integer id,String title, String clientName, String requiredSkills, String location, Integer priceMin, Integer priceMax, String status,Timestamp createdAt,Timestamp updatedAt, boolean applied, String category){
    this.id = id;
    this.title = title;
    this.clientName = clientName;
    this.requiredSkills = requiredSkills;
    this.location = location;
    this.priceMin = priceMin;
    this.priceMax = priceMax;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.applied = applied;
    this.category = category;
}

//ゲッター
public Integer getId(){return this.id;}
public String getTitle(){return this.title;}
public String getClientName(){return this.clientName;}
public String getRequiredSkills(){return this.requiredSkills;}
public String getLocation(){return this.location;}
public Integer getPriceMin(){return this.priceMin;}
public Integer getPriceMax(){return this.priceMax;}
public String getStatus(){return this.status;}
public Timestamp getCreatedAt(){return this.createdAt;}
public Timestamp getUpdatedAt(){return this.updatedAt;}
public boolean isApplied(){return this.applied;}
public String getCategory(){return this.category;}

//セッター
public void setTitle(String title){this.title = title;}
public void setClientName(String clientName){this.clientName = clientName;}
public void setRequiredSkills(String requiredSkills){this.requiredSkills = requiredSkills;}
public void setLocation(String location){this.location = location;}
public void setPriceMin(Integer priceMin){this.priceMin = priceMin;}
public void setPriceMax(Integer priceMax){this.priceMax = priceMax;}
public void setStatus(String status){this.status = status;}
public void setUpdatedAt(Timestamp updatedAt){this.updatedAt = updatedAt;}
public void setApplied(boolean applied){this.applied = applied;}
public void setCategory(String category){this.category = category;}


@Override
public String toString(){
    return "Id:" + id
                + " | 案件名:" + title
                + " | 会社名:" + clientName
                + " | 必須スキル:" + requiredSkills
                + " | 勤務地:" + location
                + " | 最低金額-最高金額:" + priceMin + " - " + priceMax
                + " | 配属状況:" + status
                + " | 取得日時:" + createdAt
                + " | 更新日時:" + updatedAt;
    }

}


