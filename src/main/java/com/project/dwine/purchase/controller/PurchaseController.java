package com.project.dwine.purchase.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.dwine.manage.model.vo.Report;
import com.project.dwine.member.model.vo.Member;
import com.project.dwine.member.model.vo.UserImpl;
import com.project.dwine.paging.PageInfo;
import com.project.dwine.purchase.model.service.PurchaseService;
import com.project.dwine.purchase.model.vo.OrderDetail;
import com.project.dwine.purchase.model.vo.Payment;
import com.project.dwine.purchase.model.vo.Point;
import com.project.dwine.purchase.model.vo.Product;
import com.project.dwine.purchase.model.vo.Purchase;
import com.project.dwine.purchase.model.vo.Review;
import com.project.dwine.wish.model.vo.Wish;


@Controller
@RequestMapping("/purchase")
public class PurchaseController {

	private PurchaseService purchaseService;
	
	@Autowired
	public PurchaseController(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
	}

	
	/* ?????? ?????? ????????? ?????? */
	@GetMapping("wine_list")
	public String getWineList(Model model)  throws Exception {
		
		String sortStandard = "popular";
		
		
		int listCount = purchaseService.getTotalListCount();
		
		int resultPage = 1;
		
		PageInfo pi = new PageInfo(resultPage, listCount, 10, 12);
		
		int startRow = (pi.getPage() - 1) * pi.getBoardLimit() + 1;
        int endRow = startRow + pi.getBoardLimit() - 1;
		
		
        List<Product> wineList = new ArrayList<>();
        System.out.println("??????: " + sortStandard);
        
		
		wineList = purchaseService.popularwineList(sortStandard, startRow, endRow);
		System.out.println("?????? ??????");
	
		//System.out.println("????????? ?????? : " + sortStandard);
		//System.out.println("?????? : " + wineList);
		
		model.addAttribute("wineList", wineList);
		model.addAttribute("pi", pi);
		model.addAttribute("sortStandard", sortStandard);
		
		return "/purchase/wine_list";
	}
	
