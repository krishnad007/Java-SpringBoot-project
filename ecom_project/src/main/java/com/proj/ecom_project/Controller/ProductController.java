package com.proj.ecom_project.Controller;

import java.io.IOException;

import java.util.List;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;

import com.paypal.base.rest.PayPalRESTException;
import com.proj.ecom_project.Model.Product;
import com.proj.ecom_project.Service.ProductService;

import lombok.extern.slf4j.Slf4j;

//@RestController
@Controller
@CrossOrigin
@Slf4j
//@RequestMapping("/api")
public class ProductController {
    
	@Autowired
	private ProductService service;
	private Log log;
	
	
	@GetMapping("/products")
	public ResponseEntity<List<Product>> getallproducts(){
		return new ResponseEntity<>(service.getAllproducts(),HttpStatus.OK);
	}
	
	@GetMapping("/product/{id}")
	public ResponseEntity<Product> getproduct(@PathVariable int id) {
		Product prod= service.getbyid(id);
        if(prod !=null) {
        	return new ResponseEntity<>(prod,HttpStatus.OK);
        }else {
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
		
	}
	
	@PostMapping("/product" )
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<?> addproduct(@RequestPart Product product, @RequestPart MultipartFile image){ //instead of requestbody I use reqpart toget two arguments
		
		try {
		Product prod=service.addproduct(product,image);
		
		return new ResponseEntity<>(prod,HttpStatus.CREATED);
		}catch(Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/product/{id}/image")
	public ResponseEntity<byte[]> getimgbyid(@PathVariable int id){
		
		Product prod=service.getbyid(id);
		  if (prod == null) {
		        return ResponseEntity.notFound().build();
		    }
		byte[] imgfil= prod.getImageData();
		return ResponseEntity.ok().contentType(MediaType.valueOf(prod.getImageType())).body(imgfil);
	}
	
	@PutMapping("/product/{id}")
	public ResponseEntity<String> updateprodct(@PathVariable int id,@RequestPart Product product, @RequestPart MultipartFile image){
		Product prod=null;
		try {
			prod = service.updateproduct(id,product,image );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(prod!=null) {
			return new ResponseEntity<>(" updated ",HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Product Not found",HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("/product/{id}")
	public ResponseEntity<String> deletprodct(@PathVariable int id){
		Product prod=service.getbyid(id);
		if(prod!=null) {
			service.deleted(id);
		return new ResponseEntity<>("Deleted",HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Not found",HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/products/search")
	public ResponseEntity<List<Product>> searchprod(@RequestParam String keyword){
		List<Product> prod= service.searchprodct(keyword);
		return new ResponseEntity<>(prod,HttpStatus.OK);
	}
	
	

	    @GetMapping("/")
	    public String home() {
	        return "index";
	    }

	    @PostMapping("/payment/create")
	    public RedirectView createPayment(
	            @RequestParam("method") String method,
	            @RequestParam("amount") String amount,
	            @RequestParam("currency") String currency,
	            @RequestParam("description") String description
	    ) {
	        try {
	            String cancelUrl = "http://localhost:8080/payment/cancel";
	            String successUrl = "http://localhost:8080/payment/success";
	            Payment payment = service.createPayment(
	                    Double.valueOf(amount),
	                    currency,
	                    method,
	                    "sale",
	                    description,
	                    cancelUrl,
	                    successUrl
	            );

	            for (Links links: payment.getLinks()) {
	                if (links.getRel().equals("approval_url")) {
	                    return new RedirectView(links.getHref());
	                }
	            }
	        } catch (PayPalRESTException e) {
	            log.error("Error occurred:: ", e);
	        }
	        return new RedirectView("/payment/error");
	    }

	    @GetMapping("/payment/success")
	    public String paymentSuccess(
	            @RequestParam("paymentId") String paymentId,
	            @RequestParam("PayerID") String payerId
	    ) {
	        try {
	            Payment payment = service.executePayment(paymentId, payerId);
	            if (payment.getState().equals("approved")) {
	                return "paymentSuccess";
	            }
	        } catch (PayPalRESTException e) {
	            
				log.error("Error occurred:: ", e);
	        }
	        return "paymentSuccess";
	    }

	    @GetMapping("/payment/cancel")
	    public String paymentCancel() {
	        return "paymentCancel";
	    }

	    @GetMapping("/payment/error")
	    public String paymentError() {
	        return "paymentError";
	    }
	
	
}
