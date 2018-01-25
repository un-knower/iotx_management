package com.anosi.asset.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * java类简单作用描述
 *
 * @ProjectName: iotx_management
 * @Package: com.anosi.asset.model.mongo
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/1/25 14:04
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/1/25 14:04
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Document
public class AllData extends AbstractDocument {

    private String val;

    private Date collectTime;

    public AllData() {
    }

    public AllData(String val, Date collectTime) {
        this.val = val;
        this.collectTime = collectTime;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }
}
