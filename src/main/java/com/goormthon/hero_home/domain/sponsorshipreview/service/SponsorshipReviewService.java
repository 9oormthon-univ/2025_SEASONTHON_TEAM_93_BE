package com.goormthon.hero_home.domain.sponsorshipreview.service;

import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoard;
import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoardPhotos;
import com.goormthon.hero_home.domain.sponsorshipboard.repository.SponsorshipBoardRepository;
import com.goormthon.hero_home.domain.sponsorshipreview.converter.SponsorshipReviewConverter;
import com.goormthon.hero_home.domain.sponsorshipreview.dto.SponsorshipReviewRequestDto;
import com.goormthon.hero_home.domain.sponsorshipreview.dto.SponsorshipReviewResponseDto;
import com.goormthon.hero_home.domain.sponsorshipreview.entity.SponsorshipReview;
import com.goormthon.hero_home.domain.sponsorshipreview.entity.SponsorshipReviewPhotos;
import com.goormthon.hero_home.domain.sponsorshipreview.repository.SponsorshipReviewPhotosRepository;
import com.goormthon.hero_home.domain.sponsorshipreview.repository.SponsorshipReviewRepository;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.repository.UserRepository;
import com.goormthon.hero_home.global.aws.AwsS3Service;
import com.goormthon.hero_home.global.code.status.ErrorStatus;
import com.goormthon.hero_home.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorshipReviewService {

    private final SponsorshipReviewRepository sponsorshipReviewRepository;
    private final SponsorshipBoardRepository sponsorshipBoardRepository;
    private final SponsorshipReviewPhotosRepository sponsorshipReviewPhotosRepository;
    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;

    @Transactional
    public void registerReview(Authentication authentication,
                               SponsorshipReviewRequestDto.SponsorshipReviewInfo sponsorshipReviewInfoDto,
                               List<MultipartFile> imgs) {

        String email = authentication.getName();

        userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        SponsorshipBoard sponsorshipBoard = sponsorshipBoardRepository.findById(sponsorshipReviewInfoDto.getSponsorshipBoardId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.BOARD_NOT_FOUNT));

        SponsorshipReview boardReview = SponsorshipReview.builder()
                .title(sponsorshipReviewInfoDto.getTitle())
                .content(sponsorshipReviewInfoDto.getContent())
                .sponsorshipBoard(sponsorshipBoard)
                .build();

        sponsorshipReviewRepository.save(boardReview);

        saveBoardReviewPhotos(boardReview, imgs);
    }

    @Transactional(readOnly = true)
    public SponsorshipReviewResponseDto.SponsorshipReviewInfo getReview(Long boardId) {
        SponsorshipReview sponsorshipReview = sponsorshipReviewRepository.findById(boardId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));

        List<SponsorshipReviewPhotos> reviewPhotos = sponsorshipReviewPhotosRepository.findBySponsorshipReview(sponsorshipReview);
        return SponsorshipReviewConverter.toSponsorshipReviewInfo(sponsorshipReview, reviewPhotos);
    }

    @Transactional
    public void updateReview(Authentication authentication, Long reviewId,
                             SponsorshipReviewRequestDto.SponsorshipReviewInfo sponsorshipReviewInfoDto,
                             List<MultipartFile> imgs) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        SponsorshipReview sponsorshipReview = sponsorshipReviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));

        SponsorshipReview updateReview = SponsorshipReview.builder()
                .id(sponsorshipReview.getId())
                .title(sponsorshipReviewInfoDto.getTitle())
                .content(sponsorshipReviewInfoDto.getContent())
                .sponsorshipBoard(sponsorshipReview.getSponsorshipBoard())
                .build();

        sponsorshipReviewRepository.save(updateReview);

        List<SponsorshipReviewPhotos> existingPhotos = sponsorshipReviewPhotosRepository.findBySponsorshipReview(sponsorshipReview);
        for (SponsorshipReviewPhotos photo : existingPhotos) {
            awsS3Service.deleteFile(photo.getFilePath());
            sponsorshipReviewPhotosRepository.delete(photo);
        }
        saveBoardReviewPhotos(updateReview, imgs);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        SponsorshipReview sponsorshipReview = sponsorshipReviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));

        sponsorshipReviewRepository.deleteById(reviewId);

        List<SponsorshipReviewPhotos> existingPhotos = sponsorshipReviewPhotosRepository.findBySponsorshipReview(sponsorshipReview);
        for (SponsorshipReviewPhotos photo : existingPhotos) {
            awsS3Service.deleteFile(photo.getFilePath());
            sponsorshipReviewPhotosRepository.delete(photo);
        }
    }

    @Transactional(readOnly = true)
    public Page<SponsorshipReviewResponseDto.SponsorshipReviewInfo> getAllReviews(Pageable pageable) {
        return sponsorshipReviewRepository.findAll(pageable)
                .map(review -> {
                    List<SponsorshipReviewPhotos> reviewPhotos =
                            sponsorshipReviewPhotosRepository.findBySponsorshipReview(review);
                    return SponsorshipReviewConverter.toSponsorshipReviewInfo(review, reviewPhotos);
                });
    }

    private void saveBoardReviewPhotos(SponsorshipReview review, List<MultipartFile> imgs) {
        if (imgs == null) return;

        for (MultipartFile img : imgs) {
            String imgUrl = awsS3Service.uploadFile(img);
            SponsorshipReviewPhotos photos = SponsorshipReviewPhotos.builder()
                    .filePath(imgUrl)
                    .sponsorshipReview(review)
                    .build();

            sponsorshipReviewPhotosRepository.save(photos);
        }
    }
}
