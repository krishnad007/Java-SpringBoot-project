package com.proj.ecom_project.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.proj.ecom_project.Model.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

	@Query(" select p from Product p where " +   //It is a  JPQL 
	 "LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%')) OR "+
	 "LOWER(p.description) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
	 "LOWER(p.brand) LIKE LOWER(CONCAT('%',:keyword,'%')) OR "+
	 "LOWER(p.category) LIKE LOWER(CONCAT('%',:keyword,'%')) "
		)
	List<Product> searchproduct(String keyword);
}
