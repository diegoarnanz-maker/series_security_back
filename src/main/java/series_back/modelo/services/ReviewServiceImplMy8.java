package series_back.modelo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import series_back.modelo.dto.ReviewDto;
import series_back.modelo.entities.Review;
import series_back.modelo.repository.IReviewRepository;

@Service
public class ReviewServiceImplMy8 extends GenericoCRUDServiceImplMy8<Review, Long> implements IReviewService {

    @Autowired
    private IReviewRepository reviewRepository;

    @Override
    protected IReviewRepository getRepository() {
        return reviewRepository;
    }

    // Método privado para convertir Review a ReviewDto
    private ReviewDto convertToDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .username(review.getUser().getUsername())
                .seriesId(review.getSeries().getId())
                .seriesTitle(review.getSeries().getTitle())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    @Override
    public List<ReviewDto> findAllWithDto() {
        return reviewRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> findByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> findBySeriesId(Long seriesId) {
        if (seriesId == null) {
            throw new IllegalArgumentException("El ID de la serie no puede ser nulo.");
        }
        return reviewRepository.findBySeriesId(seriesId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> findByMinRating(Double minRating) {
        if (minRating == null) {
            throw new IllegalArgumentException("El rating mínimo no puede ser nulo.");
        }
        return reviewRepository.findByRatingGreaterThanEqual(minRating)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> findByRatingRange(Double minRating, Double maxRating) {
        if (minRating == null || maxRating == null) {
            throw new IllegalArgumentException("Los ratings no pueden ser nulos.");
        }
        if (minRating > maxRating) {
            throw new IllegalArgumentException("El rating mínimo no puede ser mayor al rating máximo.");
        }
        return reviewRepository.findByRatingBetween(minRating, maxRating)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}







//ENFOQUE CON TRY / CATCH
// package series_back.modelo.services;

// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import series_back.modelo.dto.ReviewDto;
// import series_back.modelo.entities.Review;
// import series_back.modelo.repository.IReviewRepository;

// @Service
// public class ReviewServiceImplMy8 extends GenericoCRUDServiceImplMy8<Review, Long> implements IReviewService{

//     @Autowired
//     private IReviewRepository reviewRepository;

//     @Override
//     protected IReviewRepository getRepository() {
//         return reviewRepository;
//     }

//     private ReviewDto convertToDto(Review review) {
//         return ReviewDto.builder()
//                 .id(review.getId())
//                 .username(review.getUser().getUsername())
//                 .seriesId(review.getSeries().getId())
//                 .seriesTitle(review.getSeries().getTitle())
//                 .rating(review.getRating())
//                 .comment(review.getComment())
//                 .createdAt(review.getCreatedAt())
//                 .build();
//     }
    

//     @Override
//     public List<ReviewDto> findAllWithDto() {
//         try{
//             List<Review> reviews = reviewRepository.findAll();
//             return reviews.stream().map(this::convertToDto).collect(Collectors.toList());
//         } catch (Exception e){
//             throw new UnsupportedOperationException("Unimplemented method 'findAllWithDto'");
//         }
//     }

//     @Override
//     public List<ReviewDto> findByUserId(Long userId) {
//         try{
//             List<Review> reviews = reviewRepository.findByUserId(userId);
//             return reviews.stream().map(this::convertToDto).collect(Collectors.toList());
//         } catch (Exception e){
//             throw new UnsupportedOperationException("Unimplemented method 'findByUserId'");
//         }
//     }

//     @Override
//     public List<ReviewDto> findBySeriesId(Long seriesId) {
//         try{
//             if(seriesId == null){
//                 throw new IllegalArgumentException("El id de la serie no puede ser nulo.");
//             }
//             List<Review> reviews = reviewRepository.findBySeriesId(seriesId);
//             if (reviews.isEmpty()) {
//                 throw new IllegalArgumentException("No se encontraron reviews para la serie con id " + seriesId);
                
//             }
//             return reviews.stream().map(this::convertToDto).collect(Collectors.toList());
//         } catch (Exception e){
//             throw new UnsupportedOperationException("Unimplemented method 'findBySeriesId'");
//         }
//     }

//     @Override
//     public List<ReviewDto> findByMinRating(Double minRating) {
//         try{
//             if(minRating == null){
//                 throw new IllegalArgumentException("El rating mínimo no puede ser nulo.");
//             }
//             List<Review> reviews = reviewRepository.findByRatingGreaterThanEqual(minRating);
//             if (reviews.isEmpty()) {
//                 throw new IllegalArgumentException("No se encontraron reviews con rating mayor o igual a " + minRating);
//             }
//             return reviews.stream().map(this::convertToDto).collect(Collectors.toList());
//         } catch (Exception e){
//             throw new UnsupportedOperationException("Unimplemented method 'findByMinRating'");
//         }
//     }

//     @Override
//     public List<ReviewDto> findByRatingRange(Double minRating, Double maxRating) {
//         try{
//             if(minRating == null || maxRating == null){
//                 throw new IllegalArgumentException("Los ratings no pueden ser nulos.");
//             }
//             if(minRating > maxRating){
//                 throw new IllegalArgumentException("El rating mínimo no puede ser mayor al rating máximo.");
//             }
//             List<Review> reviews = reviewRepository.findByRatingBetween(minRating, maxRating);
//             if (reviews.isEmpty()) {
//                 throw new IllegalArgumentException("No se encontraron reviews con rating entre " + minRating + " y " + maxRating);
//             }
//             return reviews.stream().map(this::convertToDto).collect(Collectors.toList());
//         } catch (Exception e){
//             throw new UnsupportedOperationException("Unimplemented method 'findByRatingRange'");
//         }
//     }

// }
