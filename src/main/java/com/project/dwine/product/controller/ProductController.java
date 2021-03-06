package com.project.dwine.product.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.dwine.paging.PageInfo;
import com.project.dwine.product.model.service.ProductService;
import com.project.dwine.product.model.vo.Country;
import com.project.dwine.product.model.vo.Product;
import com.project.dwine.product.model.vo.Type;
import com.project.dwine.product.model.vo.Variety;

@Controller
@RequestMapping("/product")
public class ProductController {
   
   private ProductService productService;
   
   @Autowired
   public ProductController(ProductService productService) {
      this.productService = productService;
   }
   
   @GetMapping("list")
   public String listPage(@RequestParam(value="page", required=false) String page, @RequestParam(value="sortStandard", required=false) String sortStandard, 
		   @RequestParam(value="searchStandard", required=false) String searchStandard, @RequestParam(value="searchValue", required=false) String searchValue, Model model) throws Exception {
      
      if(sortStandard == null) {
         sortStandard = "no_low";
      }
   
      int listCount = productService.getListCount(sortStandard, searchStandard, searchValue);
      
      int resultPage = 1;
      
      // ????????? ????????? ?????? ??? ?????? ?????? ?????? ???????????? ?????? ?????? ?????? ?????? page??? ??????
      if(page != null) {
         resultPage = Integer.parseInt(page);
      }
      
      PageInfo pi = new PageInfo(resultPage, listCount, 10, 10);
      
      int startRow = (pi.getPage() - 1) * pi.getBoardLimit() + 1;
        int endRow = startRow + pi.getBoardLimit() - 1;
      
      List<Product> productList = productService.selectProductList(sortStandard, searchStandard, searchValue, startRow, endRow);
      
      model.addAttribute("productList", productList);
      model.addAttribute("pi", pi);
      model.addAttribute("sortStandard", sortStandard);

      return "/product/list";
     
   }
   
   @PostMapping("list")
   @ResponseBody
   public Map<String, Object> sortList(@RequestParam(value="page", required=false) String page, @RequestParam(value="sortStandard", required=false) String sortStandard, 
		   @RequestParam(value="searchStandard", required=false) String searchStandard, @RequestParam(value="searchValue", required=false) String searchValue) throws Exception {

      int searchListCount = productService.getListCount(sortStandard, searchStandard, searchValue);
      
      int resultPage = 1;
      
      // ????????? ????????? ?????? ??? ?????? ?????? ?????? ???????????? ?????? ?????? ?????? ?????? page??? ??????
      if(page != null) {
         resultPage = Integer.parseInt(page);
      }
      
      PageInfo pi = new PageInfo(resultPage, searchListCount, 10, 10);
      
      int startRow = (pi.getPage() - 1) * pi.getBoardLimit() + 1;
      int endRow = startRow + pi.getBoardLimit() - 1;
      
      List<Product> productList = productService.selectProductList(sortStandard, searchStandard, searchValue, startRow, endRow);
      
      Map<String, Object> map = new HashMap<String, Object>();
      
      map.put("productList", productList);
      map.put("pi", pi);
      map.put("sortStandard", sortStandard);
      map.put("searchStandard", searchStandard);
      map.put("searchValue", searchValue);
   
      return map;
   }
   
   @GetMapping("category")
   @ResponseBody
   public Map<String, Object> findCategoryList() {
      Map<String, Object> map = new HashMap<>();
      List<Type> typeList = productService.selectTypeList();
      List<Country> countryList = productService.selectCountryList();
      List<Variety> varietyList = productService.selectVarietyList();
      
      map.put("type", typeList);
      map.put("country", countryList);
      map.put("variety", varietyList);
      
      return map;
   }
   
   @GetMapping("detail/{productNo}")
   public String selectProductByNo(@PathVariable int productNo, Model model) {
      Product product = productService.selectProductByNo(productNo);
      model.addAttribute("product", product);
      
      return "product/detail";
   }
   
   @GetMapping("regist") 
   public void registPage() {}
   
   @PostMapping("productNameCheck")
   public void nameCheck(@RequestParam String kname, HttpServletResponse response) throws IOException {
      
      int result = productService.productNameCheck(kname);
      
      response.setCharacterEncoding("UTF-8");
      response.getWriter().print(result);
   }
   
