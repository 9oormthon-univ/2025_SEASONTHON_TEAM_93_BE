package com.goormthon.hero_home.domain.sponsorshipstatus.service;

import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoard;
import com.goormthon.hero_home.domain.sponsorshipboard.repository.SponsorshipBoardRepository;
import com.goormthon.hero_home.domain.sponsorshipstatus.converter.SponsorshipStatusConverter;
import com.goormthon.hero_home.domain.sponsorshipstatus.dto.SponsorshipStatusRequestDto;
import com.goormthon.hero_home.domain.sponsorshipstatus.dto.SponsorshipStatusResponseDto;
import com.goormthon.hero_home.domain.sponsorshipstatus.entity.SponsorshipStatus;
import com.goormthon.hero_home.domain.sponsorshipstatus.repository.SponsorshipStatusRepository;
import com.goormthon.hero_home.domain.user.entity.Role;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.repository.UserRepository;
import com.goormthon.hero_home.global.code.status.ErrorStatus;
import com.goormthon.hero_home.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorshipStatusService {

    private final SponsorshipStatusRepository sponsorshipStatusRepository;
    private final SponsorshipBoardRepository sponsorshipBoardRepository;
    private final UserRepository userRepository;

    public void donate(Authentication authentication, Long boardId,
                       SponsorshipStatusRequestDto.SponsorshipDonationInfo donationInfo) {

        User user = getUser(authentication);

        SponsorshipBoard sponsorshipBoard = sponsorshipBoardRepository.findById(boardId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BOARD_NOT_FOUNT));

        SponsorshipStatus sponsorshipStatus = SponsorshipStatus.builder()
                .sponsorshipAmount(donationInfo.getAmount())
                .sponsorshipBoard(sponsorshipBoard)
                .user(user)
                .isApproved(false) //관리자의 승인이 있어야 함
                .build();

        int newAmount = sponsorshipBoard.getCurrentAmount() + donationInfo.getAmount();

        SponsorshipBoard.builder().currentAmount(newAmount);

        sponsorshipStatusRepository.save(sponsorshipStatus);
    }

    public void approveDonation(Long statusId, Authentication authentication) {
        User user = getUser(authentication);
        checkAdminRole(user);

        SponsorshipStatus status = sponsorshipStatusRepository.findById(statusId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DONATION_STATUS_NOT_FOUND));

        SponsorshipStatus updatedStatus = SponsorshipStatus.builder()
                .id(status.getId())
                .sponsorshipBoard(status.getSponsorshipBoard())
                .user(status.getUser())
                .sponsorshipAmount(status.getSponsorshipAmount())
                .isApproved(true)
                .build();

        sponsorshipStatusRepository.save(updatedStatus);
    }

    public List<SponsorshipStatusResponseDto.SponsorshipDonationInfo> getAllSponsorshipStatus(Authentication authentication) {
        return sponsorshipStatusRepository.findAll()
                .stream().map(SponsorshipStatusConverter::toSponsorshipStatusInfo)
                .toList();
    }

    private User getUser(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return user;
    }

    private void checkAdminRole(User user) {
        if(user.getRole() != Role.ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
    }
}
