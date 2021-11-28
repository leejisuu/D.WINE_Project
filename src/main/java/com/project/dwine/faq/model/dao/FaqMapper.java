package com.project.dwine.faq.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.dwine.faq.model.vo.Faq;

@Mapper
public interface FaqMapper {

	List<Faq> selectFaqList();

	int deleteFaq(int faqNo);

}