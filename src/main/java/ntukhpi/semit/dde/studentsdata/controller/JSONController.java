package ntukhpi.semit.dde.studentsdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ntukhpi.semit.dde.studentsdata.entity.AcademicGroup;
import ntukhpi.semit.dde.studentsdata.entity.Address;
import ntukhpi.semit.dde.studentsdata.entity.Person;
import ntukhpi.semit.dde.studentsdata.service.interf.AcademicGroupService;
import ntukhpi.semit.dde.studentsdata.service.interf.AddressService;
import ntukhpi.semit.dde.studentsdata.service.interf.PersonService;
import ntukhpi.semit.dde.studentsdata.utils.ContactMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class JSONController {
    @Autowired
    private AcademicGroupService academicGroupService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/groups/download_json")
    @ResponseBody
    public ResponseEntity<byte[]> downloadJSON(RedirectAttributes redirectAttributes) {
        try {
            // Retrieve all academic groups
            List<AcademicGroup> academicGroups = academicGroupService.getAllAcademicGroups();

            // Create a map to store group names and student counts
            Map<String, Integer> groupsData = new HashMap<>();

            // Populate the map with group names and corresponding student counts
            for (AcademicGroup group : academicGroups) {
                int studentCount = group.getStudentsList().size();
                groupsData.put(group.getGroupName(), studentCount);
            }

            String currentWorkingDir = System.getProperty("user.dir");
            String jsonFilePath = currentWorkingDir + "/resources/files/groups_data.json"; // Set your desired file path
            File jsonFile = new File(jsonFilePath);
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
            }

            // Serialize the map to JSON
            objectMapper.writeValue(jsonFile, groupsData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String encodedFileName = URLEncoder.encode(jsonFile.getName(), "UTF-8");
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setContentLength(jsonFile.length());
            return new ResponseEntity<>(org.apache.commons.io.FileUtils.readFileToByteArray(jsonFile), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            // You can return an error response
            String errorMessage = "Error generating JSON file: " + e.getMessage();
            return ResponseEntity.badRequest().body(errorMessage.getBytes(StandardCharsets.UTF_8));
        }
    }

}
