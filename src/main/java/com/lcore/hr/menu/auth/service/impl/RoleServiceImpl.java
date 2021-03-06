package com.lcore.hr.menu.auth.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lcore.hr.core.entity.Role;
import com.lcore.hr.core.entity.RoleToUserRel;
import com.lcore.hr.core.entity.Root;
import com.lcore.hr.core.entity.User;
import com.lcore.hr.menu.auth.service.RoleService;
import com.lcore.hr.menu.auth.service.UserService;
import com.lcore.hr.menu.base.service.impl.BaseServiceImpl;

@Service("roleService")
@Transactional
public class RoleServiceImpl extends BaseServiceImpl implements RoleService{

	@Override
	public List<Root> getRoleList(int offset, int limit, String sort,
			String order, String key) {
		String condition = " 1=1 ";
		if (key != null && !"".equals(key)) {
			condition += " and (obj.name like '%" + key + "%' or obj.remark like '%"+key+"%')";
		}
		if (null != sort && !"".equals(sort))
			condition += " order by " + " obj." + sort + "" + "  " + order;
		return this.getPagedObjListWithCondition(Role.class.getSimpleName(),
				condition, offset, limit);
	}

	@Override
	public void addRole(Role role) throws Exception {
		this.save(role);
	}

	@Override
	public void deleteRole(List<String> ids) throws Exception {
		for (String id : ids) {
			if (id != null && !"".equals(id.trim()))
				this.delete(Role.class.getName(), id);
		}
	}

	@Override
	public void updateRole(Role role) throws Exception {
		this.update(role);
	}

	@Override
	public List<String> getUserIdsByRoleId(String roleId) throws Exception {
	    String condition = " 1=1 ";
	    if(roleId == null ||roleId.trim().equals("")){
	    	return null;
	    }
	    condition += " and obj.roleId = '"+roleId+"'";
	    List<Root> list = this.getObjListByCondition(RoleToUserRel.class.getSimpleName(), condition);
	    List<String> resultList = new ArrayList<String>();
	    for(Root root:list){
	    	RoleToUserRel rel = (RoleToUserRel) root;
	    	resultList.add(rel.getUserId());
	    }
		return resultList;
	}

	@Override
	public void updateRoleToUserRel(String roleId, String[] userIds)
			throws Exception {
		//1、删除以前的关系
		deleteUserIdsByRoleId(roleId);
		//2、批量添加关系
		for(String str:userIds){
			RoleToUserRel tmp = new RoleToUserRel();
			tmp.setRoleId(roleId);
			tmp.setUserId(str);
			this.save(tmp);
		}
		
	}

	@Override
	public void deleteUserIdsByRoleId(String roleId) throws Exception {
		List<Root> roots = this.getObjListByCondition(RoleToUserRel.class.getSimpleName(), " obj.roleId ='"+roleId+"'");
		for(Root root:roots){
			this.delete(RoleToUserRel.class.getName(), root.getId());
		}
	}

	
}