   @PostMapping("regist")
   public String registProduct(RedirectAttributes rttr, @RequestParam MultipartFile thumbnail, @RequestParam MultipartFile labelImg, HttpServletRequest request) {
      // ????????????, ????????? ?????? ?????????
      String kname = request.getParameter("kname");
      String ename = request.getParameter("ename");
      int salePrice = Integer.parseInt(request.getParameter("salePrice"));
      int productCount = Integer.parseInt(request.getParameter("productCount"));
      String winary = request.getParameter("winary");
      int capacity = Integer.parseInt(request.getParameter("capacity"));
      double abv = Double.parseDouble(request.getParameter("abv"));
      String information = request.getParameter("information");
      String award = request.getParameter("award");
      String tip = request.getParameter("tip");
      
      // ????????????
      int typeNo = Integer.parseInt(request.getParameter("typeNo"));
      int varietyNo = Integer.parseInt(request.getParameter("varietyNo"));
      int countryNo = Integer.parseInt(request.getParameter("countryNo"));
      String typeName = request.getParameter("typeName");
      String varietyName = request.getParameter("varietyName");
      String countryName = request.getParameter("countryName");
      
      // ??? ?????????
      String sweetness = request.getParameter("sweetness");
      String acidity = request.getParameter("acidity");
      String body = request.getParameter("body");
      String tannin = request.getParameter("tannin");
      String tasteGraph = sweetness + "/" + acidity + "/" + body + "/" + tannin;
      
      // ???????????? 
      int hash1 = Integer.parseInt(request.getParameter("mood1"));
      int hash2 = Integer.parseInt(request.getParameter("mood2"));
      int hash3 = Integer.parseInt(request.getParameter("food1"));
      int hash4 = Integer.parseInt(request.getParameter("food2"));
      int[] hashArr = {hash1, hash2, hash3, hash4};
         
      // ????????? ??????(?????????, ???????????????)
      String currentDir = System.getProperty("user.dir");
      // System.getProperty("user.dir"); => ?????? ???????????? ???????????? ???????????? ?????????   
      
      // ????????? ??????????????? ??? ??????
      // currentDir + \src\main\resources\static\images
      String filePath = currentDir + "\\src\\main\\resources\\static\\images\\product\\uploadFiles";
      
      // ???????????? ?????? ??? ?????? ??????
      File mkdir = new File(filePath);
      if(!mkdir.exists()) mkdir.mkdirs();
      
      // ????????? ??????????????? ??????
      String thumbnail_originFileName = thumbnail.getOriginalFilename();
      String thumbnail_ext = thumbnail_originFileName.substring(thumbnail_originFileName.lastIndexOf(".")); // ????????? ??????
      String thumbnail_savedName = UUID.randomUUID().toString().replace("-", "") + thumbnail_ext;
      
      String label_originFileName = labelImg.getOriginalFilename();
      String label_ext = label_originFileName.substring(label_originFileName.lastIndexOf(".")); // ????????? ??????
      String label_savedName = UUID.randomUUID().toString().replace("-", "") + label_ext;
      
      try {
         // ?????? ??????
         thumbnail.transferTo(new File(filePath + "\\" + thumbnail_savedName));
         labelImg.transferTo(new File(filePath + "\\" + label_savedName));
      } catch (IllegalStateException | IOException e) {
         e.printStackTrace();
      }
      
      String path = "\\images\\product\\uploadFiles";
      String thumbPath = path + "\\" + thumbnail_savedName;
      String labelPath = path + "\\" + label_savedName;
      
      Variety variety = new Variety(varietyNo, varietyName);
      Type type = new Type(typeNo, typeName);
      Country country = new Country(countryNo, countryName);
      
      Product product = new Product(kname, ename, salePrice, productCount,
            winary, thumbPath, capacity, abv, tasteGraph, information, award, tip, labelPath, variety, type, country);
      
      int result = productService.registProduct(product);
      int result2 = 0;
      
      if(result > 0) {
         for(int i = 0; i < 4; i++) {
            result2 += productService.registProductHash(hashArr[i]);
            
            if(result2 == hashArr.length) {
               rttr.addFlashAttribute("message", "?????? ????????? ?????????????????????.");
            } else {
               rttr.addFlashAttribute("message", "?????? ????????? ?????????????????????.");
            }
         }   
      }
      return "redirect:/product/list";
   }
   