	/* ?????? ?????? */
	@PostMapping("list")
	@ResponseBody
	public Map<String, Object> sortList(@RequestParam(value="page", required=false) String page,
			@RequestParam(value="sortStandard", required=false) String sortStandard, 
			@RequestParam(value="type", required=false) String type, 
			@RequestParam(value="price", required=false) String price, 
			@RequestParam(value="country", required=false) String country, 
			@RequestParam(value="variety", required=false) String variety, 
			@RequestParam(value="name", required=false) String name) throws Exception {

		System.out.println("post ?????? : " + sortStandard);
		


		int searchListCount = 0;
		
		
		if(sortStandard.equals("popular")) {
			searchListCount = purchaseService.getTotalListCount();
		} else {
			searchListCount = purchaseService.getsearchListCount(sortStandard, type, price, country, variety, name);
		}
		
		
		
		int resultPage = 1;
		// ????????? ????????? ?????? ??? ?????? ?????? ?????? ???????????? ?????? ?????? ?????? ?????? page??? ??????
		if(page != null) {
			resultPage = Integer.parseInt(page);
		}
		

		PageInfo pi = new PageInfo(resultPage, searchListCount, 10, 12);
		int startRow = (pi.getPage() - 1) * pi.getBoardLimit() + 1;
		int endRow = startRow + pi.getBoardLimit() - 1;
		

        List<Product> productList = new ArrayList<>();
        
		if(sortStandard.equals("popular")) {
			productList = purchaseService.popularwineList(sortStandard, startRow, endRow);
		} else {
			productList = purchaseService.selectSearchProductList(sortStandard, type, price, country, variety, name, startRow, endRow);
		}
		

		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("productList", productList);
		map.put("pi", pi);
		map.put("sortStandard", sortStandard);
		map.put("type", type);
		map.put("price", price);
		map.put("country", country);
		map.put("variety", variety);
		map.put("name", name);
		
		return map;
	}
	
	
	/* ?????? ????????? ????????? */
	@GetMapping("{id}")
	public String wineDetail(@PathVariable String id, Model model,  @RequestParam(value="page", required=false) String page, @AuthenticationPrincipal User loginCheck) {
		
		Wish wish = null;
		if(loginCheck != null) {
			System.out.println("????????????????????? ?????????");
			UserImpl user = (UserImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			int user_no = user.getUser_no();
			int product_no = Integer.parseInt(id);			

			wish = purchaseService.checkWish(user_no, product_no);
			System.out.println(wish);
		}
		
		/* ????????? ???????????? ????????? ?????? */
		if (wish == null) {
			wish = new Wish();
		}
		
		System.out.println("id : " + id);
		
		Product product = purchaseService.wineDetail(id);
		
		
		int product_no = Integer.parseInt(id);
		
		
		int listCount = purchaseService.getTotalReviewCount(id);
		
		System.out.println("????????????" + listCount);
		
		int resultPage = 1;
		
		// ????????? ????????? ?????? ??? ?????? ?????? ?????? ???????????? ?????? ?????? ?????? ?????? page??? ??????
		if(page != null) {
			resultPage = Integer.parseInt(page);
		}
		
		
		PageInfo pi = new PageInfo(resultPage, listCount, 10, 5);
		
		int startRow = (pi.getPage() - 1) * pi.getBoardLimit() + 1;
        int endRow = startRow + pi.getBoardLimit() - 1;
		
		List<Review> review = purchaseService.reviewList(id, startRow, endRow);
		List<Review> allReviewList = purchaseService.allReviewList(id);
		
		System.out.println("all review : " + allReviewList);
		
		model.addAttribute("review", review);
		model.addAttribute("allReviewList", allReviewList);
		model.addAttribute("wish", wish);
		model.addAttribute("product", product);
		model.addAttribute("pi", pi);
	
		return "purchase/wine_detail";
	}
	
	
	
	

	@PostMapping("reviewlist")
	@ResponseBody
	public Map<String, Object> sortList(@RequestParam(value="page", required=false) String page,
			@RequestParam(value="productNo", required=false) String id) throws Exception {

		
		System.out.println("dfsdafsdfs : " + id);
		
		int searchListCount = purchaseService.getTotalReviewCount(id);
		int resultPage = 1;
		
		// ????????? ????????? ?????? ??? ?????? ?????? ?????? ???????????? ?????? ?????? ?????? ?????? page??? ??????
		if(page != null) {
			resultPage = Integer.parseInt(page);
		}
		
		PageInfo pi = new PageInfo(resultPage, searchListCount, 10, 5);
		
		int startRow = (pi.getPage() - 1) * pi.getBoardLimit() + 1;
        int endRow = startRow + pi.getBoardLimit() - 1;
		
        System.out.println(startRow);
        System.out.println(endRow);
        
        
        List<Review> review = purchaseService.reviewList(id, startRow, endRow);
        
		Map<String, Object> map = new HashMap<String, Object>();
		
		System.out.println("reveiw" + review);
		
		map.put("review", review);
		map.put("pi", pi);
		
		return map;
	}
	
	
	/* ?????? ???????????? */
	@GetMapping("/clause")
	public void clause(Model model) {
		
	}
	
	/* purchase Table ??? ??????  */
	@ResponseBody
	@RequestMapping(value = "/insert", method = { RequestMethod.POST })
	public String purchaseInsert(@RequestParam() int use_point,
			@RequestParam() int purchase_price,
			@RequestParam() String pickup_date,
			@RequestParam() String pickup_place,
			@RequestParam() String pickup_time,
			@RequestParam() int havePoint,
			@RequestParam() String purchase_no){
		UserImpl user = (UserImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    int user_no = user.getUser_no();
	    
	    Purchase purchase = new Purchase();
	    
		purchase.setUser_no(user_no);
		purchase.setUse_point(use_point);
		purchase.setPurchase_price(purchase_price);
		purchase.setPickup_date(pickup_date);
		purchase.setPickup_place(pickup_place);
		purchase.setPickup_time(pickup_time);
		purchase.setPurchase_no(purchase_no);

		System.out.println("purchase ?????? : " +  purchase);
	
		
		 int result1 = purchaseService.purchaseInsert(purchase);
		
		 if(result1 > 0) {
			
			int usePoint = purchase.getUse_point();
			int point = (Math.round((int)(purchase.getPurchase_price() * 0.05)));
			
			System.out.println("?????? ????????? : " + havePoint);
			System.out.println("????????? ????????? : " + usePoint);
			System.out.println("?????? ????????? : " + point);
			System.out.println(result1);
			System.out.println(user_no);
			
			// ????????? ????????? ????????? - ??????????????? + ?????? ?????? ?????????
			int user_point = havePoint - usePoint + point;
			System.out.println("??? ????????? : " + user_point);
			
			/* ?????? ????????? ????????? Update */
			int result2 = purchaseService.memberPoint(user_no, user_point);
			System.out.println(result2);
			
			/* ????????? ????????? Insert */
			int result3 = purchaseService.pointPlus(user_no, point, purchase_no);	
			System.out.println(result3);

		 }
		
		 return "redirect:/purchase/purchase_detail";					
		
	}
	

	/* payment Table ??? ??????  */
	@ResponseBody
	@RequestMapping(value = "/paymentInsert", method = { RequestMethod.POST })
	public String paymentInsert(
			@RequestParam() String pay_no,
			@RequestParam() String pay_method,
			@RequestParam() String purchase_no){
		
	    Payment payment = new Payment();
	    payment.setPay_no(pay_no);
	    payment.setPay_method(pay_method);
	    payment.setPurchase_no(purchase_no);



		System.out.println("payment ?????? : " +  payment);

		int result1 = purchaseService.paymentInsert(payment);

		return "redirect:/purchase/purchase_detail";					
		
	}
	
	
	/* orderDetail Table ??? ??????  */
	@ResponseBody
	@RequestMapping(value = "/orderDetail", method = { RequestMethod.POST })
	public String orderDetailInsert(
			@RequestParam() int od_count,
			@RequestParam() int od_price,
			@RequestParam() String purchase_no,
			@RequestParam() int product_no){
		
	    OrderDetail orderDetail = new OrderDetail();
	    orderDetail.setOd_count(od_count);
	    orderDetail.setOd_price(od_price);
	    orderDetail.setPurchase_no(purchase_no);
	    orderDetail.setProduct_no(product_no);


		System.out.println("purchase ?????? : " +  orderDetail);

		int result1 = purchaseService.orderDetailInsert(orderDetail);

		return "redirect:/purchase/purchase_detail";					
		
	}
	
	
	/* ?????? ?????? ???????????? */
	@ResponseBody
	@RequestMapping(value = "/stockUpdate", method = { RequestMethod.POST })
	public String stockUpdate(
			@RequestParam(value="productNo[]") List<Integer> productNo, 
			@RequestParam(value="stockArr[]") List<Integer> stockArr)  {
		System.out.println(productNo);
		System.out.println(stockArr);
		
		int size = productNo.size();
		
		System.out.println(size);
		
		int product_no = 0;
		int product_count = 0;
	 
		for(int i=0; i<size; i++) {
			System.out.println("?????????");
			product_no = productNo.get(i);
			product_count = stockArr.get(i);
			System.out.println(product_no);
			System.out.println(product_count);
			
			int result = purchaseService.stockUpdate(product_no, product_count);

		}
		
		
		return "redirect:/purchase/purchase_detail";
	}
	
	
	/* ???????????? ?????? */
	@ResponseBody
	@RequestMapping(value = "/cartDelete", method = { RequestMethod.POST })
	public String cartDelete(@RequestParam(value="valueArr[]") List<Integer> cart_no)  {
		System.out.println("??????");
		System.out.println(cart_no);
	
		System.out.println(cart_no.size());
		
		UserImpl user = (UserImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    int user_no = user.getUser_no();
	    
		int size = cart_no.size();
		
		int result = purchaseService.cartDelete(user_no, cart_no);	
		

		System.out.println("?????? ???????????? : " + result);
		return "redirect:/purchase/purchase_detail";
	}
	
	
	/* ???????????? ????????? ?????? */
	@GetMapping("complete/{purchase_no}")
	public ModelAndView purchaseComplete(ModelAndView mv, @PathVariable String purchase_no) {
		//List<Product> wineList = purchaseService.wineList();
		//mv.addObject("wineList", wineList);
		System.out.println("???????????? : " + purchase_no);
		
		UserImpl user = (UserImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int user_no = user.getUser_no();
		
		Member member = purchaseService.memberinfo(user_no);
		
		System.out.println("member : " + member);
		
		Point point = purchaseService.selectPoint(purchase_no);
		
		System.out.println("point : " + point);
		
		
		
	  Purchase purchase =purchaseService.selectPurchase(purchase_no);
	  
	  System.out.println("purchase : " + purchase);
	 
	
	  List<OrderDetail> orderDetail = purchaseService.selectOrderDetail(purchase_no);
	  
	  System.out.println("orderDetail : " + orderDetail);
		 

		mv.addObject("member", member);
		mv.addObject("orderDetail", orderDetail);
		mv.addObject("point", point);
		mv.addObject("purchase", purchase);
		mv.setViewName("purchase/purchase_complete");
		return mv;
	}
	
	/* ?????? ?????? ??? ???????????? */
	@RequestMapping("/reviewReport")
	public String reviewReport(Model model, @RequestParam(required = false) String userNo, @RequestParam(required = false) int reviewNo,@AuthenticationPrincipal User loginCheck,HttpServletResponse response, RedirectAttributes rttr)throws Exception{
		
		System.out.println("??????????????? : " + loginCheck);
		// ????????? ????????? ??????
		if(loginCheck == null) {
			  response.setContentType("text/html; charset=euc-kr");
				PrintWriter out = response.getWriter();
				out.println("<script>alert('???????????? ???????????????.'); self.close();</script>");
				out.flush();
				
		} else {
			
			// ????????? ???????????? ??????
			UserImpl user = (UserImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			// ???????????? ?????? ??????		
			int user_no = user.getUser_no();
			
			Report report = purchaseService.checkReport(user_no, reviewNo);
			
			System.out.println("report?????? : " + report);
			if(report != null) {
				response.setContentType("text/html; charset=euc-kr");
				PrintWriter out = response.getWriter();
				out.println("<script>alert('?????? ????????? ???????????????.'); self.close();</script>");
				out.flush();
			}
		}

		System.out.println("reviewNo :" + reviewNo);
		System.out.println("userNo :" + userNo);
		model.addAttribute("reviewNo", reviewNo);
		model.addAttribute("userNo", userNo);
		
		return "/purchase/reviewReport";
	}


	
	/* ?????? ?????? DB insert*/
	@PostMapping("/reviewReport")
	public void reviewReportReason(@RequestParam int userNo, @RequestParam int reviewNo, @RequestParam(required = false) String reason,HttpServletResponse response) throws IOException{
		
		System.out.println("reason ?????? : " +  reason);
		System.out.println("userNo ????????? : " +  userNo);
		System.out.println("reviewNo ????????? : " +  reviewNo);
		
		UserImpl user = (UserImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		int reasonNo = 0;
	
		if(reason.equals("?????? / ??????")) {
			reasonNo = 1;
		} else if(reason.equals("??????")) {
			reasonNo = 2;
		} else if(reason.equals("?????? / ?????????")) {
			reasonNo = 3;
		} else if(reason.equals("????????? ??????")) {
			reasonNo = 4;
		} else {
			reasonNo = 5;
		}
		
		
		// ???????????? ?????? ??????		
		int user_no = user.getUser_no();
		
		
		int result = purchaseService.insertReport(user_no, userNo, reviewNo, reasonNo);
	    
		if(result > 0) {
			
			int result2 = purchaseService.memberReportedCount(userNo);
			response.setContentType("text/html; charset=euc-kr");
			PrintWriter out = response.getWriter();
			out.println("<script>alert('??????????????? ?????????????????????.'); self.close();</script>");
			out.flush();
			
		}
	}
	
	
}

