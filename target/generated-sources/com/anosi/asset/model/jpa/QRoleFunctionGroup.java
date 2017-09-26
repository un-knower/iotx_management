package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoleFunctionGroup is a Querydsl query type for RoleFunctionGroup
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QRoleFunctionGroup extends EntityPathBase<RoleFunctionGroup> {

    private static final long serialVersionUID = -1462970856L;

    public static final QRoleFunctionGroup roleFunctionGroup = new QRoleFunctionGroup("roleFunctionGroup");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<Account, QAccount> accountList = this.<Account, QAccount>createList("accountList", Account.class, QAccount.class, PathInits.DIRECT2);

    //inherited
    public final StringPath createBy = _super.createBy;

    //inherited
    public final DateTimePath<java.util.Date> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.util.Date> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath name = createString("name");

    public final ListPath<RoleFunctionBtn, QRoleFunctionBtn> roleFunctionBtnList = this.<RoleFunctionBtn, QRoleFunctionBtn>createList("roleFunctionBtnList", RoleFunctionBtn.class, QRoleFunctionBtn.class, PathInits.DIRECT2);

    public final ListPath<RoleFunction, QRoleFunction> roleFunctionList = this.<RoleFunction, QRoleFunction>createList("roleFunctionList", RoleFunction.class, QRoleFunction.class, PathInits.DIRECT2);

    public QRoleFunctionGroup(String variable) {
        super(RoleFunctionGroup.class, forVariable(variable));
    }

    public QRoleFunctionGroup(Path<? extends RoleFunctionGroup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoleFunctionGroup(PathMetadata metadata) {
        super(RoleFunctionGroup.class, metadata);
    }

}

