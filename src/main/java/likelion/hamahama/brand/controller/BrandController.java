package likelion.hamahama.brand.controller;

import likelion.hamahama.brand.dto.BrandDto;
import likelion.hamahama.brand.entity.Brand;
import likelion.hamahama.brand.entity.BrandLike;
import likelion.hamahama.brand.repository.BrandLikeRepsitory;
import likelion.hamahama.brand.repository.BrandRepository;
import likelion.hamahama.brand.service.BrandService;
import likelion.hamahama.common.CreateResponseMessage;
import likelion.hamahama.coupon.entity.Coupon;
import likelion.hamahama.coupon.entity.enums.Category;
import likelion.hamahama.user.entity.User;
import likelion.hamahama.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final  BrandLikeRepsitory brandLikeRepsitory;

    // 모든 브랜드 조회
    @GetMapping("/brandList")
    public List<Brand> findAll_brand() {
        return brandService.findAll_brand();
    }

    // 브랜드 상세 조회
    @GetMapping("/{brandId}")
    public BrandDto getBrand(@PathVariable long brandId) {
        BrandDto theBrandDTO = new BrandDto(brandService.findBrandById(brandId).get());
        return theBrandDTO;
    }


    //단일 브랜드 조회 (사용 x)
    @GetMapping("")
    public BrandDto getBrand(@RequestParam(value="brandId") Long brandId){
        BrandDto theBrandDTO = new BrandDto(brandService.findBrandById(brandId).get());

        if(theBrandDTO == null){
            throw new RuntimeException("브랜드가 발견 되지 않았습니다 - " + brandId);
        }

        return theBrandDTO;
    }
    // ============== 브랜드 즐겨찾기 (마이페이지) ================
    @PostMapping("/mypage/{userId}/{brandId}/edit")
    public CreateResponseMessage likeBrand(@PathVariable("userId") Long userId, @PathVariable("brandId") Long brandId){
        Optional<Brand> brand = brandRepository.findById(brandId);
        User user = userRepository.findById(userId).get();
        BrandLike brandLike = brandLikeRepsitory.findOneByUserAndBrand(user, brand.get());
        if(brandLike == null ){
            brandService.createBrandFavorite(user, brand.get());
        }else brandService.deleteBrandFavorite(user, brand.get());
        return new CreateResponseMessage((long) 200, "좋아요 성공");
    }

    // ============== 카테고리에 맞는 브랜드 찾기 =============.
    @GetMapping("/{category}")
    public List<Brand> brandByCategory(@PathVariable String category){
        return brandService.findByCategory(Category.valueOf(category));
    }

    // ============= 브랜드 검색 시 브랜드 출력 ==============
    public List<Brand> findBrandByKeyword(@PathVariable String keyword){
        List<Brand> brandlist = brandService.findByKeyword(keyword);
        return brandlist;
    }


    // 브랜드 삭제 (
    @DeleteMapping("/brands/{brandId}")
    public String deleteBrand(@PathVariable Long brandId){
        Optional<Brand> tempBrand = brandService.findBrandById(brandId);

        if(tempBrand == null){
            throw new RuntimeException("브랜드가 발견 되지 않았습니다 - " + brandId);
        }

//        List<Coupon> coupons = tempBrand.get().getCoupons();
//
//        for(Coupon tempCoupon : coupons){
//            tempCoupon.setBrand(null);
//        }

        brandService.deleteById(brandId);

        return "브랜드가 삭제 되었습니다 - " + brandId;
    }
}