   @GetMapping("modify/{productNo}")
   public String modifyPage(Model model, @PathVariable int productNo) {
      Product product = productService.selectProductByNo(productNo);
      model.addAttribute("product", product);
      
      return "product/modify";
   }
   
   @PostMapping("modify")
   public String modifyProduct(RedirectAttributes rttr, @RequestParam(required = false) MultipartFile thumbnail, @RequestParam(required = false) MultipartFile labelImg, HttpServletRequest request) throws IllegalStateException, IOException {
      // ????????????, ????????? ?????? ?????????
      int productNo = Integer.parseInt(request.getParameter("productNo"));
      String kname = request.getParameter("kname");
      String ename = request.getParameter("ename");
      int salePrice = Integer.parseInt(request.getParameter("salePrice"));
      int productCount = Integer.parseInt(request.getParameter("productCount"));
      String winary = request.getParameter("winary");
      int capacity = Integer.parseInt(request.getParameter("capacity"));
      double abv = Double.parseDouble(request.getParameter("abv"));
      String information = request.getParameter("information");
      String award = request.getParameter("award");
      String tip = request.getParameter("tip");
      
      // ????????????
      int typeNo = Integer.parseInt(request.getParameter("typeNo"));
      int varietyNo = Integer.parseInt(request.getParameter("varietyNo"));
      int countryNo = Integer.parseInt(request.getParameter("countryNo"));
      String typeName = request.getParameter("typeName");
      String varietyName = request.getParameter("varietyName");
      String countryName = request.getParameter("countryName");
      
      // ??? ?????????
      String sweetness = request.getParameter("sweetness");
      String acidity = request.getParameter("acidity");
      String body = request.getParameter("body");
      String tannin = request.getParameter("tannin");
      String tasteGraph = sweetness + "/" + acidity + "/" + body + "/" + tannin;
      
      // ?????? ??? ???????????? 
      int hash1 = Integer.parseInt(request.getParameter("mood1"));
      int hash2 = Integer.parseInt(request.getParameter("mood2"));
      int hash3 = Integer.parseInt(request.getParameter("food1"));
      int hash4 = Integer.parseInt(request.getParameter("food2"));
      int[] hashArr = {hash1, hash2, hash3, hash4};
         
      // ?????? ??? ????????????
      String preHash = request.getParameter("pre_hash");
      String[] preHashArr = preHash.split("/");
      
      // ????????? ??????(?????????, ???????????????)
      String currentDir = System.getProperty("user.dir");
      // System.getProperty("user.dir"); => ?????? ???????????? ???????????? ???????????? ?????????
      // currentDir : C:\Users\?????????\AppData\Roaming\SPB_16.6\git\DWine   
      
      // ????????? ??????
      // file??? null??? ????????? ????????? ?????? ??????
      String delete_path = currentDir + "\\src\\main\\resources\\static";
      String old_thumbPath = request.getParameter("old_thumbPath");
      String old_labelPath = request.getParameter("old_labelPath");
      
      // file??? null??? ????????? ????????? ???????????? ?????? ??????
      if(!thumbnail.isEmpty()) {
         if (!old_thumbPath.equals("")) {
            File deleteThumbnail = new File(delete_path + old_thumbPath);
            deleteThumbnail.delete();
         }
      }
      if(!labelImg.isEmpty()) {
         if (!old_labelPath.equals("")) {
            File deleteLabel = new File(delete_path + old_labelPath);
            deleteLabel.delete();
         }
      }

      // ????????? ??????
      // ????????? ??????????????? ??? ??????
      String filePath = currentDir + "\\src\\main\\resources\\static\\images\\product\\uploadFiles";
      
      // ???????????? ?????? ??? ?????? ??????
      File mkdir = new File(filePath);
      if(!mkdir.exists()) mkdir.mkdirs();
      
      String save_path = "\\images\\product\\uploadFiles";
      String new_thumbPath = "";
      String new_labelPath = "";
      
      // file??? null??? ????????? ????????? ?????? ??????
      if(!thumbnail.isEmpty()) {
         
         // ????????? ??????????????? ??????
         String thumbnail_originFileName = thumbnail.getOriginalFilename();
         String thumbnail_ext = thumbnail_originFileName.substring(thumbnail_originFileName.lastIndexOf(".")); // ????????? ??????
         String thumbnail_savedName = UUID.randomUUID().toString().replace("-", "") + thumbnail_ext;
         
         thumbnail.transferTo(new File(filePath + "\\" + thumbnail_savedName));
         new_thumbPath = save_path + "\\" + thumbnail_savedName;
      }
      if(!labelImg.isEmpty()) {
         
         String label_originFileName = labelImg.getOriginalFilename();
         String label_ext = label_originFileName.substring(label_originFileName.lastIndexOf(".")); // ????????? ??????
         String label_savedName = UUID.randomUUID().toString().replace("-", "") + label_ext;
         
         labelImg.transferTo(new File(filePath + "\\" + label_savedName));
         new_labelPath = save_path + "\\" + label_savedName;;
      }
      
      // ????????????(?????? ??????, ?????? ??????, ??????) ??????
      Variety variety = new Variety(varietyNo, varietyName);
      Type type = new Type(typeNo, typeName);
      Country country = new Country(countryNo, countryName);
      
      Product product = new Product(productNo, kname, ename, salePrice, productCount,
            winary, new_thumbPath, capacity, abv, tasteGraph, information, award, tip, new_labelPath, variety, type, country);
      
      int result = productService.modifyProduct(product);
      int result2 = 0;
      
      if(result > 0) {
         for(int i = 0; i < 4; i++) {
            result2 += productService.modifyProductHash(productNo, hashArr[i], Integer.parseInt(preHashArr[i]));
            
            if(result2 == hashArr.length) {
               rttr.addFlashAttribute("message", "?????? ????????? ?????????????????????.");
            } else {
               rttr.addFlashAttribute("message", "?????? ????????? ?????????????????????.");
            }
         }
      }
      
      return "redirect:/product/list";
   }
   
