package com.guide.run.user.service;

import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.exception.validation.img.ImgNotValidException;
import com.guide.run.global.s3.S3Uploader;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImgService {
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;

    @Transactional
    public String uploadProfile(String privateId, MultipartFile file) {
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new ImgNotValidException();
        }
        int idx = originalFilename.lastIndexOf('.');
        if (idx < 0) {
            throw new ImgNotValidException();
        }
        String imgformat = originalFilename.substring(idx + 1).toLowerCase();

        if (imgformat.equals("png") || imgformat.equals("jpg") || imgformat.equals("jpeg")) {
            try {
                String prevImg = user.getImg();
                String imgUrl = s3Uploader.upload(file, "user");
                user.editImg(imgUrl);
                if (prevImg != null && !prevImg.isBlank()) {
                    s3Uploader.deleteFile(prevImg);
                }
                return imgUrl;
            } catch (Exception e) {
                log.error("이미지 저장 실패 privateId={}: {}", privateId, e.getMessage(), e);
                throw new RuntimeException("이미지 저장 실패");
            }
        } else {
            throw new ImgNotValidException();
        }
    }

    @Transactional
    public void deleteProfile(String privateId){
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        String img = user.getImg();
        if (img == null || img.isBlank()) {
            return;
        }
        s3Uploader.deleteFile(img);
        user.editImg(null);
    }
}
