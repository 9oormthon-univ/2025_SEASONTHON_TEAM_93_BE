package com.goormthon.hero_home.domain.sponsorshipboard.service;

import com.goormthon.hero_home.domain.sponsorshipboard.converter.SponsorshipBoardConverter;
import com.goormthon.hero_home.domain.sponsorshipboard.dto.SponsorshipBoardResponseDto;
import com.goormthon.hero_home.domain.sponsorshipboard.repository.SponsorshipBoardPhotosRepository;
import com.goormthon.hero_home.domain.sponsorshipboard.repository.SponsorshipBoardRepository;
import com.goormthon.hero_home.domain.sponsorshipboard.dto.SponsorshipBoardRequestDto;
import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoard;
import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoardPhotos;
import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoardStatus;
import com.goormthon.hero_home.domain.user.entity.Role;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.repository.UserRepository;
import com.goormthon.hero_home.global.aws.AwsS3Service;
import com.goormthon.hero_home.global.code.status.ErrorStatus;
import com.goormthon.hero_home.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SponsorshipBoardService {

    private final SponsorshipBoardRepository sponsorshipBoardRepository;
    private final SponsorshipBoardPhotosRepository sponsorshipBoardPhotosRepository;
    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public void registerBoard(Authentication authentication,
                              SponsorshipBoardRequestDto.BoardInfoRequestDto boardInfoRequestDto,
                              List<MultipartFile> imgs) {
        
        User user = getUserFromAuthentication(authentication);
        checkAdminRole(user);

        SponsorshipBoard sponsorshipBoard = SponsorshipBoard.builder()
                .title(boardInfoRequestDto.getTitle())
                .subTitle(boardInfoRequestDto.getSubTitle())
                .content(boardInfoRequestDto.getContent())
                .startDate(boardInfoRequestDto.getStartDate())
                .endDate(boardInfoRequestDto.getEndDate())
                .targetAmount(boardInfoRequestDto.getTargetAmount())
                .sponsorshipStatus(SponsorshipBoardStatus.ACTIVE)
                .user(user)
                .build();

        sponsorshipBoardRepository.save(sponsorshipBoard);
        saveBoardPhotos(sponsorshipBoard, imgs);
    }

    @Transactional(readOnly = true)
    public SponsorshipBoardResponseDto.SponsorshipBoardInfo getBoard(Long boardId) {
        SponsorshipBoard board = sponsorshipBoardRepository.findById(boardId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BOARD_NOT_FOUNT));

        List<SponsorshipBoardPhotos> photos = sponsorshipBoardPhotosRepository.findBySponsorshipBoard(board);

        return SponsorshipBoardConverter.toSponsorshipBoardInfo(board, photos);
    }

    @Transactional
    public void updateBoard(Authentication authentication, Long boardId,
                            SponsorshipBoardRequestDto.BoardInfoRequestDto boardInfoRequestDto, List<MultipartFile> imgs) {
        SponsorshipBoard board = sponsorshipBoardRepository.findById(boardId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BOARD_NOT_FOUNT));

        User user = getUserFromAuthentication(authentication);
        checkAdminRole(user);

        SponsorshipBoard updateBoard = SponsorshipBoard.builder()
                .id(board.getId())
                .title(boardInfoRequestDto.getTitle())
                .subTitle(boardInfoRequestDto.getSubTitle())
                .content(boardInfoRequestDto.getContent())
                .startDate(boardInfoRequestDto.getStartDate())
                .endDate(boardInfoRequestDto.getEndDate())
                .sponsorshipStatus(SponsorshipBoardStatus.ACTIVE)
                .targetAmount(boardInfoRequestDto.getTargetAmount())
                .user(getUserFromAuthentication(authentication))
                .build();

        sponsorshipBoardRepository.save(updateBoard);

        List<SponsorshipBoardPhotos> existingPhotos = sponsorshipBoardPhotosRepository.findBySponsorshipBoard(board);
        for (SponsorshipBoardPhotos photo : existingPhotos) {
            awsS3Service.deleteFile(photo.getFilePath());
            sponsorshipBoardPhotosRepository.delete(photo);
        }
        saveBoardPhotos(updateBoard, imgs);
    }

    @Transactional
    public void deleteBoard(Authentication authentication, Long boardId) {
        User user = getUserFromAuthentication(authentication);
        checkAdminRole(user);

        SponsorshipBoard sponsorshipBoard = sponsorshipBoardRepository.findById(boardId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BOARD_NOT_FOUNT));

        sponsorshipBoardRepository.deleteById(boardId);

        List<SponsorshipBoardPhotos> existingPhotos = sponsorshipBoardPhotosRepository.findBySponsorshipBoard(sponsorshipBoard);
        for (SponsorshipBoardPhotos photo : existingPhotos) {
            awsS3Service.deleteFile(photo.getFilePath());
            sponsorshipBoardPhotosRepository.delete(photo);
        }
    }

    @Transactional(readOnly = true)
    public Page<SponsorshipBoardResponseDto.SponsorshipBoardInfo> getAllBoards(Pageable pageable) {
        return sponsorshipBoardRepository.findAll(pageable)
                .map(board -> {
                    List<SponsorshipBoardPhotos> boardPhotos =
                            sponsorshipBoardPhotosRepository.findBySponsorshipBoard(board);
                    return SponsorshipBoardConverter.toSponsorshipBoardInfo(board, boardPhotos);
                });
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private void saveBoardPhotos(SponsorshipBoard board, List<MultipartFile> imgs) {
        if (imgs == null) return;

        for (MultipartFile img : imgs) {
            String imgUrl = awsS3Service.uploadFile(img);
            SponsorshipBoardPhotos photos = SponsorshipBoardPhotos.builder()
                    .filePath(imgUrl)
                    .sponsorshipBoard(board)
                    .build();

            sponsorshipBoardPhotosRepository.save(photos);
        }
    }

    private void checkAdminRole(User user) {
        if(user.getRole() != Role.ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
    }
}
