package vn.iotstar.service.impl;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.service.CloudinaryService;
import vn.iotstar.service.CloudinaryUploadResult;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public CloudinaryUploadResult upload(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Chưa chọn ảnh");

        String type = file.getContentType();
        if (type == null || !type.startsWith("image/"))
            throw new IllegalArgumentException("Chỉ cho phép file hình ảnh");

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                Map.of("folder", "shop/products")
            );
            return new CloudinaryUploadResult(
                String.valueOf(result.get("secure_url")),
                String.valueOf(result.get("public_id"))
            );
        } catch (Exception e) {
            System.err.println("Upload Cloudinary thất bại (kiểm tra API key): " + e.getMessage());
            // Fallback placeholder để vẫn lưu được sản phẩm nếu dùng key demo
            return new CloudinaryUploadResult(
                "/images/user.png",
                "local_placeholder_" + System.currentTimeMillis()
            );
        }
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) return;
        try {
            cloudinary.uploader().destroy(
                publicId, Map.of("resource_type", "image")
            );
        } catch (Exception e) {
            System.err.println("Xóa ảnh Cloudinary thất bại: " + e.getMessage());
        }
    }
}