   @PostMapping("delete") 
   public String deleteProduct(@RequestParam String productNo, RedirectAttributes rttr) {
      
      Product product = productService.selectImgPath(Integer.parseInt(productNo));
      
      String currentDir = System.getProperty("user.dir");
      String delete_path = currentDir + "\\src\\main\\resources\\static";
      
      String thumbPath = product.getThumbnail();
      String labelPath = product.getLabelImage();
   
      if (!thumbPath.equals("")) {
         File deleteThumb = new File(delete_path + thumbPath);
         deleteThumb.delete();
      }
      
      if (!labelPath.equals("")) {
         File deleteLabel = new File(delete_path + labelPath);
         deleteLabel.delete();
      }
      
      int result = productService.deleteProduct(Integer.parseInt(productNo));
      
      if(result > 0) {
         rttr.addFlashAttribute("message", "?????? ????????? ?????????????????????.");
      } else {
         rttr.addFlashAttribute("message", "?????? ????????? ?????????????????????.");
      }
      
      return "redirect:/product/list";
   }
   
   @PostMapping("deleteMulti") 
   public String deleteMultiProduct(HttpServletRequest request, RedirectAttributes rttr) {
      String[] productNos = request.getParameterValues("delete_nums");

      String currentDir = System.getProperty("user.dir");
      String delete_path = currentDir + "\\src\\main\\resources\\static";
      
      int result = 0;
      
      for(String productNo : productNos) {
         Product product = productService.selectImgPath(Integer.parseInt(productNo));
         
         String thumbPath = product.getThumbnail();
         String labelPath = product.getLabelImage();
         
         if (!thumbPath.equals("")) {
            File deleteThumb = new File(delete_path + thumbPath);
            deleteThumb.delete();
         }
         
         if (!labelPath.equals("")) {
            File deleteLabel = new File(delete_path + labelPath);
            deleteLabel.delete();
         }
         
         result += productService.deleteMultiProduct(Integer.parseInt(productNo));
      }
      
      if(result == productNos.length) {
         rttr.addFlashAttribute("message", "?????? ?????? ????????? ?????????????????????.");
      } else {
         rttr.addFlashAttribute("message", "?????? ?????? ????????? ?????????????????????.");
      }
   
      return "redirect:/product/list";
   }

}