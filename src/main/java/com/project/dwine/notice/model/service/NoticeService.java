package com.project.dwine.notice.model.service;

import java.util.List;

import com.project.dwine.notice.model.vo.Notice;
import com.project.dwine.product.model.vo.Product;

public interface NoticeService {
	
	List<Notice> selectNoticeList();
	
	Notice selectNoticeByNo(int notice_no);

	int registNewNotice(Notice notice);
	
	int modifyNotice(Notice notice);
	
	void deleteNotice(String notice_no);




	

}
