package api.shared.helpers;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.Map;

public class CloudinaryAPI {
    public static Cloudinary cloudinary = new com.cloudinary.Cloudinary(ObjectUtils.asMap(
            "cloud_name", "spotify-scalable",
            "api_key", "425999279194475",
            "api_secret", "hF61Z6VFjQnUtqCh38A7ZHpFgnA"));

    public static String upload(String pathName, String folder) {
        try {

            String[] pathSplit = pathName.split("/");

            String fileName = pathSplit[pathSplit.length - 1];


            Map params = ObjectUtils.asMap(
                    "public_id", folder + "/" + fileName,
                    "overwrite", true,
                    "resource_type", "raw"
            );
            Map uploadResult = cloudinary.uploader().upload(new File(pathName), params);

            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            return "Error";
        }
    }


    public static String upload(String pathName, String folder, String type) {
        try {

            String[] pathSplit = pathName.split("/");

            String fileName = pathSplit[pathSplit.length - 1];


            Map params = ObjectUtils.asMap(
                    "public_id", folder + "/" + fileName,
                    "overwrite", true,
                    "resource_type", type
            );
            Map uploadResult = cloudinary.uploader().upload(new File(pathName), params);

            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }

    }
    public static void delete(String url) {
        try {
            String[] urlSplit = url.split("/");
            String public_id = "";
            for (int i = 7; i < urlSplit.length; i++) {
                public_id += urlSplit[i] + "/";
            }
            public_id = public_id.substring(0,public_id.length()-1);
            Map params = ObjectUtils.asMap(
                    "resource_type", "raw"
            );

            cloudinary.uploader().destroy(public_id, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        System.out.println(cloudinary.url().transformation(new Transformation().audioFrequency(44100)).publicId("say_wahtttt/alone_again.mp3"));
        String s = upload("/Users/mohamedhooda/Downloads/compiler.pdf", "haaa", "auto");
//        System.out.println(s);
//        delete("https://res.cloudinary.com/spotify-scalable/raw/upload/v1619645063/song/3rd.txt");
    }
}
