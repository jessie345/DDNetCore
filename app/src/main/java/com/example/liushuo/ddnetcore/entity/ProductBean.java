package com.example.liushuo.ddnetcore.entity;

/**
 * Created by liushuo on 2017/9/22.
 */

public class ProductBean {
    /**
     * _id : 59b746b3f3075fa55fe6561f
     * name : 宾得全站仪
     * category : 59b7417ff3075fa55fe6556e
     * supplier : 59b73da5f3075fa55fe65515
     * icon : http://zhongcelixing.com.cn/UploadFiles/2013-3/201332210274899214.jpg
     * brief : 非同寻常的的手动型0.5²全站仪之新品，为测绘、工程、建筑和三维工业测量等领域提供超高测量精度的解决方案。
     */

    private String _id;
    private String name;
    private String category;
    private String supplier;
    private String icon;
    private String brief;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
