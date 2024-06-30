package com.guide.run.temp.member.service;

import com.guide.run.temp.member.entity.Member;
import com.guide.run.temp.member.repository.MemberRepository;
import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.Guide;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
import com.guide.run.user.repository.ArchiveDataRepository;
import com.guide.run.user.repository.GuideRepository;
import com.guide.run.user.repository.ViRepository;
import com.guide.run.user.repository.user.UserRepository;
import com.guide.run.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MoveService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final ArchiveDataRepository archiveDataRepository;
    private final GuideRepository guideRepository;
    private final ViRepository viRepository;
    private final UserService userService;
    public void move() {
        List<User> allUser = userRepository.findAll();
        for(User u: allUser){
            Optional<Member> member = memberRepository.findByPhoneNumber(u.getPhoneNumber());
            if(!member.isEmpty()){
                memberRepository.delete(member.get());
            }
        }
        memberRepository.flush();
        List<Member> allMember = memberRepository.findAll();
        for(Member m : allMember){
            ArchiveData archiveData = ArchiveData.builder()
                    .privateId(String.valueOf(m.getId()))
                    .hopePrefs("")
                    .portraitRights(false)
                    .privacy(false)
                    .runningPlace("")
                    .motive("")
                    .build();
            archiveDataRepository.save(archiveData);
            if(UserType.VI.getValue().equals(m.getType())) {
                User user = User.builder()
                        .userId(userService.getUUID())
                        .privateId(String.valueOf(m.getId()))
                        .name("미가입 VI")
                        .gender(m.getGender())
                        .recordDegree("E")
                        .type(UserType.VI)
                        .role(Role.ROLE_USER)
                        .competitionCnt(0)
                        .trainingCnt(0)
                        .build();
                userRepository.save(user);
                Vi vi = Vi.builder()
                        .guideName("")
                        .privateId(String.valueOf(m.getId()))
                        .isRunningExp(false)
                        .build();
                viRepository.save(vi);
            }else {
                User user = User.builder()
                        .userId(userService.getUUID())
                        .privateId(String.valueOf(m.getId()))
                        .gender(m.getGender())
                        .name("미가입 Guide")
                        .recordDegree("E")
                        .type(UserType.GUIDE)
                        .role(Role.ROLE_USER)
                        .competitionCnt(0)
                        .trainingCnt(0)
                        .build();
                userRepository.save(user);
                Guide guide = Guide.builder()
                        .guidingPace("")
                        .isGuideExp(false)
                        .viName("")
                        .viCount("")
                        .viRecord("")
                        .privateId(String.valueOf(m.getId()))
                        .build();
                guideRepository.save(guide);
            }
        }

    }
}
