package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.entity.Image;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageRelation;
import com.ktc.togetherPet.model.entity.Report;
import com.ktc.togetherPet.repository.ImageRelationRepository;
import com.ktc.togetherPet.repository.ImageRepository;
import com.ktc.togetherPet.repository.ReportRepository;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final ReportRepository reportRepository;
    private final ImageRelationRepository imageRelationRepository;

    public ImageService(ImageRepository imageRepository, ReportRepository reportRepository, ImageRelationRepository imageRelationRepository) {
        this.imageRepository = imageRepository;
        this.reportRepository = reportRepository;
        this.imageRelationRepository = imageRelationRepository;
    }

    @Value("${image-folder-path}")
    private String FOLDER_PATH;

    // TODO: 매개변수로 path를 받아서 좀 더 유동적인 이미지 저장 방식을 구현할 수 있도록 변경해야할 듯
    // 지금 saveImage 메서드는 petId를 받아서 이미지를 저장하는 방식으로 구현되어 있음
    // saveImages 메서드는 reportId를 받아서 이미지를 저장하는 방식으로 구현되어 있음
    // 두 개의 기능이 중복되어있어 petId는 path/pet/ 에 저장되고 reportId는 path/report/ 에 저장되는 형태로 구현하는 게 좋을 것 같음

    public String saveImage(Long petId, MultipartFile petImage) throws IOException {
        File directory = new File(FOLDER_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String extension = getFileExtension(petImage.getOriginalFilename());
        String imageFileName = petId + extension; // petId만 사용하여 파일 이름 생성
        String imageFilePath = FOLDER_PATH + imageFileName;

        File file = new File(imageFilePath);
        petImage.transferTo(file);

        return imageFilePath;
    }

    public void saveImages(Long reportId, List<MultipartFile> files) throws IOException {

        File directory = new File(FOLDER_PATH + "/report/");

        if (!directory.exists()) {
            directory.mkdirs();
        }
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String extension = getFileExtension(file.getOriginalFilename());
            String imageFileName = reportId + "_" + i + extension;
            String imageFilePath = FOLDER_PATH + "/report/" + imageFileName;

            File imageFile = new File(imageFilePath);
            file.transferTo(imageFile);

            Image image = new Image(imageFilePath);
            Image savedImage = imageRepository.save(image);

            //todo: 이것도 지우고 QueryDSL로 report와 연관짓도록 하기
            Report report = reportRepository.findById(reportId)
                .orElseThrow(CustomException::reportNotFoundException);

            ImageRelation imageRelation = new ImageRelation(ImageEntityType.REPORT, savedImage);
            imageRelationRepository.save(imageRelation);
        }
    }

    public String getRepresentativeImageById(ImageEntityType entityType,Long Id) {
        List<ImageRelation> imageRelations = imageRelationRepository.findByImageEntityTypeAndEntityId(entityType, Id);

        return imageRelations.stream()
            .findFirst()
            .map(imageRelation -> imageRelation.getImage().getPath())
            .orElseThrow(CustomException::imageNotFoundException);
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
}
