package com.project.dwine.manage.model.service;

import java.util.List;

import com.project.dwine.member.model.vo.Member;

public interface MemberMgService {

	List<Member> selectMemberMgList(int startRow, int endRow);

	Member selectMemberMgByNo(int user_no);

	void deleteMemberMg(String user_no);

	int memberMgTotalListCnt();

}
