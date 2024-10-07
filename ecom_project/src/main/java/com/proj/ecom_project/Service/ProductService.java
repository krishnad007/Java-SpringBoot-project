package com.proj.ecom_project.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.proj.ecom_project.Model.Product;
import com.proj.ecom_project.repo.ProductRepo;


@Service
public class ProductService {
      
	@Autowired
	private ProductRepo repo;
	
    private  APIContext apiContext;
    
    @Autowired
    public ProductService(APIContext apiContext) {
    	this.apiContext=apiContext;
    }

    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl
    ) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total)); // 9.99$ - 9,99€

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(
            String paymentId,
            String payerId
    ) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }
	
	
	public List<Product> getAllproducts() {
		
		return repo.findAll();
	}
	public Product getbyid(int id) {
		// TODO Auto-generated method stub
		
		return repo.findById(id).orElse(null);
	}
	public Product addproduct(Product product, MultipartFile image) throws IOException {
		// TODO Auto-generated method stub
		
		product.setImageName(image.getOriginalFilename());
		product.setImageType(image.getContentType());
		product.setImageData(image.getBytes());
		product.setDiscount(product.getDiscount());
		return repo.save(product);
		
	}
	public Product updateproduct(int id, Product product, MultipartFile image) throws IOException {
		// TODO Auto-generated method stub
	
		product.setImageData(image.getBytes());
		product.setImageName(image.getOriginalFilename());
		product.setImageType(image.getContentType());
		product.setDiscount(product.getDiscount());
		return repo.save(product);
	}
	

	public void deleted(int id) {
		// TODO Auto-generated method stub
		 repo.deleteById(id);
		
	}
	public List<Product> searchprodct(String keyword) {
		// TODO Auto-generated method stub
		
		return repo.searchproduct(keyword);
	}

	
}
