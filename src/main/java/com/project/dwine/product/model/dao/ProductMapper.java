package com.project.dwine.product.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.dwine.product.model.vo.Country;
import com.project.dwine.product.model.vo.Product;
import com.project.dwine.product.model.vo.Type;
import com.project.dwine.product.model.vo.Variety;

@Mapper
public interface ProductMapper {

	List<Product> selectProductList();

	List<Type> selectTypeList();

	List<Country> selectCountryList();

	List<Variety> selectVarietyList();

	Product selectProductByNo(int productNo);

	int registProduct(Product product);

	int selectLastSeqNo();

	int registProductHash(int hashNo);

	int deleteProduct(int productNo);

	int modifyProduct(Product product);

	int modifyProductHash(int productNo, int hashNo, int preHashNo);

	int deleteMultiProduct(int productNo);

}
