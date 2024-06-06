package com.guide.run.user.service;

import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.s3.S3Uploader;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImgService {
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;

    @Transactional
    public String uploadProfile(String privateId, MultipartFile file) {
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        String imgUrl = null;
        try{
             imgUrl = s3Uploader.upload(file, "user");
             user.editImg(imgUrl);
            return imgUrl;
        }catch (Exception e){
            //예외처리 추가
        }

        return imgUrl;
    }

    @Transactional
    public void deleteProfile(String privateId){
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        String img = user.getImg();
        s3Uploader.deleteFile(img);
        user.editImg(null);
    }
}
