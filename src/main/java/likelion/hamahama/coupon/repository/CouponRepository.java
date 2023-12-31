package likelion.hamahama.coupon.repository;

import likelion.hamahama.brand.entity.Brand;
import likelion.hamahama.coupon.entity.Coupon;
import likelion.hamahama.coupon.entity.CouponLike;
import likelion.hamahama.coupon.entity.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    //===== 쿠폰이 속한 카테고리로 쿠폰 찾는 메서드
    //Page<Coupon> findByCategory(Category category, Pageable pageable);

    /** 추가 브랜드 쿠폰 찾는 메서드 */
//    Page<Coupon> findByBrand(Brand brand, Pageable pageable);
//    Page<Coupon> findAllByBrand(long brandId, Pageable pageable);

    List<Coupon> findAllByUser(Long userId);


    // ========= 쿠폰 이름에 포함된 키워드 찾는 메서드
    List<Coupon> findByCouponNameContaining(String searchKeyword);
    Coupon findByCouponName(String couponName);

    //List<Coupon> findAllByLikeUsersIn(List<CouponLike> couponLike);

    // === 쿠폰 정렬 (인기순/최신순) 일단 만들어두는 중
//    Page<Coupon> findById(Long couponId, Pageable pageable);
//    Page<Coupon> findAll(Pageable pageable);
//    Page<Coupon> findByLikeCount(Integer likeCount, Pageable pageable);

    // 추가=
    //Page<Coupon> findAllByLikeUsersIn(List<CouponLike> couponLike, Pageable pageable);
    //태현


    @Query("SELECT u FROM Coupon u WHERE u.brand.id = :data")
    List<Coupon> findAllByBrandId(@Param("data") long theId);

    List<Coupon> findAll();

    Optional<Coupon> findById(Long couponId);


    //boolean existsById(Long couponId);
    boolean existsById(Long couponId);

    List<Coupon> findByBrandId(Long brand);


    Coupon findByCouponNameAndBrand_brandName(String couponName, String brandName);

    List<Coupon> findAllByCategory(Category category);

}
