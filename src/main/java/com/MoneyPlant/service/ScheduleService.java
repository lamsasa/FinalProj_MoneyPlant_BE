package com.MoneyPlant.service;

import com.MoneyPlant.dto.ScheduleDto;
import com.MoneyPlant.entity.*;
import com.MoneyPlant.repository.*;
import com.MoneyPlant.service.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import javax.transaction.Transactional;
import java.util.List;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import javax.security.auth.kerberos.KerberosKey;
//import javax.servlet.http.HttpServletRequest;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    // 캘린더 일정 생성
    @Transactional
    public boolean createSchedule(ScheduleDto scheduleDto, UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getId();
            scheduleDto.setUserId(userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            Schedule schedule = new Schedule();
            schedule.setUser(user);
            schedule.setCalId(scheduleDto.getCalId());
            schedule.setScName(scheduleDto.getScName());
            schedule.setScDate(scheduleDto.getScDate());
            schedule.setScBudget(scheduleDto.getScBudget());

            scheduleRepository.save(schedule);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 마이페이지 나의 일정 수정

    // 마이페이지 나의 일정 삭제

}
